package net.minecraft.network.packet.s2c.login;

import java.security.PublicKey;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginHelloS2CPacket> CODEC = Packet.createCodec(LoginHelloS2CPacket::write, LoginHelloS2CPacket::new);
	private final String serverId;
	private final byte[] publicKey;
	private final byte[] nonce;
	private final boolean field_48235;

	public LoginHelloS2CPacket(String serverId, byte[] publicKey, byte[] nonce, boolean bl) {
		this.serverId = serverId;
		this.publicKey = publicKey;
		this.nonce = nonce;
		this.field_48235 = bl;
	}

	private LoginHelloS2CPacket(PacketByteBuf buf) {
		this.serverId = buf.readString(20);
		this.publicKey = buf.readByteArray();
		this.nonce = buf.readByteArray();
		this.field_48235 = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(this.serverId);
		buf.writeByteArray(this.publicKey);
		buf.writeByteArray(this.nonce);
		buf.writeBoolean(this.field_48235);
	}

	@Override
	public PacketIdentifier<LoginHelloS2CPacket> getPacketId() {
		return LoginPackets.HELLO_S2C;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onHello(this);
	}

	public String getServerId() {
		return this.serverId;
	}

	public PublicKey getPublicKey() throws NetworkEncryptionException {
		return NetworkEncryptionUtils.decodeEncodedRsaPublicKey(this.publicKey);
	}

	public byte[] getNonce() {
		return this.nonce;
	}

	public boolean method_56013() {
		return this.field_48235;
	}
}
