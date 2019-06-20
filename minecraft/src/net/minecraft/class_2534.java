package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class class_2534 extends MessageToByteEncoder<ByteBuf> {
	private final byte[] field_11637 = new byte[8192];
	private final Deflater field_11638;
	private int field_11636;

	public class_2534(int i) {
		this.field_11636 = i;
		this.field_11638 = new Deflater();
	}

	protected void method_10741(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
		int i = byteBuf.readableBytes();
		class_2540 lv = new class_2540(byteBuf2);
		if (i < this.field_11636) {
			lv.method_10804(0);
			lv.writeBytes(byteBuf);
		} else {
			byte[] bs = new byte[i];
			byteBuf.readBytes(bs);
			lv.method_10804(bs.length);
			this.field_11638.setInput(bs, 0, i);
			this.field_11638.finish();

			while (!this.field_11638.finished()) {
				int j = this.field_11638.deflate(this.field_11637);
				lv.writeBytes(this.field_11637, 0, j);
			}

			this.field_11638.reset();
		}
	}

	public void method_10742(int i) {
		this.field_11636 = i;
	}
}
