package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.util.FecfsAnimations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

@Environment(EnvType.CLIENT)
public class WebSwinging {
    public static boolean isSwinging = false;
    private static int swingTime = 0;
    public static Vec3d anchorPoint = null;
    private static final double MAX_SWING_DISTANCE = 150;
    private static ModelPart webLineModel;
    private static final EntityModelLayer WEB_LINE_MODEL_LAYER = new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main");
    private static double initialWebLength = 0;
    private static final double GRAVITY = -0.08;
    private static final double SPRING_CONSTANT = 0.05;
    private static final double DAMPING_COEFFICIENT = 0.000125;
    private static Vec3d initialToPlayer = null;
    public static boolean canPlayAnimation = false;
    private static Vec3d swingPlaneNormal = null;
    public static Arm swingHand(PlayerEntity player) {
        return player.getMainArm();
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebSwinging::onClientTick);
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                onRenderWorld(context, client.player, context.tickDelta());
            }});
        EntityModelLayerRegistry.registerModelLayer(WEB_LINE_MODEL_LAYER, WebLineModel::getTexturedModelData);
    }

    public static boolean canSwing(PlayerEntity player) {
        return HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) && !player.getAbilities().flying &&
                player.isAlive() && !player.isTouchingWater();
    }

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if(anchorPoint == null) return;
        if (isSwinging) {
            swing(client.player);
            client.player.sendMessage(Text.literal("Angle: "+getSwingingAngle(client.player.getPos(), anchorPoint)), true);
        }
    }

    public static BlockHitResult hitResult(PlayerEntity player) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = start.add(player.getRotationVector().multiply(MAX_SWING_DISTANCE));
        return player.getWorld().raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player));
    }

    public static void startSwing(PlayerEntity player) {
        BlockHitResult hitRes = hitResult(player);
        if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK && swingHand(player) != null) {
            anchorPoint = hitRes.getPos();
            isSwinging = true;
            swingTime = 0;
            initialWebLength = Math.min(anchorPoint.subtract(player.getPos()).length(), 120.0);
            player.setNoDrag(true);

            // Vector from the anchor point to the player's position at the start of the swing
            initialToPlayer = player.getPos().subtract(anchorPoint).normalize();

            // Calculate the normal vector of the swing plane based on the initial direction and the vertical axis
            swingPlaneNormal = initialToPlayer.crossProduct(new Vec3d(0, -1, 0)).normalize();
        }
    }


    public static void swing(PlayerEntity player) {
        if (anchorPoint == null || !canSwing(player)) {
            stopSwinging(player);
            return;
        }

        swingTime++;
        if (player.isOnGround()) {
            player.setNoDrag(false);
            return;
        } else {
            player.setNoDrag(true);
        }

        if (swingTime > 400) {
            stopSwinging(player);
        }

        if(anchorPoint != null) {
            Vec3d toAnchor = anchorPoint.subtract(player.getPos());
            Vec3d acceleration = new Vec3d(0, GRAVITY, 0);
            double stretch = toAnchor.length() - initialWebLength;

            if (stretch > 0) {
                Vec3d springForce = toAnchor.normalize().multiply(SPRING_CONSTANT * stretch);
                acceleration = acceleration.add(springForce);
            }
            if (stretch < 0) {
                Vec3d additionalDamping = player.getVelocity().multiply(-DAMPING_COEFFICIENT * 2);
                acceleration = acceleration.add(additionalDamping);
            }
            Vec3d dampingForce = player.getVelocity().multiply(-DAMPING_COEFFICIENT);
            acceleration = acceleration.add(dampingForce);
            double minDistance = 2;
            if (toAnchor.length() < minDistance) {
                double compression = minDistance - toAnchor.length();
                Vec3d repulsiveForce = toAnchor.normalize().multiply(-SPRING_CONSTANT * compression * 2);
                acceleration = acceleration.add(repulsiveForce);
            }

            Vec3d newVelocity = player.getVelocity().add(acceleration);
            double maxSpeed = 3;
            if (newVelocity.length() > maxSpeed) {
                newVelocity = newVelocity.normalize().multiply(maxSpeed);
            }

            player.setVelocity(newVelocity);
        }
    }



    public static void stopSwinging(PlayerEntity player) {
        isSwinging = false;
        anchorPoint = null;
        swingTime = 0;
        player.setNoDrag(false);
        initialToPlayer = null;
        swingPlaneNormal = null;
    }


    public static void boost(PlayerEntity player) {
        if (!isSwinging || anchorPoint == null) return;
        Vec3d boostedVelocity = player.getVelocity().multiply(1.75).add(new Vec3d(0, 1.4, 0));
        player.setVelocity(boostedVelocity);
    }


    @Environment(EnvType.CLIENT)
    public static void onRenderWorld(WorldRenderContext context, PlayerEntity player, float tickDelta) {
        renderWebLine(context.matrixStack(), context.consumers(), player, tickDelta);
    }

    @Environment(EnvType.CLIENT)
    public static void renderWebLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, float tickDelta) {
        if (player == null || !isSwinging || anchorPoint == null) return;
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WEB_LINE_MODEL_LAYER);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d webStartPos = getWebStartPosition(player, tickDelta);
        if (webStartPos == null) return;
        int segmentCount = (int) Math.ceil(webStartPos.distanceTo(anchorPoint));
        segmentCount = Math.max(segmentCount, 8);
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d controlPoint = calculateControlPoint(webStartPos, anchorPoint);
        float modelRotationOffset = -((float) Math.PI / 2);

        for (int i = 1; i < segmentCount; i++) {
            Vec3d startPos = quadraticBezier(webStartPos, controlPoint, anchorPoint, (double) i / segmentCount);
            Vec3d endPos = quadraticBezier(webStartPos, controlPoint, anchorPoint, (double) (i + 1) / segmentCount);
            Vec3d segmentVec = endPos.subtract(startPos);
            float segmentLengthF = (float) segmentVec.length();
            if (segmentLengthF < 1e-6) {
                continue;
            }
            Vec3d dir = segmentVec.normalize();
            double dx = dir.x;
            double dy = dir.y;
            double dz = dir.z;
            float yaw = (float) Math.atan2(-dx, dz);
            float pitch = (float) Math.asin(-dy);
            matrices.push();
            matrices.translate(startPos.x - cameraPos.x, startPos.y - cameraPos.y, startPos.z - cameraPos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch + modelRotationOffset));
            matrices.scale(1.0F, segmentLengthF, 1.0F);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(
                    new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_line.png")));
            webLineModel.render(matrices, vertexConsumer, light(player), OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }
    }

    public static Vec3d getWebStartPosition(PlayerEntity player, float tickDelta) {
        if (player != null) {
            if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
                return getFirstPersonHandPosition(player);
            } else {
                Vec3d playerPos = player.getLerpedPos(tickDelta);
                double headHeight = player.getStandingEyeHeight() + 0.5;
                return new Vec3d(playerPos.x, playerPos.y + headHeight, playerPos.z);
            }
        }
        return null;
    }

    public static Vec3d getFirstPersonHandPosition(PlayerEntity player) {
        return MinecraftClient.getInstance().gameRenderer.getCamera().getPos().add(player.getMainArm() == Arm.RIGHT ? 0.3 : -0.3, -0.25, -0.5);
    }

    private static Vec3d calculateControlPoint(Vec3d start, Vec3d end) {
        return start.lerp(end, 0.5).add(0, -Math.min(5, start.distanceTo(end) * 0.2), 0);
    }

    private static Vec3d quadraticBezier(Vec3d p0, Vec3d p1, Vec3d p2, double t) {
        return new Vec3d(
                (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x,
                (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y,
                (1 - t) * (1 - t) * p0.z + 2 * (1 - t) * t * p1.z + t * t * p2.z);
    }

    @Environment(EnvType.CLIENT)
    public static class WebLineModel {

        private final ModelPart bone;

        public WebLineModel(ModelPart root) {
            this.bone = root.getChild("bone");
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(-0.25F))
                    .uv(9, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(0)), ModelTransform.of(0.0F, 15.0F, 0.0F, -(((float) Math.PI)), 0f, 0));
            return TexturedModelData.of(modelData, 32, 32);
        }

        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
            bone.render(matrices, vertices, light, overlay);
        }
    }

    private static int light(PlayerEntity player) {
        if(player.getWorld().isNight()) {
            return LightmapTextureManager.MAX_LIGHT_COORDINATE / 100;
        } else {
            return LightmapTextureManager.MAX_LIGHT_COORDINATE;
        }
    }
    public static double getSwingingAngle(Vec3d playerPos, Vec3d anchorPos) {
        if (playerPos == null || anchorPos == null || swingPlaneNormal == null || initialToPlayer == null) {
            return 0;
        }
        Vec3d toPlayer = playerPos.subtract(anchorPos).normalize();
        Vec3d toPlayerProjected = toPlayer.subtract(swingPlaneNormal.multiply(toPlayer.dotProduct(swingPlaneNormal))).normalize();
        Vec3d initialToPlayerProjected = initialToPlayer.subtract(swingPlaneNormal.multiply(initialToPlayer.dotProduct(swingPlaneNormal))).normalize();
        double angleRadians = Math.acos(initialToPlayerProjected.dotProduct(toPlayerProjected));
        double angleSign = swingPlaneNormal.dotProduct(initialToPlayerProjected.crossProduct(toPlayerProjected));
        double angleDegrees = Math.toDegrees(angleRadians);
        if (angleSign < 0) {
            angleDegrees = -angleDegrees;
        }
        return angleDegrees;
    }




}
