package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class PacketInflater extends ByteToMessageDecoder {
	public static final int field_34057 = 2097152;
	/**
	 * The maximum size allowed for a compressed packet. Has value {@value}.
	 */
	public static final int MAXIMUM_PACKET_SIZE = 8388608;
	private final Inflater inflater;
	private int compressionThreshold;
	private boolean field_34058;

	public PacketInflater(int compressionThreshold, boolean bl) {
		this.compressionThreshold = compressionThreshold;
		this.field_34058 = bl;
		this.inflater = new Inflater();
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() != 0) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
			int i = packetByteBuf.readVarInt();
			if (i == 0) {
				list.add(packetByteBuf.readBytes(packetByteBuf.readableBytes()));
			} else {
				if (this.field_34058) {
					if (i < this.compressionThreshold) {
						throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
					}

					if (i > 8388608) {
						throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
					}
				}

				byte[] bs = new byte[packetByteBuf.readableBytes()];
				packetByteBuf.readBytes(bs);
				this.inflater.setInput(bs);
				byte[] cs = new byte[i];
				this.inflater.inflate(cs);
				list.add(Unpooled.wrappedBuffer(cs));
				this.inflater.reset();
			}
		}
	}

	public void setCompressionThreshold(int compressionThreshold, boolean bl) {
		this.compressionThreshold = compressionThreshold;
		this.field_34058 = bl;
	}
}
