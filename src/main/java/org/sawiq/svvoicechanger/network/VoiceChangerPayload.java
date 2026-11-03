package org.sawiq.svvoicechanger.network;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.21 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.resources.ResourceLocation;
import org.sawiq.svvoicechanger.protocol.VoiceChangerChannel;

/**
 * The one packet this mod puts on the wire, in both directions.
 *
 * <p>It carries an opaque block of bytes and nothing else. What those bytes
 * mean is settled entirely by {@link org.sawiq.svvoicechanger.protocol.VoiceChangerCodec},
 * which is plain Java and can be tested without a game running. Keeping the
 * Minecraft-facing layer this thin is why the same wire format works unchanged
 * across every supported version and both loaders.</p>
 *
 * <p>Minecraft 1.20.1 predates typed payloads, so there the packet is just the
 * channel id plus the raw bytes; the bytes on the wire are identical.</p>
 */
//? if >=1.21 {
public record VoiceChangerPayload(byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VoiceChangerPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.parse(VoiceChangerChannel.CHANNEL));

    public static final StreamCodec<FriendlyByteBuf, VoiceChangerPayload> CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeBytes(payload.data()),
            VoiceChangerPayload::read);

    @Override
    public CustomPacketPayload.Type<VoiceChangerPayload> type() {
        return TYPE;
    }
//?} else {
/*public record VoiceChangerPayload(byte[] data) {
    public static final ResourceLocation ID = new ResourceLocation(VoiceChangerChannel.CHANNEL);

    /^* Writes this packet's body into a fresh buffer for the loader to send. ^/
    public FriendlyByteBuf write(FriendlyByteBuf buffer) {
        buffer.writeBytes(this.data);
        return buffer;
    }
*///?}

    /**
     * Reads whatever is left in the buffer, which for a custom payload is
     * exactly this packet's body. The length is checked before allocating:
     * the other end of a connection is a machine somebody else administers.
     */
    public static VoiceChangerPayload read(FriendlyByteBuf buffer) {
        int length = buffer.readableBytes();
        if (length > VoiceChangerChannel.MAX_MESSAGE_BYTES) {
            throw new DecoderException(
                    "Voice changer payload of " + length + " bytes is too large");
        }

        byte[] data = new byte[length];
        buffer.readBytes(data);
        return new VoiceChangerPayload(data);
    }
}
