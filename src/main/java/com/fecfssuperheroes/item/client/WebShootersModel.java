package com.fecfssuperheroes.item.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.custom.WebShootersArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WebShootersModel extends GeoModel<WebShootersArmorItem> {
    @Override
    public Identifier getModelResource(WebShootersArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "geo/web_shooters.geo.json");
    }

    @Override
    public Identifier getTextureResource(WebShootersArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "textures/armor/web_shooters.png");
    }

    @Override
    public Identifier getAnimationResource(WebShootersArmorItem animatable) {
        return new Identifier(FecfsSuperheroes.MOD_ID, "animations/web_shooters.animation.json");
    }
}
