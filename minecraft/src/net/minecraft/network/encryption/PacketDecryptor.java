package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class PacketDecryptor extends MessageToMessageDecoder<ByteBuf> {
	private final PacketEncryptionManager manager;

	public PacketDecryptor(Cipher cipher) {
		this.manager = new PacketEncryptionManager(cipher);
	}

	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		list.add(this.manager.decrypt(channelHandlerContext, byteBuf));
	}
}
