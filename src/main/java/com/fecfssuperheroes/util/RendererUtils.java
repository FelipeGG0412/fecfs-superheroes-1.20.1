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
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
    public static void renderWebLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, Vec3d anchorPoint, float tickDelta, boolean useBezierCurve) {
        if (player == null || anchorPoint == null) return;
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader()
                    .getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
        }
        Vec3d webStartPos = getWebStartPosition(player, tickDelta);
        if (webStartPos == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        int segmentCount = (int) Math.ceil(webStartPos.distanceTo(anchorPoint));
        segmentCount = Math.max(segmentCount, 8);
        Vec3d controlPoint = null;
        if (useBezierCurve) {
            controlPoint = calculateControlPoint(webStartPos, anchorPoint, player);
        }
        for (int i = 2; i < segmentCount + 2; i++) {
            double tStart = (double) i / segmentCount;
            double tEnd = (double) (i + 1) / segmentCount;
            Vec3d startPosSegment;
            Vec3d endPosSegment;
            if (useBezierCurve) {
                startPosSegment = quadraticBezier(webStartPos, controlPoint, anchorPoint, tStart);
                endPosSegment = quadraticBezier(webStartPos, controlPoint, anchorPoint, tEnd);
            } else {
                startPosSegment = linearInterpolation(webStartPos, anchorPoint, tStart);
                endPosSegment = linearInterpolation(webStartPos, anchorPoint, tEnd);
            }
            renderSegment(matrices, vertexConsumers, cameraPos, startPosSegment, endPosSegment);
        }
    }
    private static Vec3d linearInterpolation(Vec3d start, Vec3d end, double t) {return start.lerp(end, t);}
    public static Vec3d getWebStartPosition(PlayerEntity player, float tickDelta) {
        if (player != null) {
            Vec3d playerPos = player.getLerpedPos(tickDelta);
            if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
                Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
                Vec3d cameraDir = player.getRotationVec(tickDelta);
                double distanceFromCamera = 0.5;

                return cameraPos.add(cameraDir.multiply(distanceFromCamera)).add(0.75, -0.2, 0);
            } else {
                double yOffset = player.getStandingEyeHeight() + 0.2;
                return new Vec3d(playerPos.x, playerPos.y + yOffset, playerPos.z);
            }
        }
        return null;
    }
    private static Vec3d calculateControlPoint(Vec3d start, Vec3d end, PlayerEntity player) {
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
    private static void renderSegment(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d startPos, Vec3d endPos) {
        if (webLineModel == null) return;

        Vec3d segmentVec = endPos.subtract(startPos);
        float segmentLengthF = (float) segmentVec.length();
        if (segmentLengthF < 1e-6) return;
        float modelRotationOffset = -((float) Math.PI / 2);
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
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(WEB_TEXTURE));
        webLineModel.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
    public static void renderPastWebLines(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        if (activeWebLines.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        Iterator<WebLine> iterator = activeWebLines.iterator();

        while (iterator.hasNext()) {
            WebLine webLine = iterator.next();
            long elapsed = currentTime - webLine.startTime;
            sag = calculateSag(elapsed);
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
    private static Vec3d calculateControlPointWithSag(Vec3d start, Vec3d end, double sag) {
        Vec3d midPoint = start.lerp(end, 0.5);
        return midPoint.add(0, -sag, 0);
    }
    private static double calculateSag(long elapsedTime) {
        double maxSag = 25.0;
        double normalizedTime = (double) elapsedTime / 5000.0;
        double sag = maxSag * (1 - Math.exp(-3 * normalizedTime));
        return Math.min(sag, maxSag);
    }

    private static void renderWebLineInstance(MatrixStack matrices, VertexConsumerProvider vertexConsumers, WebLine webLine, float alpha) {
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
        }
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d startPos = webLine.startPos;
        Vec3d endPos = webLine.anchorPoint;
        int segmentCount = (int) Math.ceil(startPos.distanceTo(endPos));
        segmentCount = Math.max(segmentCount, 8);
        Vec3d controlPoint = calculateControlPointWithSag(startPos, endPos, sag);

        for (int i = 2; i < segmentCount; i++) {
            double tStart = (double) i / segmentCount;
            double tEnd = (double) (i + 1) / segmentCount;

            Vec3d segmentStart = quadraticBezier(startPos, controlPoint, endPos, tStart);
            Vec3d segmentEnd = quadraticBezier(startPos, controlPoint, endPos, tEnd);

            renderSegmentWithAlpha(matrices, vertexConsumers, cameraPos, segmentStart, segmentEnd, alpha);
        }
    }
    private static void renderSegmentWithAlpha(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, Vec3d startPos, Vec3d endPos, float alpha) {
        if (MinecraftClient.getInstance().player == null) return;
        Vec3d segmentVec = endPos.subtract(startPos);
        float segmentLength = (float) segmentVec.length();
        if (segmentLength < 1e-6) return;
        matrices.push();
        matrices.translate(startPos.x - cameraPos.x, startPos.y - cameraPos.y, startPos.z - cameraPos.z);
        Vec3d dir = segmentVec.normalize();
        double dx = dir.x;
        double dy = dir.y;
        double dz = dir.z;
        float modelRotationOffset = -((float) Math.PI / 2);
        float yaw = (float) Math.atan2(-dx, dz);
        float pitch = (float) Math.asin(-dy);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch + modelRotationOffset));
        matrices.scale(1.0F, segmentLength, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WEB_TEXTURE));
        webLineModel.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, alpha);
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
    public static void showWebHit(Vec3d anchorPoint, Direction facing) {
        activeWebHits[webHitIndex++] = new WebHit(anchorPoint, facing);
        if (webHitIndex >= MAX_WEB_HITS) webHitIndex = 0;
    }
    public static void renderWebHits(WorldRenderContext context) {
        long currentTime = System.currentTimeMillis();
        for (WebHit webHit : activeWebHits) {
            if (webHit == null) {
                continue;
            }
            long elapsed = currentTime - webHit.startTime;
            if (elapsed > 5000) {
                continue;
            }
            float alpha = 1.0f;
            if (elapsed > 4000) {
                alpha = 1.0f - ((elapsed - 3000) / 1000.0f);
            }
            renderWebHitInstance(context, webHit, alpha);
        }
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

        Vec3d offset = Vec3d.of(webHit.facing.getVector()).multiply(0.1);
        matrices.translate(
                webHit.position.x - camera.getPos().x + offset.x,
                webHit.position.y - camera.getPos().y + offset.y - (WebSwing.isSwinging ? 2 : 1),
                webHit.position.z - camera.getPos().z + offset.z
        );

        switch (webHit.facing) {
            case NORTH:
                break;
            case SOUTH:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                break;
            case EAST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                break;
            case WEST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                break;
            case UP:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                break;
            case DOWN:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                break;
            default:
                break;
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(WEB_HIT_TEXTURE));
        int light = MinecraftClient.getInstance().world.isNight() ? LightmapTextureManager.MAX_LIGHT_COORDINATE / 200000 :
                LightmapTextureManager.MAX_LIGHT_COORDINATE;
        webHitModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, alpha);

        matrices.pop();
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