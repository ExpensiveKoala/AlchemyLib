package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import com.smashingmods.alchemylib.common.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Function;

/**
 * Implement AlchemyPacket to create your own packets to send across the network.
 *
 * <p>Implementing classes will need two constructors. The first is to create the packet elsewhere in your code.
 * The second constructor is used as a decoder to create a new packet object on the receiving side.</p>
 *
 * <p>The packet must be registered in your implementation of {@link AbstractPacketHandler#register} using
 * {@link AbstractPacketHandler#registerMessage(Class, Function)}</p>
 *
 * @see BlockEntityPacket
 * @see PacketHandler
 */
public interface AlchemyPacketHandler<T extends AlchemyPacket<T>> {

    ResourceLocation getId();

    /**
     * Implement this method to encode your packet's data to a FriendlyByteBuf that will
     * be sent across the network.
     *
     * @param packet
     * @param pBuffer {@link FriendlyByteBuf}
     * @see BlockEntityPacket#encode(FriendlyByteBuf)
     */
    void encode(T packet, RegistryFriendlyByteBuf pBuffer);

    /**
     * This method is called on the receiving end to handle the enqueued work. Whatever your packet does,
     * this is where you do it.
     *
     * @param pContext NetworkEvent.Context
     *
     * @see BlockEntityPacket#handle(NetworkEvent.Context)
     */
    void handle(T message, IPayloadContext pContext);

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
