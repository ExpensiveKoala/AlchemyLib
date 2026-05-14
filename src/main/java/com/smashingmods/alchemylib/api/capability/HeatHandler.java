package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
@UnstableApi
public class HeatHandler implements HeatCapability {

    private int heat;
    private final int maxHeat;

    public HeatHandler(int max) {
        this.maxHeat = max;
    }

    @Override
    public int getHeat() {
        return heat;
    }

    @Override
    public void setHeat(int heat) {
        this.heat = heat;
    }

    @Override
    public void increment(int heat) {
        if (this.heat + heat <= maxHeat) {
            this.heat += heat;
        } else {
            this.heat = maxHeat;
        }
    }

    @Override
    public void decrement(int heat) {
        if (this.heat - heat >= 0) {
            this.heat -= heat;
        } else {
            this.heat = 0;
        }
    }

    @Override
    public int getMaxHeat() {
        return maxHeat;
    }

    @Override
    public Component getComponent() {
        return Component.literal(String.format("%s H", heat));
    }

    @Override
    public String toString() {
        return String.format("[%s heat, %s max heat]", heat, maxHeat);
    }
}
