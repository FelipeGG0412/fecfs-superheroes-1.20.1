package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.item.client.SMSRRenderer;
import com.fecfssuperheroes.item.client.WebShootersRenderer;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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


    @Inject(at = @At("HEAD"), cancellable = true, method = "applyBoneVisibilityBySlot")
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot, CallbackInfo ci) {
        ci.cancel();
        this.setVisible(false);
        Screen screen = MinecraftClient.getInstance().currentScreen;
        boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        boolean isInventoryOpen = screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen;
        boolean isWearingSuperSuit = HeroUtil.isWearingSuit(MinecraftClient.getInstance().player, FecfsTags.Items.FULLSUIT);
        GeoArmorRenderer webShooters = new SMSRRenderer();
        if (isFirstPerson && isWearingSuperSuit && !isInventoryOpen) {
            if (currentSlot == EquipmentSlot.CHEST) {
                this.setBoneVisible(this.rightArm, true);
            } else if (HeroUtil.isWearingWebShooter(MinecraftClient.getInstance().player)) {
                this.setBoneVisible(this.leftArm, false);
                this.setBoneVisible(this.rightArm, true);
            }
        } else {
            switch (currentSlot) {
                case HEAD -> this.setBoneVisible(this.head, true);
                case CHEST -> {
                    this.setBoneVisible(this.body, true);
                    this.setBoneVisible(this.rightArm, true);
                    this.setBoneVisible(this.leftArm, true);
                }
                case LEGS -> {
                    this.setBoneVisible(this.rightLeg, true);
                    this.setBoneVisible(this.leftLeg, true);
                }
                case FEET -> {
                    this.setBoneVisible(this.rightBoot, true);
                    this.setBoneVisible(this.leftBoot, true);
                }
            }
        }
    }

}
