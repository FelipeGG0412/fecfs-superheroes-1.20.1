package com.fecfssuperheroes.entity.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.entity.custom.WebProjectile;
import com.fecfssuperheroes.entity.layer.FecfsModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WebProjectileRenderer extends EntityRenderer<WebProjectile> {
    public static final Identifier TEXTURE = new Identifier(FecfsSuperheroes.MOD_ID, "textures/entity/web_projectile.png");
    protected WebProjectileModel model;
    public WebProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new WebProjectileModel(ctx.getPart(FecfsModelLayers.WEB_PROJECTILE));
    }
    @Override
    public void render(WebProjectile entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        double x = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.prevY, entity.getY()) - 1;
        double z = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        matrices.translate(x - entity.getX(), y - entity.getY(), z - entity.getZ());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));

        // Render the model
        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE)), light, OverlayTexture.DEFAULT_UV,
                1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }


    @Override
    public Identifier getTexture(WebProjectile entity) {
        return TEXTURE;
    }
}
