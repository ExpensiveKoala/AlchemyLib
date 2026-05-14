package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.common.network.SearchPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSearchableBlockEntity extends AbstractInventoryBlockEntity implements SearchableBlockEntity {

    private boolean recipeSelectorOpen = false;
    private String searchText = "";

    public AbstractSearchableBlockEntity(String modId, BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(modId, type, pos, blockState);
    }

    @Override
    public void setRecipeSelectorOpen(boolean open) {
        this.recipeSelectorOpen = open;
    }

    @Override
    public boolean isRecipeSelectorOpen() {
        return recipeSelectorOpen;
    }

    @Override
    public String getSearchText() {
        return searchText;
    }

    @Override
    public void setSearchText(@Nullable String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            this.searchText = searchText;
            if (level != null && level.isClientSide()) {
                AlchemyLib.getPacketHandler().sendToServer(new SearchPacket(getBlockPos(), this.searchText));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("searchText", searchText);
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        setSearchText(tag.getString("searchText"));
    }
}
