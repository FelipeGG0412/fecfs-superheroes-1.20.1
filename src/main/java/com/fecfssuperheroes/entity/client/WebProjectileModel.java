package com.fecfssuperheroes.entity.client;

import com.fecfssuperheroes.entity.custom.WebProjectile;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class WebProjectileModel extends EntityModel<WebProjectile> {
    private final ModelPart bone;
    public WebProjectileModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(2, 2).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(1, 12).cuboid(-2.0F, -4.0F, -2.25F, 4.0F, 4.0F, 5.0F, new Dilation(0.25F))
                .uv(21, 0).cuboid(-1.0F, -3.0F, 0.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(21, 9).cuboid(-1.0F, -3.0F, 0.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(WebProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
