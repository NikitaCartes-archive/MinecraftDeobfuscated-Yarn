package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class SizePrepender extends MessageToByteEncoder<ByteBuf> {
	/**
	 * The max length, in number of bytes, of the prepending size var int permitted.
	 * Has value {@value}.
	 */
	private static final int MAX_PREPEND_LENGTH = 3;

	protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
		int i = byteBuf.readableBytes();
		int j = PacketByteBuf.getVarIntLength(i);
		if (j > 3) {
			throw new IllegalArgumentException("unable to fit " + i + " into 3");
		} else {
			PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf2);
			packetByteBuf.ensureWritable(j + i);
			packetByteBuf.writeVarInt(i);
			packetByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), i);
		}
	}
}
