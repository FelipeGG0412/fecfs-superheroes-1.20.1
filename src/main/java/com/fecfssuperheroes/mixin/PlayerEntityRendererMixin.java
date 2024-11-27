package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.item.client.SMSRModel;
import com.fecfssuperheroes.item.client.SMSRRenderer;
import com.fecfssuperheroes.item.client.WebShootersRenderer;
import com.fecfssuperheroes.item.custom.SMSRArmorItem;
import com.fecfssuperheroes.item.custom.WebShootersArmorItem;
import com.fecfssuperheroes.util.FecfsTags;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Optional;

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
            headVisibility(player, scale);
            chestVisibility(player, scale);
            legVisibility(player, scale);
        }
    }
    @Unique
    private void headVisibility(AbstractClientPlayerEntity player, float scale) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = this.getModel();
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
    @Unique
    private void chestVisibility(AbstractClientPlayerEntity player, float scale) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = this.getModel();
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
    @Unique
    private void legVisibility(AbstractClientPlayerEntity player, float scale) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = this.getModel();
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
    @Inject(at = @At("TAIL"), method = "renderArm")
    private void renderArm(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve,
            CallbackInfo ci) {
        EquipmentSlot slot = EquipmentSlot.CHEST; // First-person arm rendering typically involves chest slot only
        ItemStack stack = player.getInventory().getArmorStack(slot.getEntitySlotId());

        if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {
            GeoArmorRenderer<?> armorRenderer = getArmorRenderer(stack);
            if (armorRenderer != null) {
                matrices.push();

                // Prepare for rendering
                armorRenderer.prepForRender(player, stack, slot, armorRenderer);

                // Apply first-person-specific transformations
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5));
                matrices.translate(0, 0.01f, 0);

                // Render the visible arm part of the armor
                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorRenderer, getArmorTexture(stack, slot));

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
    private Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return new Identifier("minecraft", "textures/models/armor/chainmail_layer_1.png");
    }



}
