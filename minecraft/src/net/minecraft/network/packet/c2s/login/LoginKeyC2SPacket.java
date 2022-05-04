package net.minecraft.network.packet.c2s.login;

import com.mojang.datafixers.util.Either;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ServerLoginPacketListener;

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
	private final Either<byte[], NetworkEncryptionUtils.SignatureData> nonce;

	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws NetworkEncryptionException {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.nonce = Either.left(NetworkEncryptionUtils.encrypt(publicKey, nonce));
	}

	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, long seed, byte[] signature) throws NetworkEncryptionException {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.nonce = Either.right(new NetworkEncryptionUtils.SignatureData(seed, signature));
	}

	public LoginKeyC2SPacket(PacketByteBuf buf) {
		this.encryptedSecretKey = buf.readByteArray();
		this.nonce = buf.readEither(PacketByteBuf::readByteArray, NetworkEncryptionUtils.SignatureData::new);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByteArray(this.encryptedSecretKey);
		buf.writeEither(this.nonce, PacketByteBuf::writeByteArray, (buf2, signature) -> signature.write(buf2));
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey decryptSecretKey(PrivateKey privateKey) throws NetworkEncryptionException {
		return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
	}

	public boolean verifySignedNonce(byte[] nonce, PlayerPublicKey publicKeyInfo) {
		return this.nonce.<Boolean>map(encrypted -> false, signature -> {
			try {
				Signature signature2 = publicKeyInfo.createSignatureInstance();
				signature2.update(nonce);
				signature2.update(signature.getSalt());
				return signature2.verify(signature.signature());
			} catch (NetworkEncryptionException | GeneralSecurityException var4) {
				return false;
			}
		});
	}

	public boolean verifyEncryptedNonce(byte[] nonce, PrivateKey privateKey) {
		Optional<byte[]> optional = this.nonce.left();

		try {
			return optional.isPresent() && Arrays.equals(nonce, NetworkEncryptionUtils.decrypt(privateKey, (byte[])optional.get()));
		} catch (NetworkEncryptionException var5) {
			return false;
		}
	}
}
