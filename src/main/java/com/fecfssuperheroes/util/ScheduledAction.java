package com.fecfssuperheroes.util;

import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class ScheduledAction {
    private int ticksLeft;
    private final Consumer<PlayerEntity> action;

    public ScheduledAction(int ticksLeft, Consumer<PlayerEntity> action) {
        this.ticksLeft = ticksLeft;
        this.action = action;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void decrementTicks() {
        ticksLeft--;
    }

    public Consumer<PlayerEntity> getAction() {
        return action;
    }
}
