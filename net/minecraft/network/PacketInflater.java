/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;
import net.minecraft.network.PacketByteBuf;

public class PacketInflater
extends ByteToMessageDecoder {
    public static final int field_34057 = 0x200000;
    /**
     * The maximum size allowed for a compressed packet. Has value {@value}.
     */
    public static final int MAXIMUM_PACKET_SIZE = 0x800000;
    private final Inflater inflater;
    private int compressionThreshold;
    private boolean field_34058;

    public PacketInflater(int compressionThreshold, boolean bl) {
        this.compressionThreshold = compressionThreshold;
        this.field_34058 = bl;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
        int i = packetByteBuf.readVarInt();
        if (i == 0) {
            list.add(packetByteBuf.readBytes(packetByteBuf.readableBytes()));
            return;
        }
        if (this.field_34058) {
            if (i < this.compressionThreshold) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
            }
            if (i > 0x800000) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
            }
        }
        byte[] bs = new byte[packetByteBuf.readableBytes()];
        packetByteBuf.readBytes(bs);
        this.inflater.setInput(bs);
        byte[] cs = new byte[i];
        this.inflater.inflate(cs);
        list.add(Unpooled.wrappedBuffer(cs));
        this.inflater.reset();
    }

    public void setCompressionThreshold(int compressionThreshold, boolean bl) {
        this.compressionThreshold = compressionThreshold;
        this.field_34058 = bl;
    }
}

