package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at = @At("TAIL"), method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V")
    private static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, CallbackInfo ci) {
        if(entity instanceof AbstractClientPlayerEntity player) {
            if(HeroUtil.isWearingSuit(player, FecfsTags.Items.FULLSUIT)) {
                var entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
                var renderer = entityRenderDispatcher.getRenderer(player);
                if (renderer instanceof PlayerEntityRenderer playerRenderer) {
                    PlayerEntityModel<AbstractClientPlayerEntity> model = playerRenderer.getModel();
                    model.body.visible = false;

                }
            }
        }
    }
}
