package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.power.custom.DoubleJump;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class FecfsAnimations implements IAnimatedHero {
    private static boolean canPlayStart = false;
    private static boolean canPlayMiddle = false;
    private static boolean canPlayEnd = false;
    public static TagKey<Item> spiderManTag = FecfsTags.Items.SPIDERMAN;
    @Override
    public ModifierLayer<IAnimation> fecfsSuperheroes_getModAnimation() {
        return fecfsSuperheroes_getModAnimation();
    }


    //Spider-Man animations
    public static void playSpiderManDoubleJumpAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_double_jump"));
        var builder = anim.mutableCopy();
        builder.head.pitch.setEnabled(false);
        anim = builder.build();
        if (DoubleJump.hasDoubleJumped && anim != null) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(10, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim));
        }

    }
    public static void playSpiderManJumpAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_jump"));
        var builder = anim.mutableCopy();
        builder.head.pitch.setEnabled(false);
        anim = builder.build();
        animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
    }

    public static void playSpiderManLandingAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_landing"));
        var builder = anim.mutableCopy();
        builder.head.pitch.setEnabled(false);
        anim = builder.build();
        if(anim != null) {
            animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
        }
    }
    public static void playSpiderManSwingingAnimations(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag) || !HeroUtil.isWearingWebShooter(user)) return;
        if (!user.getWorld().isClient) return;

        if (!WebSwinging.isSwinging) {
            canPlayStart = false;
            canPlayMiddle = false;
            canPlayEnd = false;
            return;
        }

        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();

        KeyframeAnimation swingStart = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_swing_start"));
        KeyframeAnimation swingMiddle = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_swing_middle"));
        KeyframeAnimation swingEnd = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_swing_end"));

        if (swingStart == null || swingMiddle == null || swingEnd == null) return;
        double angle = WebSwinging.getSwingingAngle(user.getPos(), WebSwinging.anchorPoint);

        if(canPlayStart) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(swingStart));
        }
        if (angle >= -45 && angle <= 60 && !canPlayStart) {
            canPlayStart = true;
            canPlayMiddle = false;
            canPlayEnd = false;

        }
        if(canPlayMiddle) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(swingMiddle));
        }
        if (angle > 60 && angle <= 120 && !canPlayMiddle) {
            canPlayStart = false;
            canPlayMiddle = true;
            canPlayEnd = false;
        }

        if(canPlayEnd) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(swingEnd));
        }
        if ((angle > 120 && angle <= 180 || angle < -45 && angle >= -180) && !canPlayEnd) {
            canPlayStart = false;
            canPlayMiddle = false;
            canPlayEnd = true;
        }
    }
}
