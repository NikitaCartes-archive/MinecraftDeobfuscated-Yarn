package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;

@Sharable
public class SizePrepender extends MessageToByteEncoder<ByteBuf> {
	/**
	 * The max length, in number of bytes, of the prepending size var int permitted.
	 * Has value {@value}.
	 */
	public static final int MAX_PREPEND_LENGTH = 3;

	protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
		int i = byteBuf.readableBytes();
		int j = VarInts.getSizeInBytes(i);
		if (j > 3) {
			throw new EncoderException("Packet too large: size " + i + " is over 8");
		} else {
			byteBuf2.ensureWritable(j + i);
			VarInts.write(byteBuf2, i);
			byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), i);
		}
	}
}
