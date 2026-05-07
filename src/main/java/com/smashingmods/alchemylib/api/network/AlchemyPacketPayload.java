package com.smashingmods.alchemylib.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AlchemyPacketPayload<T extends AlchemyPacket<T>>(
        T packet,
        CustomPacketPayload.Type<? extends CustomPacketPayload> type
) implements CustomPacketPayload {

    public AlchemyPacketPayload(T handler) {
        this(handler, handler.handler().type());
    }
}
