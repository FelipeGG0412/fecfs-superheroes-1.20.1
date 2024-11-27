package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.util.FecfsAnimations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.TextureMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
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
    private static boolean hasDamaged = false;
    public static Vec3d anchorPoint = null;
    private static ModelPart webLineModel;
    private static double initialWebLength = 0;
    private static final double SPRING_CONSTANT = 0.05;
    private static final double DAMPING_COEFFICIENT = 0.000125;
    private static Vec3d initialToPlayer = null;
    private static Vec3d swingPlaneNormal = null;
    public static int swingDuration = 0;
    public static Arm swingHand(PlayerEntity player) {
        return player.getMainArm();
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebSwinging::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && isSwinging) {
                FecfsAnimations.playSpiderManSwingingAnimations(client.player);
            }});
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                onRenderWorld(context, client.player, context.tickDelta());
            }});
        EntityModelLayerRegistry.registerModelLayer(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"), WebLineModel::getTexturedModelData);
    }
    public static boolean canSwing(PlayerEntity player) {
        return (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))&& !player.getAbilities().flying &&
                player.isAlive() && !player.isTouchingWater();
    }
    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if(anchorPoint == null) return;
        if (isSwinging) {
            swing(client.player);
            client.player.sendMessage(Text.literal("Velocity: "+client.player.getVelocity()), true);
            FecfsAnimations.playSpiderManSwingingAnimations(client.player);
            if(!hasDamaged && isSwinging && client.player.horizontalCollision) {
                boolean damagePositiveNegativeX = (client.player.getVelocity().x > 1.25 || client.player.getVelocity().x < -1.25);
                boolean damagePositiveNegativeZ = (client.player.getVelocity().z > 1.25 || client.player.getVelocity().z < -1.25);
                if(HeroUtil.isWearingWebShooter(client.player) && (damagePositiveNegativeX || damagePositiveNegativeZ)) {
                    client.player.damage(client.player.getDamageSources().flyIntoWall(), (damage(client.player)));
                    hasDamaged = true;
                }
            }
        }
    }
    public static BlockHitResult hitResult(PlayerEntity player) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = start.add(player.getRotationVector().multiply(150));
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
            initialWebLength = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) ? Math
                    .min(anchorPoint.subtract(player.getPos()).length(), 120.0) :  Math.min(anchorPoint.subtract(player.getPos()).length(), 70.0);;
            player.setNoDrag(true);
            initialToPlayer = player.getPos().subtract(anchorPoint).normalize();
            swingPlaneNormal = initialToPlayer.crossProduct(new Vec3d(0, -1, 0)).normalize();
            swingDuration = 0;
            hasDamaged = false;
        }
    }
    public static void swing(PlayerEntity player) {
        if (anchorPoint == null || !canSwing(player)) {
            stopSwinging(player);
            return;
        }
        swingDuration++;
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
            Vec3d acceleration = new Vec3d(0, -0.08, 0);
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
            double maxSpeed = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) ? 3 : 2.3;
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
        swingDuration = 0;
    }
    public static void boost(PlayerEntity player) {
        if (!isSwinging || anchorPoint == null) return;
        Vec3d boostedVelocity = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) ? player.getVelocity().multiply(1.75)
                .add(new Vec3d(0, 1.25, 0)) : player.getVelocity().multiply(1.45).add(new Vec3d(0, 1.1, 0));
        player.setVelocity(boostedVelocity);
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
    private static float damage(PlayerEntity player) {
        float positiveX = player.getVelocity().x > 1.25 ? (float) player.getVelocity().x : 0;
        float negativeX = player.getVelocity().x < -1.25 ? (float) player.getVelocity().x : 0;
        float positiveZ = player.getVelocity().z > 1.25 ? (float) player.getVelocity().z : 0;
        float negativeZ = player.getVelocity().z < -1.25 ? (float) player.getVelocity().z : 0;

        if(player.getVelocity().x > 1.25) {
            return (positiveX * 12);
        } else if(player.getVelocity().x < -1.25) {
            return (negativeX * (-12));
        }

        if(player.getVelocity().z > 1.25) {
            return (positiveZ * 12);
        } else if(player.getVelocity().z < -1.25) {
            return (negativeZ * (-12));
        }
        else {return 0;}
    }

    @Environment(EnvType.CLIENT)
    public static void onRenderWorld(WorldRenderContext context, PlayerEntity player, float tickDelta) {
        renderWebLine(context.matrixStack(), context.consumers(), player, tickDelta);
    }
    @Environment(EnvType.CLIENT)
    public static void renderWebLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, float tickDelta) {
        if (player == null || !isSwinging || anchorPoint == null) return;
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
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
    private static int light(PlayerEntity player) {
        return player.getWorld().isNight() ? LightmapTextureManager.MAX_LIGHT_COORDINATE / 200 : LightmapTextureManager.MAX_LIGHT_COORDINATE;
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
}
