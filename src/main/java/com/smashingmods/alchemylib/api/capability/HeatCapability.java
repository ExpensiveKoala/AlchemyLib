package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
@UnstableApi
public interface HeatCapability {

    int getHeat();

    void setHeat(int heat);

    void increment(int heat);

    void decrement(int heat);

    int getMaxHeat();

    Component getComponent();

    String toString();
}
