package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;

public class PacketHandler extends AbstractPacketHandler {

    public PacketHandler() {
        super("1.0.0");
    }

    @Override
    public PacketHandler register() {
        registerServerBoundPacket(ToggleLockButtonPacket.HANDLER);
        registerServerBoundPacket(TogglePauseButtonPacket.HANDLER);
        registerServerBoundPacket(SearchPacket.HANDLER);
        registerClientBoundPacket(BlockEntityPacket.HANDLER);
        return this;
    }
}
