package com.fecfssuperheroes.item.client;


import com.fecfssuperheroes.item.custom.WebShootersArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WebShootersRenderer extends GeoArmorRenderer<WebShootersArmorItem> {
    public WebShootersRenderer() {
        super(new WebShootersModel());
    }
}
