package net.minecraft.network.packet.c2s.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener> {
	private final byte[] encryptedSecretKey;
	private final byte[] encryptedNonce;

	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws NetworkEncryptionException {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.encryptedNonce = NetworkEncryptionUtils.encrypt(publicKey, nonce);
	}

	public LoginKeyC2SPacket(PacketByteBuf buf) {
		this.encryptedSecretKey = buf.readByteArray();
		this.encryptedNonce = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByteArray(this.encryptedSecretKey);
		buf.writeByteArray(this.encryptedNonce);
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey decryptSecretKey(PrivateKey privateKey) throws NetworkEncryptionException {
		return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
	}

	public byte[] decryptNonce(PrivateKey privateKey) throws NetworkEncryptionException {
		return NetworkEncryptionUtils.decrypt(privateKey, this.encryptedNonce);
	}
}
