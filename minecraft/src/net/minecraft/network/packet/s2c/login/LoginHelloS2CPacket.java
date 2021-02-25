package net.minecraft.network.packet.s2c.login;

import java.security.PublicKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.listener.ClientLoginPacketListener;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
	private final String serverId;
	private final byte[] publicKey;
	private final byte[] nonce;

	public LoginHelloS2CPacket(String serverId, byte[] publicKey, byte[] nonce) {
		this.serverId = serverId;
		this.publicKey = publicKey;
		this.nonce = nonce;
	}

	public LoginHelloS2CPacket(PacketByteBuf buf) {
		this.serverId = buf.readString(20);
		this.publicKey = buf.readByteArray();
		this.nonce = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.serverId);
		buf.writeByteArray(this.publicKey);
		buf.writeByteArray(this.nonce);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onHello(this);
	}

	@Environment(EnvType.CLIENT)
	public String getServerId() {
		return this.serverId;
	}

	@Environment(EnvType.CLIENT)
	public PublicKey getPublicKey() throws NetworkEncryptionException {
		return NetworkEncryptionUtils.readEncodedPublicKey(this.publicKey);
	}

	@Environment(EnvType.CLIENT)
	public byte[] getNonce() {
		return this.nonce;
	}
}
