package com.fecfssuperheroes.util;

import com.fecfssuperheroes.item.FecfsItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class HeroUtil {
    private static final List<ScheduledAction> scheduledActions = new ArrayList<>();
    public static boolean canChargedJump = false;
    public static boolean isWearingSuit(PlayerEntity player, TagKey<Item> tag) {
        if (player != null && player.getInventory().armor.toArray().length == 4) {
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            return helmet.isIn(tag) && chestplate.isIn(tag) && leggings.isIn(tag) && boots.isIn(tag);
        }
        return false;
    }
    public static boolean isWearingWebShooter(PlayerEntity player) {
        if(player != null) {
            TagKey<Item> tag = FecfsTags.Items.FULLSUIT;
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            return !helmet.isIn(tag) && chestplate.isOf(FecfsItems.WEB_SHOOTERS) && !leggings.isIn(tag) && !boots.isIn(tag);
        }
        return false;
    }
    public static boolean isSlotInTag(PlayerEntity player, EquipmentSlot slot, TagKey<Item> tag) {
        return player.getEquippedStack(slot).isIn(tag);
    }
    public static BlockHitResult raycast(PlayerEntity player, int distance) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = start.add(player.getRotationVector().multiply(distance));
        return player.getWorld().raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player));
    }
    public static boolean canUseWeb(PlayerEntity player, boolean ground) {
        if(ground) {
            return (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player)) &&
                    !player.getAbilities().flying &&
                    player.isAlive() &&
                    !player.isTouchingWater();
        } else {
            return (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player)) &&
                    !player.getAbilities().flying &&
                    player.isAlive() &&
                    !player.isTouchingWater() && !player.isOnGround();
        }
    }
    private static boolean checkChargedJump() {
        if(MinecraftClient.getInstance().options.jumpKey.isPressed() && MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            return canChargedJump = true;
        } else {
            return false;
        }
    }
    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick(client));
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.isPaused()) {
            return;
        }

        PlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        Iterator<ScheduledAction> iterator = scheduledActions.iterator();
        while (iterator.hasNext()) {
            ScheduledAction scheduledAction = iterator.next();
            scheduledAction.decrementTicks();
            if (scheduledAction.getTicksLeft() <= 0) {
                scheduledAction.getAction().accept(player);
                iterator.remove();
            }
        }
    }
    public static void tickAction(int ticksToWait, Consumer<PlayerEntity> action) {
        if (ticksToWait <= 0) {
            throw new IllegalArgumentException("ticks must be positive!");
        }
        if (action == null) {
            throw new NullPointerException("action cannot be null!");
        }
        scheduledActions.add(new ScheduledAction(ticksToWait, action));
    }
}
