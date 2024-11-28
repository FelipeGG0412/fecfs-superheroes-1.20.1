package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class WebRendererUtil {
    private static ModelPart webLineModel = null;
    private static final Identifier WEB_TEXTURE = new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_line.png");
    public static void renderWebLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player,
                                     Vec3d anchorPoint, float tickDelta, boolean useBezierCurve) {
        if (player == null || anchorPoint == null) return;
        if (webLineModel == null) {
            webLineModel = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"));
        }

        Vec3d webStartPos = getWebStartPosition(player, tickDelta);
        if (webStartPos == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();

        int segmentCount = (int) Math.ceil(webStartPos.distanceTo(anchorPoint));
        segmentCount = Math.max(segmentCount, 8);

        Vec3d controlPoint = useBezierCurve ? calculateControlPoint(webStartPos, anchorPoint) : null;

        for (int i = 1; i < segmentCount; i++) {
            Vec3d startPos = useBezierCurve
                    ? quadraticBezier(webStartPos, controlPoint, anchorPoint, (double) i / segmentCount)
                    : linearInterpolation(webStartPos, anchorPoint, (double) i / segmentCount);

            Vec3d endPos = useBezierCurve
                    ? quadraticBezier(webStartPos, controlPoint, anchorPoint, (double) (i + 1) / segmentCount)
                    : linearInterpolation(webStartPos, anchorPoint, (double) (i + 1) / segmentCount);

            renderSegment(matrices, vertexConsumers, cameraPos, startPos, endPos);
        }
    }

    private static Vec3d getWebStartPosition(PlayerEntity player, float tickDelta) {
        if (player != null) {
            if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
                return MinecraftClient.getInstance().gameRenderer.getCamera().getPos()
                        .add(player.getMainArm() == Arm.RIGHT ? 0.3 : -0.3, -0.25, -0.5);
            } else {
                Vec3d playerPos = player.getLerpedPos(tickDelta);
                return new Vec3d(playerPos.x, playerPos.y + player.getStandingEyeHeight() + 0.5, playerPos.z);
            }
        }
        return null;
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

    private static Vec3d linearInterpolation(Vec3d start, Vec3d end, double t) {
        return start.lerp(end, t);
    }

    private static void renderSegment(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos,
                                      Vec3d startPos, Vec3d endPos) {
        if(MinecraftClient.getInstance().player == null) return;
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
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(
                new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_line.png")));
        webLineModel.render(matrices, vertexConsumer, MinecraftClient.getInstance().player.getWorld().isNight() ?
                LightmapTextureManager.MAX_LIGHT_COORDINATE / 1000 : LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

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
            ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(-0.25F))
                    .uv(9, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(0)), ModelTransform.of(0.0F, 15.0F, 0.0F, -(((float) Math.PI)), 0f, 0));
            return TexturedModelData.of(modelData, 32, 32);
        }

        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
            bone.render(matrices, vertices, light, overlay);
        }
    }

}
