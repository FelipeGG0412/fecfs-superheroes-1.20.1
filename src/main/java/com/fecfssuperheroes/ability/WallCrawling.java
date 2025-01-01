package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class WallCrawling extends Ability {
    public static boolean isCrawling = false;
    private boolean isUpsideDown = false;
    private Direction crawlingSurface = null;
    private int spaceKeyHeldTicks = 0;

    public WallCrawling() {
        super(null);
    }

    @Override
    public void start(PlayerEntity player) {
    }
    @Override
    public void update(PlayerEntity player) {
        if (player == null || MinecraftClient.getInstance().player == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        boolean spacePressed = client.options.jumpKey.isPressed();

        if (spacePressed) {
            spaceKeyHeldTicks++;
        } else {
            spaceKeyHeldTicks = 0;
            if (isCrawling) {
                stopCrawling(player);
            }
        }

        if (spaceKeyHeldTicks >= 5 && !isCrawling && canCrawl(player)) {
            crawlingSurface = adjacentSurface(player);
            if (crawlingSurface != null && crawlingSurface != Direction.DOWN) {
                isCrawling = true;
                isUpsideDown = (crawlingSurface == Direction.UP);
            } else {
                isCrawling = false;
                crawlingSurface = null;
            }
        }

        if (isCrawling) {
            crawlingSurface = adjacentSurface(player);
            if (crawlingSurface == null) {
                stopCrawling(player);
                return;
            }

            crawl(player);
        }
    }
    @Override
    public void stop(PlayerEntity player) {
    }
    private void stopCrawling(PlayerEntity player) {
        if (isCrawling) {
            isCrawling = false;
            isUpsideDown = false;
            crawlingSurface = null;
            player.setVelocity(player.getVelocity().x, Math.min(player.getVelocity().y, 0), player.getVelocity().z);
            spaceKeyHeldTicks = 0;
        }
    }

    private boolean canCrawl(PlayerEntity player) {
        return player.isAlive()
                && !player.isSubmergedInWater()
                && !player.getAbilities().flying
                && !WebSwing.isSwinging
                && !WebZip.isZipping()
                && HeroUtil.isWearingSuit(player, FecfsTags.Items.WALL_CRAWLER);
    }

    private Direction adjacentSurface(PlayerEntity player) {
        Direction[] directions = {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction direction : directions) {
            Vec3d offset = Vec3d.of(direction.getVector());
            Box expandedBox = player.getBoundingBox().offset(offset.multiply(0.1));
            boolean collision = !player.getWorld().isSpaceEmpty(player, expandedBox);

            if (collision) {
                return direction;
            }
        }
        return null;
    }

    private void crawl(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null) return;
        Input input = client.player.input;
        boolean spacePressed = client.options.jumpKey.isPressed();
        boolean shiftPressed = client.options.sneakKey.isPressed();

        if (!spacePressed) {
            stopCrawling(player);
            return;
        }
        if (!input.hasForwardMovement() && !shiftPressed) {
            player.setVelocity(Vec3d.ZERO);
            player.fallDistance = 0.0F;
            return;
        }

        Vec3d movement = movementInput(player, input);
        movement = movementForSurface(movement, crawlingSurface, shiftPressed, spacePressed);
        player.setVelocity(movement);
        player.fallDistance = 0.0F;
    }

    private Vec3d movementInput(PlayerEntity player, Input input) {
        Vec3d forward = player.getRotationVec(1.0F);
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = forward.crossProduct(up).normalize();

        double moveForward = 0.0;
        double moveSideways = 0.0;

        if (input.pressingForward) moveForward += 1.0;
        if (input.pressingBack) moveForward -= 1.0;
        if (input.pressingRight) moveSideways += 1.0;
        if (input.pressingLeft) moveSideways -= 1.0;

        Vec3d movement = forward.multiply(moveForward).add(right.multiply(moveSideways));

        if (movement.lengthSquared() > 0) {
            double baseSpeed = MinecraftClient.getInstance().options.sprintKey.isPressed() ? 0.4 : 0.25;
            movement = movement.normalize().multiply(baseSpeed);
        } else {
            movement = Vec3d.ZERO;
        }

        return movement;
    }

    private Vec3d movementForSurface(Vec3d movement, Direction surface, boolean shiftPressed, boolean spacePressed) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isCrawling) return Vec3d.ZERO;

        double baseSpeed = client.options.sprintKey.isPressed() ? 0.4 : 0.25;
        double climbSpeed = 0.2;

        switch (surface) {
            case UP: //
                if(spacePressed && shiftPressed) {
                    movement = movement.multiply(1, -1, 1);
                    return movement.normalize().multiply(baseSpeed * 0.8);
                } else if (spacePressed) {
                    return movement.normalize().multiply(baseSpeed * 0.8);
                } else {
                    return Vec3d.ZERO;
                }

            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                Vec3d wallNormal = Vec3d.of(surface.getVector());
                Vec3d adjustedMovement = projectOntoPlane(movement, wallNormal);
                if (shiftPressed) {
                    adjustedMovement = adjustedMovement.add(0, -climbSpeed, 0);
                }
                if (spacePressed) {
                    adjustedMovement = adjustedMovement.add(0, climbSpeed, 0);
                }
                return adjustedMovement.normalize().multiply(baseSpeed);

            default:
                return Vec3d.ZERO;
        }
    }


    private Vec3d projectOntoPlane(Vec3d vector, Vec3d planeNormal) {
        return vector.subtract(planeNormal.multiply(vector.dotProduct(planeNormal)));
    }
}
