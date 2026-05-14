package com.smashingmods.alchemylib.api.capability;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@SuppressWarnings("unused")
public class CapabilityHelper {

    public static <T extends AbstractProcessingBlockEntity> void registerDefaultCapabilities(RegisterCapabilitiesEvent event, T blockEntity, BlockEntityType<T> blockEntityType) {
        if (blockEntity instanceof AbstractInventoryBlockEntity) {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    blockEntityType,
                    (inventoryBlockEntity, direction) -> ((InventoryBlockEntity)inventoryBlockEntity).getCombinedSlotHandler().getView(direction)
            );
        }
        if (blockEntity instanceof AbstractFluidBlockEntity) {
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    blockEntityType,
                    (fluidBlockEntity, direction) -> ((AbstractFluidBlockEntity)fluidBlockEntity).getFluidStorage()
            );

            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    blockEntityType,
                    (fluidBlockEntity, direction) -> ((AbstractFluidBlockEntity)fluidBlockEntity).getCombinedSlotHandler().getView(direction)
            );
        }

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                blockEntityType,
                (be, direction) -> be.getEnergyHandler()
        );
    }
}
