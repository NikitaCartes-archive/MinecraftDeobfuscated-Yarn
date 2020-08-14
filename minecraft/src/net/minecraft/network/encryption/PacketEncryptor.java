package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class PacketEncryptor extends MessageToByteEncoder<ByteBuf> {
	private final PacketEncryptionManager manager;

	public PacketEncryptor(Cipher cipher) {
		this.manager = new PacketEncryptionManager(cipher);
	}

	protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
		this.manager.encrypt(byteBuf, byteBuf2);
	}
}
