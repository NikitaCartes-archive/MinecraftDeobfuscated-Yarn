package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class class_2532 extends ByteToMessageDecoder {
	private final Inflater field_11622;
	private int field_11623;

	public class_2532(int i) {
		this.field_11623 = i;
		this.field_11622 = new Inflater();
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() != 0) {
			class_2540 lv = new class_2540(byteBuf);
			int i = lv.method_10816();
			if (i == 0) {
				list.add(lv.readBytes(lv.readableBytes()));
			} else {
				if (i < this.field_11623) {
					throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.field_11623);
				}

				if (i > 2097152) {
					throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
				}

				byte[] bs = new byte[lv.readableBytes()];
				lv.readBytes(bs);
				this.field_11622.setInput(bs);
				byte[] cs = new byte[i];
				this.field_11622.inflate(cs);
				list.add(Unpooled.wrappedBuffer(cs));
				this.field_11622.reset();
			}
		}
	}

	public void method_10739(int i) {
		this.field_11623 = i;
	}
}
