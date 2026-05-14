package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class AbstractProcessingBlockEntity extends BlockEntity implements ProcessingBlockEntity, EnergyBlockEntity, MenuProvider {

    private final Component name;
    private int energyPerTick = 0;
    private int maxProgress = 0;
    private int progress = 0;
    private boolean canProcess = false;
    private boolean recipeLocked = false;
    private boolean paused = false;

    private boolean ioScreenOpen = false;

    private final EnergyStorageHandler energyHandler = initializeEnergyStorage();

    public AbstractProcessingBlockEntity(String modId, BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.name = MutableComponent.create(new TranslatableContents(String.format("%s.container.%s", modId, BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType())), null, TranslatableContents.NO_ARGS));
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        Objects.requireNonNull(pkt.getTag());
        this.loadAdditional(pkt.getTag(), lookupProvider);
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (!paused) {
                if (!recipeLocked) {
                    updateRecipe();
                }
                if (canProcess) {
                    processRecipe();
                }
            }
        }
    }

    @Override
    public boolean getCanProcess() {
        return canProcess;
    }

    @Override
    public void setCanProcess(boolean canProcess) {
        this.canProcess = canProcess;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void incrementProgress() {
        this.progress++;
    }

    @Override
    public boolean isRecipeLocked() {
        return this.recipeLocked;
    }

    @Override
    public void setRecipeLocked(boolean recipeLocked) {
        this.recipeLocked = recipeLocked;
    }

    @Override
    public boolean isProcessingPaused() {
        return this.paused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public boolean isSideConfigScreenOpen() {
        return ioScreenOpen;
    }

    @Override
    public void setSideConfigScreenState(boolean state) {
        this.ioScreenOpen = state;
    }

    @Override
    public EnergyStorageHandler getEnergyHandler() {
        return energyHandler;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        setProgress(tag.getInt("progress"));
        setRecipeLocked(tag.getBoolean("locked"));
        setPaused(tag.getBoolean("paused"));
        energyHandler.deserializeNBT(registries, Objects.requireNonNull(tag.get("energy")));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("progress", progress);
        tag.putBoolean("locked", isRecipeLocked());
        tag.putBoolean("paused", isProcessingPaused());
        tag.put("energy", energyHandler.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }
}
