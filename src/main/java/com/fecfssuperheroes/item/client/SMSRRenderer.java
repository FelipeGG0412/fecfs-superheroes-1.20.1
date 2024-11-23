package com.fecfssuperheroes.item.client;

import com.fecfssuperheroes.item.custom.SMSRArmorItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SMSRRenderer extends GeoArmorRenderer<SMSRArmorItem> {
    public SMSRRenderer() {
        super(new SMSRModel());
    }
}
