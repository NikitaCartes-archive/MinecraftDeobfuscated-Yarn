package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class PacketEncryptionManager {
	private final Cipher cipher;
	private byte[] conversionBuffer = new byte[0];
	private byte[] encryptionBuffer = new byte[0];

	protected PacketEncryptionManager(Cipher cipher) {
		this.cipher = cipher;
	}

	private byte[] toByteArray(ByteBuf byteBuf) {
		int i = byteBuf.readableBytes();
		if (this.conversionBuffer.length < i) {
			this.conversionBuffer = new byte[i];
		}

		byteBuf.readBytes(this.conversionBuffer, 0, i);
		return this.conversionBuffer;
	}

	protected ByteBuf decrypt(ChannelHandlerContext context, ByteBuf byteBuf) throws ShortBufferException {
		int i = byteBuf.readableBytes();
		byte[] bs = this.toByteArray(byteBuf);
		ByteBuf byteBuf2 = context.alloc().heapBuffer(this.cipher.getOutputSize(i));
		byteBuf2.writerIndex(this.cipher.update(bs, 0, i, byteBuf2.array(), byteBuf2.arrayOffset()));
		return byteBuf2;
	}

	protected void encrypt(ByteBuf buffer, ByteBuf byteBuf) throws ShortBufferException {
		int i = buffer.readableBytes();
		byte[] bs = this.toByteArray(buffer);
		int j = this.cipher.getOutputSize(i);
		if (this.encryptionBuffer.length < j) {
			this.encryptionBuffer = new byte[j];
		}

		byteBuf.writeBytes(this.encryptionBuffer, 0, this.cipher.update(bs, 0, i, this.encryptionBuffer));
	}
}
