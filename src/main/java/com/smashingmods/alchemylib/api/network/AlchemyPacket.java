package com.smashingmods.alchemylib.api.network;

public interface AlchemyPacket<T extends AlchemyPacket<T>> {
    AlchemyPacketHandler<T> handler();
}
