package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.network.AlchemyPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public record BlockEntityPacket(BlockPos blockPos, CompoundTag tag) implements AlchemyPacket<BlockEntityPacket> {

    public static final AlchemyPacketHandler<BlockEntityPacket> HANDLER = new Packet();

    @Override
    public AlchemyPacketHandler<BlockEntityPacket> handler() {
        return HANDLER;
    }

    private static class Packet implements AlchemyPacketHandler<BlockEntityPacket> {
        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "block_entity_packet");
        }

        @Override
        public void encode(BlockEntityPacket packet, RegistryFriendlyByteBuf pBuffer) {
            pBuffer.writeBlockPos(packet.blockPos);
            pBuffer.writeNbt(packet.tag);
        }

        @Override
        public void handle(BlockEntityPacket message, IPayloadContext pContext) {
            Level level = Minecraft.getInstance().level;
            BlockEntity blockEntity = Objects.requireNonNull(level).getBlockEntity(message.blockPos);
            Objects.requireNonNull(blockEntity).loadCustomOnly(message.tag, level.registryAccess());
        }

        @Override
        public BlockEntityPacket decode(RegistryFriendlyByteBuf buf) {
            return new BlockEntityPacket(buf.readBlockPos(), buf.readNbt());
        }
    }
}
