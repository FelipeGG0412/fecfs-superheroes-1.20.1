package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(GeoArmorRenderer.class)
public abstract class GeoArmorRendererMixin {
    @Shadow
    public abstract void setVisible(boolean pVisible);

    @Shadow protected abstract void setBoneVisible(@Nullable GeoBone bone, boolean visible);

    @Shadow protected GeoBone head;

    @Shadow protected GeoBone body;

    @Shadow protected GeoBone rightArm;

    @Shadow protected GeoBone leftArm;

    @Shadow protected GeoBone rightLeg;

    @Shadow protected GeoBone leftLeg;

    @Shadow protected GeoBone rightBoot;

    @Shadow protected GeoBone leftBoot;

    @Shadow protected float scaleWidth;

    @Shadow @Nullable public abstract GeoBone getBodyBone();

    @Inject(at = @At("HEAD"), cancellable = true, method = "applyBoneVisibilityBySlot")
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot, CallbackInfo ci) {
        ci.cancel();

        this.setVisible(false);
        Screen screen = MinecraftClient.getInstance().currentScreen;
        boolean bl = MinecraftClient.getInstance().getCameraEntity() instanceof
                LivingEntity && ((LivingEntity)MinecraftClient.getInstance().getCameraEntity()).isSleeping();
        boolean isInventoryOpen = screen instanceof InventoryScreen;
        boolean isCreativeInventoryOpen = screen instanceof CreativeInventoryScreen;
        boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        TagKey<Item> armorTag = FecfsTags.Items.FULLSUIT;
        boolean isWearingSuperSuit = HeroUtil.isWearingSuit(MinecraftClient.getInstance().player, FecfsTags.Items.FULLSUIT);
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(isFirstPerson && isWearingSuperSuit && !bl && !isCreativeInventoryOpen && !isInventoryOpen) {
            switch (currentSlot) {
                case HEAD:
                    this.setBoneVisible(this.head, true);
                    break;
                case CHEST:
                    if(player.getMainArm() == Arm.RIGHT) {
                        this.setBoneVisible(this.rightArm, true);
                        this.setBoneVisible(this.body, false);
                        this.setBoneVisible(this.leftArm, false);
                    } else {
                        this.setBoneVisible(this.rightArm, true);
                        this.setBoneVisible(this.body, false);
                        this.setBoneVisible(this.leftArm, false);
                    }
                    break;
                case LEGS:
                    this.setBoneVisible(this.rightLeg, true);
                    this.setBoneVisible(this.leftLeg, true);
                    break;
                case FEET:
                    this.setBoneVisible(this.rightBoot, true);
                    this.setBoneVisible(this.leftBoot, true);
                    break;
            }
        } else {
            switch (currentSlot) {
                case HEAD:
                    this.setBoneVisible(this.head, true);
                    break;
                case CHEST:
                    this.setBoneVisible(this.body, true);
                    this.setBoneVisible(this.rightArm, true);
                    this.setBoneVisible(this.leftArm, true);
                    break;
                case LEGS:
                    this.setBoneVisible(this.rightLeg, true);
                    this.setBoneVisible(this.leftLeg, true);
                    break;
                case FEET:
                    this.setBoneVisible(this.rightBoot, true);
                    this.setBoneVisible(this.leftBoot, true);
                    break;
            }
        }
        if(HeroUtil.isSlotInTag(MinecraftClient.getInstance().player, EquipmentSlot.CHEST, armorTag) && isFirstPerson && !bl
                && !isCreativeInventoryOpen && !isInventoryOpen) {
            switch (currentSlot) {
                case HEAD:
                    this.setBoneVisible(this.head, true);
                    break;
                case CHEST:
                    this.setBoneVisible(this.body, false);
                    this.setBoneVisible(this.rightArm, true);
                    this.setBoneVisible(this.leftArm, false);
                    break;
                case LEGS:
                    this.setBoneVisible(this.rightLeg, true);
                    this.setBoneVisible(this.leftLeg, true);
                    break;
                case FEET:
                    this.setBoneVisible(this.rightBoot, true);
                    this.setBoneVisible(this.leftBoot, true);
                    break;
            }
        } else {
            switch (currentSlot) {
                case HEAD:
                    this.setBoneVisible(this.head, true);
                    break;
                case CHEST:
                    this.setBoneVisible(this.body, true);
                    this.setBoneVisible(this.rightArm, true);
                    this.setBoneVisible(this.leftArm, true);
                    break;
                case LEGS:
                    this.setBoneVisible(this.rightLeg, true);
                    this.setBoneVisible(this.leftLeg, true);
                    break;
                case FEET:
                    this.setBoneVisible(this.rightBoot, true);
                    this.setBoneVisible(this.leftBoot, true);
                    break;
            }
        }
    }
}
