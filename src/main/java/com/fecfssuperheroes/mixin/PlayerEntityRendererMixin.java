package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.item.client.SMSRRenderer;
import com.fecfssuperheroes.item.client.WebShootersRenderer;
import com.fecfssuperheroes.item.custom.SMSRArmorItem;
import com.fecfssuperheroes.item.custom.WebShootersArmorItem;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "setModelPose", at = @At("HEAD"))
    private void setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = this.getModel();

        visibility(player, .9f);
        if(player != null) {
            boolean a = SMSRArmorItem.isStartSwinging;
            boolean b = SMSRArmorItem.isMiddleSwinging;
            boolean c = SMSRArmorItem.isEndSwinging;
        }
    }

    @Unique
    private void visibility(AbstractClientPlayerEntity player, float scale) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = this.getModel();
        if(player != null) {
            RendererUtils.headVisibility(player, scale, model);
            RendererUtils.chestVisibility(player, scale, model);
            RendererUtils.legVisibility(player, scale, model);
        }
    }
    @Inject(at = @At("TAIL"), method = "renderArm")
    private void renderArm(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve,
            CallbackInfo ci) {
        EquipmentSlot slot = EquipmentSlot.CHEST;
        ItemStack stack = player.getInventory().getArmorStack(slot.getEntitySlotId());

        if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {
            GeoArmorRenderer<?> armorRenderer = getArmorRenderer(stack);
            if (armorRenderer != null) {
                matrices.push();
                armorRenderer.prepForRender(player, stack, slot, armorRenderer);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5));
                matrices.translate(0, 0.01f, 0);
                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorRenderer, getArmorTexture());
                matrices.pop();
            }
        }
    }

    @Unique
    private GeoArmorRenderer<?> getArmorRenderer(ItemStack stack) {
        if (stack.getItem() instanceof SMSRArmorItem) {
            return new SMSRRenderer();
        } else if(stack.getItem() instanceof WebShootersArmorItem) {
            return new WebShootersRenderer();
        }
        return null;
    }

    @Unique
    private Identifier getArmorTexture() {
        return new Identifier("minecraft", "textures/models/armor/chainmail_layer_1.png");
    }



}