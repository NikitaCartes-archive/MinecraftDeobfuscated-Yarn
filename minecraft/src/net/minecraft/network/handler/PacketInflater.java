package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.minecraft.network.encoding.VarInts;

public class PacketInflater extends ByteToMessageDecoder {
	public static final int field_34057 = 2097152;
	/**
	 * The maximum size allowed for a compressed packet. Has value {@value}.
	 */
	public static final int MAXIMUM_PACKET_SIZE = 8388608;
	private final Inflater inflater;
	private int compressionThreshold;
	private boolean rejectsBadPackets;

	public PacketInflater(int compressionThreshold, boolean rejectsBadPackets) {
		this.compressionThreshold = compressionThreshold;
		this.rejectsBadPackets = rejectsBadPackets;
		this.inflater = new Inflater();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
		if (buf.readableBytes() != 0) {
			int i = VarInts.read(buf);
			if (i == 0) {
				objects.add(buf.readBytes(buf.readableBytes()));
			} else {
				if (this.rejectsBadPackets) {
					if (i < this.compressionThreshold) {
						throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
					}

					if (i > 8388608) {
						throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
					}
				}

				this.setInputBuf(buf);
				ByteBuf byteBuf = this.inflate(ctx, i);
				this.inflater.reset();
				objects.add(byteBuf);
			}
		}
	}

	private void setInputBuf(ByteBuf buf) {
		ByteBuffer byteBuffer;
		if (buf.nioBufferCount() > 0) {
			byteBuffer = buf.nioBuffer();
			buf.skipBytes(buf.readableBytes());
		} else {
			byteBuffer = ByteBuffer.allocateDirect(buf.readableBytes());
			buf.readBytes(byteBuffer);
			byteBuffer.flip();
		}

		this.inflater.setInput(byteBuffer);
	}

	private ByteBuf inflate(ChannelHandlerContext context, int expectedSize) throws DataFormatException {
		ByteBuf byteBuf = context.alloc().directBuffer(expectedSize);

		try {
			ByteBuffer byteBuffer = byteBuf.internalNioBuffer(0, expectedSize);
			int i = byteBuffer.position();
			this.inflater.inflate(byteBuffer);
			int j = byteBuffer.position() - i;
			if (j != expectedSize) {
				throw new DecoderException("Badly compressed packet - actual length of uncompressed payload " + j + " is does not match declared size " + expectedSize);
			} else {
				byteBuf.writerIndex(byteBuf.writerIndex() + j);
				return byteBuf;
			}
		} catch (Exception var7) {
			byteBuf.release();
			throw var7;
		}
	}

	public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
		this.compressionThreshold = compressionThreshold;
		this.rejectsBadPackets = rejectsBadPackets;
	}
}
