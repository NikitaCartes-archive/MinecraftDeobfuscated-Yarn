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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.network.packet.Packet;
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
        int i = networkState.getPacketId(this.side, packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), i, packet.getClass().getName());
        }
        if (i == -1) {
            throw new IOException("Can't serialize unregistered packet");
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
        packetByteBuf.writeVarInt(i);
        try {
            int j = packetByteBuf.writerIndex();
            packet.write(packetByteBuf);
            int k = packetByteBuf.writerIndex() - j;
            if (k > 0x800000) {
                throw new IllegalArgumentException("Packet too big (is " + k + ", should be less than 8388608): " + packet);
            }
            int l = channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get().getId();
            FlightProfiler.INSTANCE.onPacketSent(l, i, channelHandlerContext.channel().remoteAddress(), k);
        } catch (Throwable throwable) {
            LOGGER.error("Error receiving packet {}", (Object)i, (Object)throwable);
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

