package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class class_2529 extends MessageToByteEncoder<ByteBuf> {
	private final class_2524 field_11620;

	public class_2529(Cipher cipher) {
		this.field_11620 = new class_2524(cipher);
	}

	protected void method_10736(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
		this.field_11620.method_10732(byteBuf, byteBuf2);
	}
}
