package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.power.custom.DoubleJump;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
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
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FecfsAnimations implements IAnimatedHero {
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
        if (DoubleJump.hasDoubleJumped && anim != null) {
            var builder = anim.mutableCopy();
            builder.head.yaw.setEnabled(false);
            anim = builder.build();
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim));
        }

    }
    public static void playSpiderManLandingAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_land"));;
        if(anim != null) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(1, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim));
        }

    }
    public static void playWebShootAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        boolean rightArm;
        Random rand = new Random();
        int a = rand.nextInt(101);
        if(a <= 50) {
            rightArm = true;
        } else {
            rightArm = false;
        }
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                rightArm ? "smsr_shoot_web_right" : "smsr_shoot_web_left"));
        if(anim != null) {
            var builder = anim.mutableCopy();
            builder.head.fullyEnablePart(false);
            anim = builder.build();
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(1, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim).setFirstPersonMode(FirstPersonMode.VANILLA));

        }
    }
    public static void playDiveIntroAnimation(PlayerEntity user) {
        if (!HeroUtil.isWearingSuit(user, spiderManTag)) return;

        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_dive_intro"));

        if (anim != null) {
            animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
        }
    }

    public static void playDiveLoopAnimation(PlayerEntity user) {
        if (!HeroUtil.isWearingSuit(user, spiderManTag)) return;

        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID, "smsr_dive"));

        if (anim != null) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.LINEAR),
                    new  KeyframeAnimationPlayer(anim));
        }
    }
    public static void stopAnimation(PlayerEntity player){
        var animationContainer = ((IAnimatedHero) player).fecfsSuperheroes_getModAnimation();
        if(animationContainer.isActive()) {
            animationContainer.setAnimation(null);
        }
    }

}
