package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.common.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * AbstractPacketHandler is meant to be extended by other mods. It provides
 * some basic helper methods so that you don't need to build your own from
 * scratch for each mod.
 *
 * <p>Implement AlchemyPacket to easily create your own. Use the builtin packets
 * as an example for how they should work.</p>
 *
 * @see AlchemyPacketHandler
 * @see BlockEntityPacket
 * @see SearchPacket
 * @see ToggleLockButtonPacket
 * @see TogglePauseButtonPacket
 */
@SuppressWarnings({"unused", "SameParameterValue"})
@EventBusSubscriber(modid = AlchemyLib.MODID)
public abstract class AbstractPacketHandler {

    private static final List<Consumer<RegisterPayloadHandlersEvent>> PACKET_HANDLERS = Collections.synchronizedList(new ArrayList<>());

    private final List<AlchemyPacketHandler<?>> clientPackets = new ArrayList<>();
    private final List<AlchemyPacketHandler<?>> serverPackets = new ArrayList<>();
    private final String version;

    public AbstractPacketHandler(String version) {
        this.version = version;

        PACKET_HANDLERS.add(this::registerPackets);
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(version);
        for (AlchemyPacketHandler<?> packet : clientPackets) {
            registerClientPacket(registrar, packet);
        }

        for (AlchemyPacketHandler<?> packet : serverPackets) {
            registerServerPacket(registrar, packet);
        }
    }

    private <T extends AlchemyPacket<T>> void registerClientPacket(PayloadRegistrar registrar, AlchemyPacketHandler<T> packet) {
        registrar.playToClient(
                packet.type(),
                packet.codec(),
                (payload, context) -> payload.packet().handler().handle(payload.packet(), context)
        );
    }

    private <T extends AlchemyPacket<T>> void registerServerPacket(PayloadRegistrar registrar, AlchemyPacketHandler<T> packet) {
        registrar.playToServer(
                packet.type(),
                packet.codec(),
                (payload, context) -> payload.packet().handler().handle(payload.packet(), context)
        );
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PACKET_HANDLERS.forEach(handler -> handler.accept(event));
    }

    protected <T extends AlchemyPacket<T>> void registerClientBoundPacket(AlchemyPacketHandler<T> packet) {
        clientPackets.add(packet);
    }

    protected <T extends AlchemyPacket<T>> void registerServerBoundPacket(AlchemyPacketHandler<T> packet) {
        serverPackets.add(packet);
    }

    /**
     * This method should be used to register all of your packets. After registering, return
     * the instance of your PacketHandler so that it can be used to send packets.
     *
     * @return an implementation of AbstractPacketHandler.
     *
     * @see PacketHandler#register()
     */
    public abstract AbstractPacketHandler register();

    /**
     * Sends the packet passed as a parameter to the server via {@link PacketDistributor}.
     *
     * @param message Your packet to send to the server.
     * @param <T> extends AlchemyPacket
     *
     * @see AlchemyPacketHandler
     */
    public <T extends AlchemyPacket<T>> void sendToServer(T message) {
        PacketDistributor.sendToServer(new AlchemyPacketPayload<>(message));
    }

    /**
     * Sends a packet to the specific player specified in parameters via {@link PacketDistributor}.
     *
     * @param message Your packet to send to the player.
     * @param serverPlayer And instance of ServerPlayer.
     * @param <T> AlchemyPacket
     *
     * @see AlchemyPacketHandler
     */
    public <T extends AlchemyPacket<T>> void sendToPlayer(T message, ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new AlchemyPacketPayload<>(message));
    }

    /**
     * Sends the packet passed as a parameter to all players connected to the server via
     * {@link PacketDistributor}. Note: this will work if you are in a single player instance,
     * LAN, or a dedicated server.
     *
     * @param message Your packet to send to all players.
     * @param <T> AlchemyPacket
     */
    public <T extends AlchemyPacket<T>> void sendToAll(T message) {
        PacketDistributor.sendToAllPlayers(new AlchemyPacketPayload<>(message));
    }

    /**
     * Sends the packet passed as a parameter to all players within a radius of the passed {@link BlockPos}
     * in the {@link Level} parameter.
     *
     * @param message Your packet to send.
     * @param exclude A nullable ServerPlayer to exclude from receiving the packet.
     * @param serverLevel An instance of the Level (overworld, nether, end, etc) used to determine the context
     *               of the BlockPos parameter.
     * @param pos BlockPos that is the center location for where to send the packet.
     * @param radius Distance in blocks from the center BlockPos, the packet is sent to everyone in this radius.
     * @param <T> AlchemyPacket
     *
     */
    public <T extends AlchemyPacket<T>> void sendToNear(T message, @Nullable ServerPlayer exclude, ServerLevel serverLevel, BlockPos pos, double radius) {
        ResourceKey<Level> dimension = serverLevel.dimension();
        double posX = pos.getX();
        double posY = pos.getY();
        double posZ = pos.getZ();
        PacketDistributor.sendToPlayersNear(serverLevel, exclude, posX, posY, posZ, radius, new AlchemyPacketPayload<>(message));
    }

    /**
     * Sends the packet to all players that are tracking a specific chunk based on the passed {@link Level} and {@link BlockPos}.
     * All players that are tracking the chunk of the BlockPos will receive the packet.
     *
     * @param message Your packet to send.
     * @param serverLevel An instance of the Level (overworld, nether, end, etc) used to determine the context
     *               of the BlockPos parameter.
     * @param pos BlockPos used to find the chunk being tracked.
     * @param <T> AlchemyPacket
     *
     * @see Level
     * @see BlockPos
     */
    public <T extends AlchemyPacket<T>> void sendToTrackingChunk(T message, ServerLevel serverLevel, BlockPos pos) {
        LevelChunk levelChunk = serverLevel.getChunkAt(pos);
        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, levelChunk.getPos(), new AlchemyPacketPayload<>(message));
    }
}
