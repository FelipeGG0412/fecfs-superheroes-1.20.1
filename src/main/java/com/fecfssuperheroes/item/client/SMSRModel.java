package com.fecfssuperheroes.item.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.custom.SMSRArmorItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SMSRModel extends GeoModel<SMSRArmorItem> {
    @Override
    public Identifier getModelResource(SMSRArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "geo/sm/smsr.json");
    }

    @Override
    public Identifier getTextureResource(SMSRArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "textures/armor/sm/smsr.png");
    }

    @Override
    public Identifier getAnimationResource(SMSRArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "animations/sm/smsr.animation.json");
    }
}
