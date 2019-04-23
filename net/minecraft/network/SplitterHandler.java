/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.util.PacketByteBuf;

public class SplitterHandler
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        byte[] bs = new byte[3];
        for (int i = 0; i < bs.length; ++i) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }
            bs[i] = byteBuf.readByte();
            if (bs[i] < 0) continue;
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(bs));
            try {
                int j = packetByteBuf.readVarInt();
                if (byteBuf.readableBytes() < j) {
                    byteBuf.resetReaderIndex();
                    return;
                }
                list.add(byteBuf.readBytes(j));
                return;
            } finally {
                packetByteBuf.release();
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

