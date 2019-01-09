package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class class_2528 extends MessageToMessageDecoder<ByteBuf> {
	private final class_2524 field_11619;

	public class_2528(Cipher cipher) {
		this.field_11619 = new class_2524(cipher);
	}

	protected void method_10735(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		list.add(this.field_11619.method_10734(channelHandlerContext, byteBuf));
	}
}
