package com.smashingmods.alchemylib.api.blockentity.power;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Arrays;
import java.util.Collection;

/**
 * {@link net.minecraft.world.level.block.state.BlockState BlockState} property wrapper for {@link PowerState} for persisting that state.
 */
@SuppressWarnings("unused")
public class PowerStateProperty extends EnumProperty<PowerState> {
    public static final PowerStateProperty POWER_STATE = PowerStateProperty.create("power_state", PowerState.values());

    protected PowerStateProperty(String name, Collection<PowerState> values) {
        super(name, PowerState.class, values);
    }

    public static PowerStateProperty create(String name, PowerState... values) {
        return create(name, Arrays.asList(values));
    }

    public static PowerStateProperty create(String name, Collection<PowerState> values) {
        return new PowerStateProperty(name, values);
    }
}
