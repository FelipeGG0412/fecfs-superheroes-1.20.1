package com.fecfssuperheroes.client.web;

import net.minecraft.util.math.Vec3d;

public class WebLine {
    public Vec3d startPos;
    public Vec3d anchorPoint;
    public long startTime;
    public double swingAngle;
    public double swingSpeed;

    public WebLine(Vec3d startPos, Vec3d anchorPoint) {
        this.startPos = startPos;
        this.anchorPoint = anchorPoint;
        this.startTime = System.currentTimeMillis();
        this.swingAngle = 0.0;
        this.swingSpeed = 0.0;
    }
}

