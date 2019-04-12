package net.minecraft.client.network.packet;

import java.io.IOException;
import java.security.PublicKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
	private String serverId;
	private PublicKey publicKey;
	private byte[] nonce;

	public LoginHelloS2CPacket() {
	}

	public LoginHelloS2CPacket(String string, PublicKey publicKey, byte[] bs) {
		this.serverId = string;
		this.publicKey = publicKey;
		this.nonce = bs;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.serverId = packetByteBuf.readString(20);
		this.publicKey = NetworkEncryptionUtils.readEncodedPublicKey(packetByteBuf.readByteArray());
		this.nonce = packetByteBuf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.serverId);
		packetByteBuf.writeByteArray(this.publicKey.getEncoded());
		packetByteBuf.writeByteArray(this.nonce);
	}

	public void method_12612(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onHello(this);
	}

	@Environment(EnvType.CLIENT)
	public String getServerId() {
		return this.serverId;
	}

	@Environment(EnvType.CLIENT)
	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	@Environment(EnvType.CLIENT)
	public byte[] getNonce() {
		return this.nonce;
	}
}
