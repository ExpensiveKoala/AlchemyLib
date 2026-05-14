package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

/**
 * Implement AlchemyPacketHandler with
 *
 * @see AlchemyPacket
 * @see BlockEntityPacket.Packet
 */
public interface AlchemyPacketHandler<T extends AlchemyPacket<T>> {

    ResourceLocation getId();

    /**
     * Implement this method to encode your packet's data to a {@link RegistryFriendlyByteBuf} that will
     * be sent across the network.
     *
     * @param packet
     * @param pBuffer {@link RegistryFriendlyByteBuf}
     * @see BlockEntityPacket.Packet#encode(BlockEntityPacket, RegistryFriendlyByteBuf)
     */
    void encode(T packet, RegistryFriendlyByteBuf pBuffer);

    /**
     * This method is called on the receiving end to handle the enqueued work. Whatever your packet does,
     * this is where you do it.
     *
     * @param message The {@link AlchemyPacket}
     * @param pContext The {@link IPayloadContext}
     * <p>Will be either {@link ClientPayloadContext} or {@link ServerPayloadContext} depending on if the packet is serverbound or clientbound.</p>
     *
     * @see BlockEntityPacket.Packet#handle(BlockEntityPacket, IPayloadContext)
     */
    void handle(T message, IPayloadContext pContext);

    /**
     * Implement this method to decode your packet's data to an {@link AlchemyPacket} with data sent across the network.
     *
     * @param buf The {@link RegistryFriendlyByteBuf} sent across the network.
     * @return A new {@link AlchemyPacket}
     */
    T decode(RegistryFriendlyByteBuf buf);

    default StreamCodec<RegistryFriendlyByteBuf, AlchemyPacketPayload<T>> codec() {
        return StreamCodec.of(
                (buf, payload) -> encode(payload.packet(), buf),
                (buf) -> new AlchemyPacketPayload<>(decode(buf))
        );
    }

    default CustomPacketPayload.Type<AlchemyPacketPayload<T>> type() {
        return new CustomPacketPayload.Type<>(this.getId());
    }
}
