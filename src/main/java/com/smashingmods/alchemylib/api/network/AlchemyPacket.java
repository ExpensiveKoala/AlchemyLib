package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import com.smashingmods.alchemylib.common.network.PacketHandler;

/**
 * Implement AlchemyPacket to create your own packets to send across the network.
 *
 * <p>Implementing classes should return a statically instantiated {@link AlchemyPacketHandler}.</p>
 *
 * <p>The packet handler must be registered in your implementation of {@link AbstractPacketHandler#register} using
 * {@link AbstractPacketHandler#registerClientBoundPacket(AlchemyPacketHandler)} or {@link AbstractPacketHandler#registerServerBoundPacket(AlchemyPacketHandler)}</p>
 *
 * @see BlockEntityPacket
 * @see PacketHandler
 */
public interface AlchemyPacket<T extends AlchemyPacket<T>> {
    AlchemyPacketHandler<T> handler();
}
