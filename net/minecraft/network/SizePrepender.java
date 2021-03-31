/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketByteBuf;

@ChannelHandler.Sharable
public class SizePrepender
extends MessageToByteEncoder<ByteBuf> {
    /**
     * The max length, in number of bytes, of the prepending size var int permitted.
     * Has value {@value}.
     */
    private static final int MAX_PREPEND_LENGTH = 3;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = PacketByteBuf.getVarIntLength(i);
        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf2);
        packetByteBuf.ensureWritable(j + i);
        packetByteBuf.writeVarInt(i);
        packetByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), i);
    }

    @Override
    protected /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        this.encode(channelHandlerContext, (ByteBuf)object, byteBuf);
    }
}

