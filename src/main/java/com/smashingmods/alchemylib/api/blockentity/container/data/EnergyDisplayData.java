package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Extends {@link AbstractDisplayData} by passing an {@link AbstractProcessingBlockEntity} into the constructor.
 * The block entity is used as a reference to get the energy stored and max energy stored values and return them
 * in {@link #getValue()} and {@link #getMaxValue()} respectively.
 */
public class EnergyDisplayData extends AbstractDisplayData {

    private final AbstractProcessingBlockEntity blockEntity;

    public EnergyDisplayData(AbstractProcessingBlockEntity blockEntity, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.blockEntity = blockEntity;
    }

    @Override
    public int getValue() {
        return blockEntity.getEnergyHandler().getEnergyStored();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getEnergyHandler().getMaxEnergyStored();
    }

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getValue());
        String capacity = numFormat.format(getMaxValue());
        return String.format("%s/%s FE", stored, capacity);
    }
}
