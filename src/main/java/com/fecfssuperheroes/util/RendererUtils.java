package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.client.model.WebHitModel;
import com.fecfssuperheroes.client.web.WebHit;
import com.fecfssuperheroes.client.web.WebLine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RendererUtils {
    //Web line rendering
    private static final List<WebLine> activeWebLines = new ArrayList<>();
    public static double sag;
    public static void addWebLine(Vec3d startPos, Vec3d anchorPoint) {
        activeWebLines.add(new WebLine(startPos, anchorPoint));
    }
    private static ModelPart webLineModel = null;
    private static final Identifier WEB_TEXTURE = new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_line.png");
    public static void renderWebLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, Vec3d anchorPoint, Direction anchorFacing, float tickDelta, boolean useBezierCurve) {
        if (player == null || anchorPoint == null || anchorFacing == null) return;
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader()
                    .getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
        }
        Vec3d webStartPos = webStartPosition(player, tickDelta);
        if (webStartPos == null) return;
        Vec3d adjustedAnchorPoint = adjustHitPosition(anchorPoint, anchorFacing);
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        int segmentCount = (int) Math.ceil(webStartPos.distanceTo(adjustedAnchorPoint));
        segmentCount = Math.max(segmentCount, 16);
        Vec3d controlPoint = useBezierCurve ? controlPoint(webStartPos, adjustedAnchorPoint, player) : null;
        for (int i = 2; i < segmentCount + 2; i++) {
            double tStart = (double) i / segmentCount;
            double tEnd = (double) (i + 1) / segmentCount;
            Vec3d startPosSegment = useBezierCurve
                    ? quadraticBezier(webStartPos, controlPoint, adjustedAnchorPoint, tStart)
                    : interpol(webStartPos, adjustedAnchorPoint, tStart);
            Vec3d endPosSegment = useBezierCurve
                    ? quadraticBezier(webStartPos, controlPoint, adjustedAnchorPoint, tEnd)
                    : interpol(webStartPos, adjustedAnchorPoint, tEnd);

            renderSegment(matrices, vertexConsumers, cameraPos, startPosSegment, endPosSegment);
        }
    }
    private static void renderSegment(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d startPos, Vec3d endPos) {
        if (webLineModel == null) return;
        Vec3d segmentVec = endPos.subtract(startPos);
        float segmentLength = (float) segmentVec.length();
        if (segmentLength < 1e-6) return;
        Vec3d dir = segmentVec.normalize();
        float yaw = (float) Math.atan2(-dir.x, dir.z);
        float pitch = (float) Math.asin(-dir.y);
        float modelRotationOffset = -((float) Math.PI / 2);
        matrices.push();
        matrices.translate(startPos.x - cameraPos.x, startPos.y - cameraPos.y, startPos.z - cameraPos.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch + modelRotationOffset));
        matrices.scale(1.0F, segmentLength, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WEB_TEXTURE));
        webLineModel.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 0.9f); // Slight alpha for realism
        matrices.pop();
    }
    private static double webFall(long elapsedTime) {
        double maxSag = 5.0;
        double sagPerSecond = 1.0;
        double sag = (elapsedTime / 1000.0) * sagPerSecond;
        return Math.min(sag, maxSag);
    }
    private static Vec3d interpol(Vec3d start, Vec3d end, double t) {return start.lerp(end, t);}
    public static Vec3d webStartPosition(PlayerEntity player, float tickDelta) {
        if (player != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
            Arm swingArm = WebSwing.currentSwingArm != null ? WebSwing.currentSwingArm : player.getMainArm();
            boolean isRightHanded = (swingArm == Arm.RIGHT);
            float handSideMultiplier = isRightHanded ? 1.0F : -1.0F;

            Vec3d playerPos = player.getLerpedPos(tickDelta);
            float yaw = interpolate(player.prevYaw, player.getYaw(), tickDelta);

            double xOffset = handSideMultiplier * -MathHelper.sin((float) Math.toRadians(yaw)) * 0.35;
            double zOffset = handSideMultiplier * MathHelper.cos((float) Math.toRadians(yaw)) * 0.35;
            double yOffset = player.getStandingEyeHeight() - 0.5;

            Vec3d handPosition = playerPos.add(xOffset, yOffset, zOffset);

            // Adjust for first-person perspective
            if (isFirstPerson && player == client.player) {
                Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                Vec3d cameraDir = player.getRotationVec(tickDelta);

                double xHandOffset = isRightHanded ? 0.3 : -0.3;
                Vec3d handOffset = new Vec3d(xHandOffset, -0.2, 0.0);

                // Rotate the offset based on the player's pitch and yaw
                Vec3d rotatedOffset = handOffset.rotateX((float) Math.toRadians(player.getPitch()))
                        .rotateY((float) Math.toRadians(-player.getYaw()));

                handPosition = cameraPos.add(rotatedOffset);
            }

            return handPosition;
        }
        return null;
    }
    private static float interpolate(float prev, float current, float delta) {
        return prev + (current - prev) * delta;
    }
    private static Vec3d controlPoint(Vec3d start, Vec3d end, PlayerEntity player) {
        double distance = start.distanceTo(end);
        double sagAmount;
        if (player.isOnGround() || player.isClimbing() || distance < 5.0) {
            sagAmount = 0.0;
        } else {
            sagAmount = Math.min(5.0, distance * 0.1);
        }
        Vec3d midPoint = start.lerp(end, 0.5);
        return midPoint.add(0, -sagAmount, 0);
    }
    private static Vec3d quadraticBezier(Vec3d p0, Vec3d p1, Vec3d p2, double t) {
        return new Vec3d(
                (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x,
                (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y,
                (1 - t) * (1 - t) * p0.z + 2 * (1 - t) * t * p1.z + t * t * p2.z);
    }
    public static void renderUsedWebLines(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        if (activeWebLines.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        Iterator<WebLine> iterator = activeWebLines.iterator();

        while (iterator.hasNext()) {
            WebLine webLine = iterator.next();
            long elapsed = currentTime - webLine.startTime;
            sag = webFall(elapsed);
            if (elapsed > 5000) {
                iterator.remove();
                continue;
            }
            float alpha = 1.0f;
            if (elapsed > 4000) {
                alpha = 1.0f - ((float)(elapsed - 4000) / 1000.0f);
            }


            renderWebLineInstance(matrices, vertexConsumers, webLine, alpha);
        }
    }
    private static Vec3d controlPointSag(Vec3d start, Vec3d end, double sag) {
        Vec3d midPoint = start.lerp(end, 0.5);
        return midPoint.add(0, -sag, 0);
    }
    private static void renderWebLineInstance(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WebLine webLine, float alpha) {
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader()
                    .getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
        }
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d startPos = webLine.startPos;
        Vec3d endPos = webLine.anchorPoint;
        long elapsedTime = System.currentTimeMillis() - webLine.startTime;
        double sag = webFall(elapsedTime);
        Vec3d controlPoint = controlPointSag(startPos, endPos, sag);
        int segmentCount = (int) Math.ceil(startPos.distanceTo(endPos));
        segmentCount = Math.max(segmentCount, 16);
        for (int i = 0; i < segmentCount; i++) {
            double tStart = (double) i / segmentCount;
            double tEnd = (double) (i + 1) / segmentCount;
            Vec3d segmentStart = quadraticBezier(startPos, controlPoint, endPos, tStart);
            Vec3d segmentEnd = quadraticBezier(startPos, controlPoint, endPos, tEnd);

            renderSegWithFade(matrices, vertexConsumers, cameraPos, segmentStart, segmentEnd, alpha);
        }
    }
    private static void renderSegWithFade(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d startPos, Vec3d endPos, float alpha) {
        Vec3d segmentVec = endPos.subtract(startPos);
        float segmentLength = (float) segmentVec.length();
        if (segmentLength < 1e-6) return;
        matrices.push();
        matrices.translate(startPos.x - cameraPos.x, startPos.y - cameraPos.y, startPos.z - cameraPos.z);
        Vec3d dir = segmentVec.normalize();
        float yaw = (float) Math.atan2(-dir.x, dir.z);
        float pitch = (float) Math.asin(-dir.y);
        float modelRotationOffset = -((float) Math.PI / 2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch + modelRotationOffset));
        matrices.scale(1.0F, segmentLength, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WEB_TEXTURE));
        webLineModel.render(matrices, vertexConsumer, LightmapTextureManager.pack(0, 15), OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, alpha);
        matrices.pop();
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
            ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(-0.75F))
                    .uv(9, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(-0.6F)), ModelTransform.pivot(0.0F, 15.0F, 0.0F));
            return TexturedModelData.of(modelData, 32, 32);
        }
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
            bone.render(matrices, vertices, light, overlay);
        }
    }

    //Web hit rendering
    private static final int MAX_WEB_HITS = 32;
    private static final WebHit[] activeWebHits = new WebHit[MAX_WEB_HITS];
    private static int webHitIndex = 0;
    private static final Identifier WEB_HIT_TEXTURE = new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_hit.png");
    private static WebHitModel webHitModel = null;
    public static void renderWebHits(WorldRenderContext context) {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < activeWebHits.length; i++) {
            WebHit webHit = activeWebHits[i];
            if (webHit == null) continue;

            long elapsed = currentTime - webHit.startTime;
            if (elapsed > 5000) {
                activeWebHits[i] = null;
                continue;
            }
            float alpha = 1.0f;
            if (elapsed > 4000) {
                alpha = 1.0f - ((float) (elapsed - 4000) / 1000.0f);
            }
            renderWebHitInstance(context, webHit, alpha);
        }
    }
    public static void showWebHit(Vec3d anchorPoint, Direction facing) {
        Vec3d adjustedPosition = adjustHitPosition(anchorPoint, facing);
        WebHit newWebHit = new WebHit(adjustedPosition, facing);
        for (int i = 0; i < activeWebHits.length; i++) {
            WebHit existingWebHit = activeWebHits[i];
            if (existingWebHit != null && existingWebHit.facing == facing &&
                    existingWebHit.position.squaredDistanceTo(adjustedPosition) < 0.0001) {
                return;
            }
        }
        activeWebHits[webHitIndex++] = newWebHit;
        if (webHitIndex >= MAX_WEB_HITS) webHitIndex = 0;
    }
    private static Vec3d adjustHitPosition(Vec3d anchorPoint, Direction facing) {
        Vec3d facingVector = Vec3d.of(facing.getVector());
        double depthOffset = 0.1;
        return anchorPoint.subtract(facingVector.multiply(depthOffset));
    }
    private static void renderWebHitInstance(WorldRenderContext context, WebHit webHit, float alpha) {
        if (webHitModel == null) {
            EntityModelLayer webHitLayer = new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_hit"), "main");
            webHitModel = new WebHitModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(webHitLayer));
        }
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider vertexConsumers = context.consumers();
        Camera camera = context.camera();
        matrices.push();
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - webHit.startTime;
        float scale = Math.min(1.0f, elapsed / 250.0f);
        scale = Math.max(0.01f, scale);
        Vec3d facingVector = Vec3d.of(webHit.facing.getVector());
        double depthOffset = -0.2;
        Vec3d adjustedPosition = webHit.position.subtract(facingVector.multiply(depthOffset));
        matrices.translate(
                adjustedPosition.x - camera.getPos().x,
                adjustedPosition.y - camera.getPos().y,
                adjustedPosition.z - camera.getPos().z
        );
        applyFacingRotation(matrices, webHit.facing);
        matrices.scale(scale, scale, scale);
        World world = context.world();
        Vec3i pos = new Vec3i(((int) webHit.position.x), ((int) webHit.position.y),( (int) webHit.position.z));
        int light = world.isNight() ? LightmapTextureManager.MAX_LIGHT_COORDINATE /3500 : LightmapTextureManager.MAX_LIGHT_COORDINATE;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WEB_HIT_TEXTURE));
        webHitModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, alpha);
        matrices.pop();
    }
    private static void applyFacingRotation(MatrixStack matrices, Direction facing) {
        switch (facing) {
            case NORTH -> {}
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case UP -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            case DOWN -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
        }
    }

    //Armor mixin visibility methods
    public static void headVisibility(AbstractClientPlayerEntity player, float scale, PlayerEntityModel<AbstractClientPlayerEntity> model) {
        if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(FecfsTags.Items.FULLSUIT)) {
            model.hat.visible = false;
            model.head.xScale = scale;
            model.head.yScale = scale;
            model.head.zScale = scale;
        } else {
            model.hat.visible = true;
            model.head.xScale = (float) 1;
            model.head.yScale = (float) 1;
            model.head.zScale = (float) 1;
        }
    }
    public static void chestVisibility(AbstractClientPlayerEntity player, float scale, PlayerEntityModel<AbstractClientPlayerEntity> model) {
        if (player.getEquippedStack(EquipmentSlot.CHEST).isIn(FecfsTags.Items.FULLSUIT)) {
            model.jacket.visible = false;
            model.body.xScale = scale;
            model.body.yScale = scale;
            model.body.zScale = scale;
            model.rightSleeve.visible = false;
            model.leftSleeve.visible = false;
            model.rightArm.xScale = scale;
            model.rightArm.yScale = scale;
            model.rightArm.zScale = scale;
            model.leftArm.xScale = scale;
            model.leftArm.yScale = scale;
            model.leftArm.zScale = scale;
        } else {
            model.jacket.visible = true;
            model.body.xScale = (float) 1;
            model.body.yScale = (float) 1;
            model.body.zScale = (float) 1;
            model.rightSleeve.visible = true;
            model.leftSleeve.visible = true;
            model.rightArm.xScale = (float) 1;
            model.rightArm.yScale = (float) 1;
            model.rightArm.zScale = (float) 1;
            model.leftArm.xScale = (float) 1;
            model.leftArm.yScale = (float) 1;
            model.leftArm.zScale = (float) 1;
        }
    }
    public static void legVisibility(AbstractClientPlayerEntity player, float scale, PlayerEntityModel<AbstractClientPlayerEntity> model)  {
        float nX = 1;
        float nY = 1;
        float nZ = 1;
        if(player.getEquippedStack(EquipmentSlot.LEGS).isIn(FecfsTags.Items.FULLSUIT)) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;
            model.leftLeg.xScale = scale;
            model.leftLeg.yScale = scale;
            model.leftLeg.zScale = scale;
            model.rightLeg.xScale = scale;
            model.rightLeg.yScale = scale;
            model.rightLeg.zScale = scale;
        } else {
            model.leftPants.visible = true;
            model.rightPants.visible = true;
            model.leftLeg.xScale = nX;
            model.leftLeg.yScale = nY;
            model.leftLeg.zScale = nZ;
            model.rightLeg.xScale = nX;
            model.rightLeg.yScale = nY;
            model.rightLeg.zScale = nZ;
        }
    }

}