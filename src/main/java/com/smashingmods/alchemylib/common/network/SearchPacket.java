package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.network.AlchemyPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SearchPacket(BlockPos blockPos, String searchText) implements AlchemyPacket<SearchPacket> {

    public static final AlchemyPacketHandler<SearchPacket> HANDLER = new Packet();

    @Override
    public AlchemyPacketHandler<SearchPacket> handler() {
        return HANDLER;
    }

    private static class Packet implements AlchemyPacketHandler<SearchPacket> {
        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "search");
        }

        @Override
        public void encode(SearchPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeBlockPos(packet.blockPos);
            buf.writeUtf(packet.searchText);
        }

        @Override
        public void handle(SearchPacket message, IPayloadContext context) {
            Player player = context.player();
            AbstractSearchableBlockEntity blockEntity = (AbstractSearchableBlockEntity) player.level().getBlockEntity(message.blockPos);

            if (blockEntity != null) {
                blockEntity.setSearchText(message.searchText);
                blockEntity.setChanged();
            }
        }

        @Override
        public SearchPacket decode(RegistryFriendlyByteBuf buf) {
            return new SearchPacket(buf.readBlockPos(), buf.readUtf());
        }
    }
}
