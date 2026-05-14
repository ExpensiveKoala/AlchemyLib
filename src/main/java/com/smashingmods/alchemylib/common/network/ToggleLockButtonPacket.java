package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacketHandler;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleLockButtonPacket(BlockPos blockPos, boolean locked) implements AlchemyPacket<ToggleLockButtonPacket> {

    public static final AlchemyPacketHandler<ToggleLockButtonPacket> HANDLER = new Packet();

    @Override
    public AlchemyPacketHandler<ToggleLockButtonPacket> handler() {
        return HANDLER;
    }

    private static class Packet implements AlchemyPacketHandler<ToggleLockButtonPacket> {
        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "toggle_lock_button");
        }

        @Override
        public void encode(ToggleLockButtonPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeBlockPos(packet.blockPos);
            buf.writeBoolean(packet.locked);
        }

        @Override
        public void handle(ToggleLockButtonPacket message, IPayloadContext context) {
            Player player = context.player();
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(message.blockPos());

            if (blockEntity != null) {
                blockEntity.setRecipeLocked(message.locked());
                blockEntity.setChanged();
            }
        }

        @Override
        public ToggleLockButtonPacket decode(RegistryFriendlyByteBuf buf) {
            return new ToggleLockButtonPacket(buf.readBlockPos(), buf.readBoolean());
        }
    }
}
