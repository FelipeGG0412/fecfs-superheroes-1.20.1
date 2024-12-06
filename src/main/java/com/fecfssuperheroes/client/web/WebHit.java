package com.fecfssuperheroes.client.web;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class WebHit {
    public final Vec3d position;
    public final Direction facing;
    public final long startTime;

    public WebHit(Vec3d position, Direction facing) {
        this.position = position;
        this.facing = facing;
        this.startTime = System.currentTimeMillis();
    }
}

