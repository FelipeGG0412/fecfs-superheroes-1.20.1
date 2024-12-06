package com.fecfssuperheroes.entity;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.entity.custom.WebProjectile;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FecfsEntities {
    public static final EntityType<WebProjectile> WEB_PROJECTILE_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(FecfsSuperheroes.MOD_ID, "web_projectile"), FabricEntityTypeBuilder.<WebProjectile>create(SpawnGroup.MISC, WebProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    public static void registerEntities() {
        FecfsSuperheroes.LOGGER.info("Registering entities for " + FecfsSuperheroes.MOD_ID);
    }
}
