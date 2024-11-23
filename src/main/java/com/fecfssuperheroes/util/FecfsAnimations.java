package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.power.custom.DoubleJump;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class FecfsAnimations implements IAnimatedHero {
    private static boolean hasPlayedDiveAnimation = false;
    private static boolean isFalling = false;
    @Override
    public ModifierLayer<IAnimation> fecfsSuperheroes_getModAnimation() {
        return fecfsSuperheroes_getModAnimation();
    }
    //Spider-Man animations
    public static void playSpiderManDoubleJumpAnimation(PlayerEntity user) {
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_double_jump"));
        var builder = anim.mutableCopy();
        builder.head.pitch.setEnabled(false);
        anim = builder.build();
        if (DoubleJump.hasDoubleJumped) {
            animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
        }

    }
    public static void playSpiderManJumpAnimation(PlayerEntity user) {
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_jump"));
        var builder = anim.mutableCopy();
        builder.head.pitch.setEnabled(false);
        anim = builder.build();
        animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
    }
    public static void playSpidermanDiveAnimation(PlayerEntity user) {
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation dive = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_dive"));
        var builder = dive.mutableCopy();
        builder.head.pitch.setEnabled(false);
        dive = builder.build();
        boolean isCurrentlyFalling = !user.getAbilities().flying
                && user.getVelocity().y < 0
                && !user.isOnGround()
                && !user.isTouchingWater()
                && !WebSwinging.isSwinging;
        if (isCurrentlyFalling) {
            if (!isFalling) {
                isFalling = true; // Mark the start of falling
                hasPlayedDiveAnimation = false; // Reset animation flag
            }

            if (!hasPlayedDiveAnimation && animationContainer.getAnimation() == null) {
                // Play the animation if it hasn't been played and no animation is currently active
                animationContainer.setAnimation(new KeyframeAnimationPlayer(dive));
                hasPlayedDiveAnimation = true; // Mark animation as played
                System.out.println("Dive animation triggered!");
            }
        } else {
            // Player is no longer falling
            if (isFalling) {
                System.out.println("Player landed or exited fall.");
                isFalling = false; // Reset falling state
            }

            // Ensure the animation stays on the last frame if necessary
            if (animationContainer.getAnimation() != null && !hasPlayedDiveAnimation) {
                System.out.println("Animation cleared on landing.");
            }
        }
    }

    public static void playSpiderManSwingingAnimations(PlayerEntity user) {
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();


        if (WebSwinging.isSwinging) {
            double angle = WebSwinging.getSwingingAngle(user.getPos(), WebSwinging.anchorPoint);
            if (angle >= (-10) && angle <= 74) {
            }
            if (angle >= 75 && angle <= 110) {
            }
            if (angle >= 111 && angle <= 180) {

            }
        }
    }
}
