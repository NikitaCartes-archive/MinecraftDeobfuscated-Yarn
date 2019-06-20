package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class class_2524 {
	private final Cipher field_11612;
	private byte[] field_11613 = new byte[0];
	private byte[] field_11614 = new byte[0];

	protected class_2524(Cipher cipher) {
		this.field_11612 = cipher;
	}

	private byte[] method_10733(ByteBuf byteBuf) {
		int i = byteBuf.readableBytes();
		if (this.field_11613.length < i) {
			this.field_11613 = new byte[i];
		}

		byteBuf.readBytes(this.field_11613, 0, i);
		return this.field_11613;
	}

	protected ByteBuf method_10734(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws ShortBufferException {
		int i = byteBuf.readableBytes();
		byte[] bs = this.method_10733(byteBuf);
		ByteBuf byteBuf2 = channelHandlerContext.alloc().heapBuffer(this.field_11612.getOutputSize(i));
		byteBuf2.writerIndex(this.field_11612.update(bs, 0, i, byteBuf2.array(), byteBuf2.arrayOffset()));
		return byteBuf2;
	}

	protected void method_10732(ByteBuf byteBuf, ByteBuf byteBuf2) throws ShortBufferException {
		int i = byteBuf.readableBytes();
		byte[] bs = this.method_10733(byteBuf);
		int j = this.field_11612.getOutputSize(i);
		if (this.field_11614.length < j) {
			this.field_11614 = new byte[j];
		}

		byteBuf2.writeBytes(this.field_11614, 0, this.field_11612.update(bs, 0, i, this.field_11614));
	}
}
