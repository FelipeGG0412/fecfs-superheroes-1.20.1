package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwing;
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
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import static com.fecfssuperheroes.ability.WebSwing.swingHand;

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
        var builder = anim.mutableCopy();
        builder.head.yaw.setEnabled(false);
        anim = builder.build();
        if (DoubleJump.hasDoubleJumped && anim != null) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(8, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim));
        }

    }
    public static void playSpiderManLandingAnimation(PlayerEntity user) {
        if(!HeroUtil.isWearingSuit(user, spiderManTag)) return;
        var animationContainer = ((IAnimatedHero) user).fecfsSuperheroes_getModAnimation();
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(FecfsSuperheroes.MOD_ID,
                "smsr_landing"));;
        if(anim != null) {
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(1, Ease.LINEAR),
                    new KeyframeAnimationPlayer(anim));
        }

    }
}
