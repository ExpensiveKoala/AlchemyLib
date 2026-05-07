package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.network.AlchemyPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TogglePauseButtonPacket(BlockPos blockPos, boolean paused) implements AlchemyPacket<TogglePauseButtonPacket> {

    public static final AlchemyPacketHandler<TogglePauseButtonPacket> HANDLER = new Packet();

    @Override
    public AlchemyPacketHandler<TogglePauseButtonPacket> handler() {
        return HANDLER;
    }

    private static class Packet implements AlchemyPacketHandler<TogglePauseButtonPacket> {

        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "toggle_pause_button");
        }

        @Override
        public void encode(TogglePauseButtonPacket packet, RegistryFriendlyByteBuf pBuffer) {
            pBuffer.writeBlockPos(packet.blockPos);
            pBuffer.writeBoolean(packet.paused);
        }

        @Override
        public void handle(TogglePauseButtonPacket message, IPayloadContext pContext) {
            Player player = pContext.player();
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(message.blockPos);

            if (blockEntity != null) {
                blockEntity.setPaused(message.paused);
                blockEntity.setChanged();
            }
        }

        @Override
        public TogglePauseButtonPacket decode(RegistryFriendlyByteBuf buf) {
            return new TogglePauseButtonPacket(buf.readBlockPos(), buf.readBoolean());
        }
    }
}
