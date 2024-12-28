package com.fecfssuperheroes.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class WebHitModel extends EntityModel<Entity> {
    private final ModelPart bone;

    public WebHitModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -4.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.3605F, -1.3859F, -2.3519F));
        ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(17, 0).cuboid(0.0F, -4.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.8F, 0.0F, 0.0F, 0.7811F, -1.3859F, -0.7897F));
        ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(17, 17).cuboid(0.0F, -4.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.8F, -7.9316F, 0.0F, -0.7811F, -1.3859F, 0.7897F));
        ModelPartData cube_r4 = bone.addChild("cube_r4", ModelPartBuilder.create().uv(0, 17).cuboid(0.0F, -4.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.9316F, 0.0F, -2.3605F, -1.3859F, 2.3519F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // No-op; static model
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}