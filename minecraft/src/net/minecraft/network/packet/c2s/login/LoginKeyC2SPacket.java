package net.minecraft.network.packet.c2s.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.SecretKey;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener> {
	private final byte[] encryptedSecretKey;
	/**
	 * The nonce value.
	 * 
	 * @implNote This value is either encrypted (the left side of {@code Either}) or signed
	 * (the right side). If encrypted, then it must be done so using the server's public key
	 * and the server verifies it by decrypting and comparing nonces. If signed, then it must
	 * be done so using the user's private key provided from Mojang's server, and the server
	 * verifies by checking if the reconstructed data can be verified using the public key.
	 */
	private final byte[] nonce;

	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws NetworkEncryptionException {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.nonce = NetworkEncryptionUtils.encrypt(publicKey, nonce);
	}

	public LoginKeyC2SPacket(PacketByteBuf buf) {
		this.encryptedSecretKey = buf.readByteArray();
		this.nonce = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByteArray(this.encryptedSecretKey);
		buf.writeByteArray(this.nonce);
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey decryptSecretKey(PrivateKey privateKey) throws NetworkEncryptionException {
		return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
	}

	public boolean verifySignedNonce(byte[] nonce, PrivateKey privateKey) {
		try {
			return Arrays.equals(nonce, NetworkEncryptionUtils.decrypt(privateKey, this.nonce));
		} catch (NetworkEncryptionException var4) {
			return false;
		}
	}
}
