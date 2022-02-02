/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class PacketEncoder
extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final NetworkSide side;

    public PacketEncoder(NetworkSide side) {
        this.side = side;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
        NetworkState networkState = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get();
        if (networkState == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        }
        Integer integer = networkState.getPacketId(this.side, packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", new Object[]{channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), integer, packet.getClass().getName()});
        }
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
        packetByteBuf.writeVarInt(integer);
        try {
            int i = packetByteBuf.writerIndex();
            packet.write(packetByteBuf);
            int j = packetByteBuf.writerIndex() - i;
            if (j > 0x800000) {
                throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
            }
            int k = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId();
            FlightProfiler.INSTANCE.onPacketSent(k, integer, channelHandlerContext.channel().remoteAddress(), j);
        } catch (Throwable throwable) {
            LOGGER.error("Error receiving packet {}", (Object)integer, (Object)throwable);
            if (packet.isWritingErrorSkippable()) {
                throw new PacketEncoderException(throwable);
            }
            throw throwable;
        }
    }

    @Override
    protected /* synthetic */ void encode(ChannelHandlerContext ctx, Object packet, ByteBuf out) throws Exception {
        this.encode(ctx, (Packet)packet, out);
    }
}

