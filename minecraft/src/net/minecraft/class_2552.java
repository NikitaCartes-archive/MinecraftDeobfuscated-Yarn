package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class class_2552 extends MessageToByteEncoder<ByteBuf> {
	protected void method_10840(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
		int i = byteBuf.readableBytes();
		int j = class_2540.method_10815(i);
		if (j > 3) {
			throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
		} else {
			class_2540 lv = new class_2540(byteBuf2);
			lv.ensureWritable(j + i);
			lv.method_10804(i);
			lv.writeBytes(byteBuf, byteBuf.readerIndex(), i);
		}
	}
}
