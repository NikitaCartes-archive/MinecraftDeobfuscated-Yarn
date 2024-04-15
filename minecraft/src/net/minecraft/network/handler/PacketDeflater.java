package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import net.minecraft.network.encoding.VarInts;

public class PacketDeflater extends MessageToByteEncoder<ByteBuf> {
	private final byte[] deflateBuffer = new byte[8192];
	private final Deflater deflater;
	private int compressionThreshold;

	public PacketDeflater(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
		this.deflater = new Deflater();
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
		int i = byteBuf.readableBytes();
		if (i > 8388608) {
			throw new IllegalArgumentException("Packet too big (is " + i + ", should be less than 8388608)");
		} else {
			if (i < this.compressionThreshold) {
				VarInts.write(byteBuf2, 0);
				byteBuf2.writeBytes(byteBuf);
			} else {
				byte[] bs = new byte[i];
				byteBuf.readBytes(bs);
				VarInts.write(byteBuf2, bs.length);
				this.deflater.setInput(bs, 0, i);
				this.deflater.finish();

				while (!this.deflater.finished()) {
					int j = this.deflater.deflate(this.deflateBuffer);
					byteBuf2.writeBytes(this.deflateBuffer, 0, j);
				}

				this.deflater.reset();
			}
		}
	}

	public int getCompressionThreshold() {
		return this.compressionThreshold;
	}

	public void setCompressionThreshold(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}
}
