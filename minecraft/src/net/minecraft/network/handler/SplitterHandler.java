package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.encoding.VarInts;

public class SplitterHandler extends ByteToMessageDecoder {
	private static final int LENGTH_BYTES = 3;
	private final ByteBuf reusableBuf = Unpooled.directBuffer(3);

	@Override
	protected void handlerRemoved0(ChannelHandlerContext context) {
		this.reusableBuf.release();
	}

	private static boolean shouldSplit(ByteBuf source, ByteBuf sizeBuf) {
		for (int i = 0; i < 3; i++) {
			if (!source.isReadable()) {
				return false;
			}

			byte b = source.readByte();
			sizeBuf.writeByte(b);
			if (!VarInts.shouldContinueRead(b)) {
				return true;
			}
		}

		throw new CorruptedFrameException("length wider than 21-bit");
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) {
		buf.markReaderIndex();
		this.reusableBuf.clear();
		if (!shouldSplit(buf, this.reusableBuf)) {
			buf.resetReaderIndex();
		} else {
			int i = VarInts.read(this.reusableBuf);
			if (buf.readableBytes() < i) {
				buf.resetReaderIndex();
			} else {
				objects.add(buf.readBytes(i));
			}
		}
	}
}
