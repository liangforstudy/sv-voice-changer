package org.sawiq.svvoicechanger.network;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;

//? if fabric && >=1.21 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?}
//? if fabric && <1.21 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
*///?}
//? if forge {
/*import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
*///?}
//? if neoforge {
/*import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
*///?}

/**
 * Where the packet meets the loader.
 *
 * <p>Both directions are registered from common code even though only one of
 * them is ever used on a given side. A payload registered on one side and not
 * the other is a payload neither side can use, so the registration cannot be
 * split by physical side even though the handling can: the client installs its
 * own handler through {@link #setClientReceiver}, and nothing here ever names a
 * client class.</p>
 */
public final class VoiceChangerNetwork {
    private static volatile Consumer<byte[]> clientReceiver;
    private static volatile BiConsumer<ServerPlayer, byte[]> serverReceiver;

    private VoiceChangerNetwork() {
    }

    /**
     * Declares the packet. Must run while the game is starting: Minecraft
     * settles which payload types exist before anything connects.
     */
    public static void initialize() {
        // Minecraft 26 renamed these two accessors; nothing else about the
        // Fabric networking API moved.
        //? if fabric && >=1.21 && <26.1 {
        PayloadTypeRegistry.playS2C().register(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC);
        //?}
        //? if fabric && >=26.1 {
        /*PayloadTypeRegistry.clientboundPlay().register(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC);
        *///?}

        //? if fabric && >=1.21 {
        ServerPlayNetworking.registerGlobalReceiver(VoiceChangerPayload.TYPE, (payload, context) -> {
            byte[] data = payload.data();
            ServerPlayer player = context.player();
            // Arrives on a network thread; the policy it triggers touches
            // server state and the player list.
            context.server().execute(() -> deliverToServer(player, data));
        });
        //?}
        //? if fabric && <1.21 {
        /*// Fabric API on 1.20.1 hands over the raw buffer, which is released
        // as soon as this returns, so the bytes are copied out here.
        ServerPlayNetworking.registerGlobalReceiver(VoiceChangerPayload.ID,
                (server, player, handler, buffer, responseSender) -> {
                    byte[] data = VoiceChangerPayload.read(buffer).data();
                    server.execute(() -> deliverToServer(player, data));
                });
        *///?}
        //? if forge {
        /*// An event channel rather than a SimpleChannel: it puts the bytes on
        // the wire as they are, with no discriminator in front, so a Forge
        // side and a Fabric side still understand each other. Every version
        // string is accepted, which makes the channel optional both ways.
        CHANNEL = NetworkRegistry.ChannelBuilder.named(VoiceChangerPayload.ID)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .eventNetworkChannel();
        CHANNEL.addListener(VoiceChangerNetwork::onServerPayload);
        CHANNEL.addListener(VoiceChangerNetwork::onClientPayload);
        *///?}
        //? if neoforge {
        /*// NeoForge collects payload handlers in an event instead; see
        // registerPayloads, which the mod entrypoint subscribes.
        *///?}
    }

    //? if forge {
    /*private static final String PROTOCOL_VERSION = "1";
    private static EventNetworkChannel CHANNEL;

    private static void onServerPayload(NetworkEvent.ClientCustomPayloadEvent event) {
        NetworkEvent.Context context = event.getSource().get();
        ServerPlayer player = context.getSender();
        if (player == null || event.getPayload() == null) {
            return;
        }

        byte[] data = VoiceChangerPayload.read(event.getPayload()).data();
        context.enqueueWork(() -> deliverToServer(player, data));
        context.setPacketHandled(true);
    }

    private static void onClientPayload(NetworkEvent.ServerCustomPayloadEvent event) {
        NetworkEvent.Context context = event.getSource().get();
        if (context.getDirection() != NetworkDirection.PLAY_TO_CLIENT || event.getPayload() == null) {
            return;
        }

        byte[] data = VoiceChangerPayload.read(event.getPayload()).data();
        context.enqueueWork(() -> deliverToClient(data));
        context.setPacketHandled(true);
    }

    /^* Whether the other end of this connection registered the channel. ^/
    public static boolean isRemotePresent(net.minecraft.network.Connection connection) {
        return CHANNEL != null && connection != null && CHANNEL.isRemotePresent(connection);
    }
    *///?}

    //? if neoforge && <26.1 {
    /*/^*
     * Subscribed by the common entrypoint on the mod event bus.
     *
     * <p>Registered as optional, so a client without this mod is not refused
     * by a server that has it, and the other way round.
     *
     * <p>One call, not one per direction: NeoForge keys payloads by their id
     * and throws on a second registration of the same one. The single handler
     * serves both directions, so it tells them apart itself.^/
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        IPayloadHandler<VoiceChangerPayload> handler = (payload, context) -> {
            byte[] data = payload.data();
            if (context.flow() == PacketFlow.SERVERBOUND) {
                ServerPlayer player = (ServerPlayer) context.player();
                context.enqueueWork(() -> deliverToServer(player, data));
            } else {
                context.enqueueWork(() -> deliverToClient(data));
            }
        };
        registerBothDirections(registrar, handler);
    }
    *///?}

    //? if neoforge && <1.21.7 {
    /*private static void registerBothDirections(PayloadRegistrar registrar, IPayloadHandler<VoiceChangerPayload> handler) {
        registrar.playBidirectional(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC, handler);
    }
    *///?}

    //? if neoforge && >=1.21.7 && <26.1 {
    /*/^*
     * NeoForge 21.7 gave playBidirectional a second handler, one per side, and
     * the old three-argument form now passes null for the client one. NeoForge
     * then refuses to finish loading ("clientbound payloads are missing
     * client-side handlers"), so on 1.21.7 and later the four-argument form is
     * the only one that works - and it does not exist before that.
     *
     * <p>The 1.21.8 build covers 1.21 through 1.21.8, both sides of the change,
     * so it looks for the new form rather than assuming it. NeoForge runs under
     * the same names it was compiled against, so a lookup by name is reliable
     * here, which it would not be on Fabric.^/
    private static final boolean HAS_PER_SIDE_HANDLERS = hasPerSideHandlers();

    private static boolean hasPerSideHandlers() {
        try {
            PayloadRegistrar.class.getMethod("playBidirectional", CustomPacketPayload.Type.class,
                    StreamCodec.class, IPayloadHandler.class, IPayloadHandler.class);
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }

    private static void registerBothDirections(PayloadRegistrar registrar, IPayloadHandler<VoiceChangerPayload> handler) {
        if (HAS_PER_SIDE_HANDLERS) {
            registrar.playBidirectional(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC, handler, handler);
        } else {
            registrar.playBidirectional(VoiceChangerPayload.TYPE, VoiceChangerPayload.CODEC, handler);
        }
    }
    *///?}

    //? if neoforge && >=26.1 {
    /*/^*
     * Subscribed by the common entrypoint on the mod event bus.
     *
     * <p>Registered as optional, so a client without this mod is not refused
     * by a server that has it, and the other way round.
     *
     * <p>As on 1.21.7 and later, the three-argument playBidirectional passes
     * null for the client side and NeoForge refuses to finish loading. The
     * four-argument form registers both, server handler first - the order
     * NeoForge's own playToClient reveals.^/
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playBidirectional(
                VoiceChangerPayload.TYPE,
                VoiceChangerPayload.CODEC,
                (payload, context) -> {
                    byte[] data = payload.data();
                    ServerPlayer player = (ServerPlayer) context.player();
                    context.enqueueWork(() -> deliverToServer(player, data));
                },
                (payload, context) -> {
                    byte[] data = payload.data();
                    context.enqueueWork(() -> deliverToClient(data));
                });
    }
    *///?}

    /** Installed by the client half once it is up. */
    public static void setClientReceiver(Consumer<byte[]> receiver) {
        clientReceiver = receiver;
    }

    /** Installed by the server half once it is up. */
    public static void setServerReceiver(BiConsumer<ServerPlayer, byte[]> receiver) {
        serverReceiver = receiver;
    }

    /** @return whether the packet was handed to the network layer */
    public static boolean sendToPlayer(ServerPlayer player, byte[] data) {
        if (!canSendTo(player)) {
            return false;
        }

        //? if fabric && >=1.21 {
        ServerPlayNetworking.send(player, new VoiceChangerPayload(data));
        //?}
        //? if fabric && <1.21 {
        /*ServerPlayNetworking.send(player, VoiceChangerPayload.ID,
                new VoiceChangerPayload(data).write(PacketByteBufs.create()));
        *///?}
        //? if forge {
        /*player.connection.send(new ClientboundCustomPayloadPacket(VoiceChangerPayload.ID,
                new VoiceChangerPayload(data).write(new FriendlyByteBuf(Unpooled.buffer()))));
        *///?}
        //? if neoforge {
        /*PacketDistributor.sendToPlayer(player, new VoiceChangerPayload(data));
        *///?}
        return true;
    }

    /** Whether this player's client speaks our channel. */
    public static boolean canSendTo(ServerPlayer player) {
        //? if fabric && >=1.21 {
        return ServerPlayNetworking.canSend(player, VoiceChangerPayload.TYPE);
        //?}
        //? if fabric && <1.21 {
        /*return ServerPlayNetworking.canSend(player, VoiceChangerPayload.ID);
        *///?}
        //? if forge {
        /*return isRemotePresent(player.connection.connection);
        *///?}
        //? if neoforge {
        /*return NetworkRegistry.hasChannel(player.connection, VoiceChangerPayload.TYPE.id());
        *///?}
    }

    private static void deliverToClient(byte[] data) {
        Consumer<byte[]> receiver = clientReceiver;
        if (receiver != null) {
            receiver.accept(data);
        }
    }

    private static void deliverToServer(ServerPlayer player, byte[] data) {
        BiConsumer<ServerPlayer, byte[]> receiver = serverReceiver;
        if (receiver != null) {
            receiver.accept(player, data);
        }
    }
}
