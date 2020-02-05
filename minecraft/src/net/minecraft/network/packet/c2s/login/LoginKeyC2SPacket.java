package net.minecraft.network.packet.c2s.login;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener> {
	private byte[] encryptedSecretKey = new byte[0];
	private byte[] encryptedNonce = new byte[0];

	public LoginKeyC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.encryptedNonce = NetworkEncryptionUtils.encrypt(publicKey, nonce);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.encryptedSecretKey = buf.readByteArray();
		this.encryptedNonce = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByteArray(this.encryptedSecretKey);
		buf.writeByteArray(this.encryptedNonce);
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey decryptSecretKey(PrivateKey privateKey) {
		return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
	}

	public byte[] decryptNonce(PrivateKey privateKey) {
		return privateKey == null ? this.encryptedNonce : NetworkEncryptionUtils.decrypt(privateKey, this.encryptedNonce);
	}
}
