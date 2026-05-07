package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public abstract class AbstractInventoryBlockEntity extends AbstractProcessingBlockEntity implements InventoryBlockEntity {

    private final ProcessingSlotHandler inputHandler = initializeInputHandler();
    private final ProcessingSlotHandler outputHandler = initializeOutputHandler();
    private final SidedProcessingSlotWrapper combinedHandler = new SidedProcessingSlotWrapper(inputHandler, outputHandler);

    public AbstractInventoryBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (!inputHandler.isEmpty()) {
            setCanProcess(canProcessRecipe());
        }
        super.tick();
    }

    @Override
    public ProcessingSlotHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public ProcessingSlotHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public SidedProcessingSlotWrapper getCombinedSlotHandler() {
        return combinedHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        pTag.put("input", inputHandler.serializeNBT(registries));
        pTag.put("output", outputHandler.serializeNBT(registries));
        pTag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(pTag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.loadAdditional(pTag, registries);
        inputHandler.deserializeNBT(registries, pTag.getCompound("input"));
        outputHandler.deserializeNBT(registries, pTag.getCompound("output"));
        if (pTag.contains("sides")) {
            combinedHandler.setSideModesFromShort(pTag.getShort("sides"));
        } else {
            combinedHandler.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);
        }
    }
}
