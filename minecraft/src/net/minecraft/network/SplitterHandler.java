package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class SplitterHandler extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) {
		buf.markReaderIndex();
		byte[] bs = new byte[3];

		for (int i = 0; i < bs.length; i++) {
			if (!buf.isReadable()) {
				buf.resetReaderIndex();
				return;
			}

			bs[i] = buf.readByte();
			if (bs[i] >= 0) {
				PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(bs));

				try {
					int j = packetByteBuf.readVarInt();
					if (buf.readableBytes() >= j) {
						objects.add(buf.readBytes(j));
						return;
					}

					buf.resetReaderIndex();
				} finally {
					packetByteBuf.release();
				}

				return;
			}
		}

		throw new CorruptedFrameException("length wider than 21-bit");
	}
}
