package net.minecraft.network.encryption;

import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.InvalidException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.dynamic.Codecs;

/**
 * An RSA public key for a player, signed by the Mojang's server.
 * 
 * <p>Users cannot generate the keys themselves; this must be provided from Mojang's
 * authentication server.
 * 
 * @see net.minecraft.client.util.ProfileKeys
 * @see PlayerKeyPair
 */
public record PlayerPublicKey(PlayerPublicKey.PublicKeyData data) {
	public static final Codec<PlayerPublicKey> CODEC = PlayerPublicKey.PublicKeyData.CODEC.comapFlatMap(publicKeyData -> {
		try {
			return DataResult.success(fromKeyData(publicKeyData));
		} catch (NetworkEncryptionException var2) {
			return DataResult.error("Malformed public key");
		}
	}, PlayerPublicKey::data);

	public static PlayerPublicKey fromKeyData(PlayerPublicKey.PublicKeyData publicKeyData) throws NetworkEncryptionException {
		return new PlayerPublicKey(publicKeyData);
	}

	/**
	 * Verifies the public key and decodes it.
	 * 
	 * <p>The checks whether the public key is present, signed with the Mojang's private key,
	 * and not expired.
	 * 
	 * @throws InsecurePublicKeyException.MissingException when the key is missing or empty
	 * @throws InsecurePublicKeyException.InvalidException when the key is unsigned or expired
	 * @throws NetworkEncryptionException when the key is malformed
	 */
	public static PlayerPublicKey verifyAndDecode(SignatureVerifier servicesSignatureVerifier, PlayerPublicKey.PublicKeyData publicKeyData) throws InsecurePublicKeyException, NetworkEncryptionException {
		if (publicKeyData.isExpired()) {
			throw new InvalidException("Expired profile public key");
		} else if (!publicKeyData.verifyKey(servicesSignatureVerifier)) {
			throw new InvalidException("Invalid profile public key signature");
		} else {
			return fromKeyData(publicKeyData);
		}
	}

	public SignatureVerifier createSignatureInstance() {
		return SignatureVerifier.create(this.data.key, "SHA256withRSA");
	}

	public static record PublicKeyData(Instant expiresAt, PublicKey key, byte[] keySignature) {
		private static final int KEY_SIGNATURE_MAX_SIZE = 4096;
		public static final Codec<PlayerPublicKey.PublicKeyData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.INSTANT.fieldOf("expires_at").forGetter(PlayerPublicKey.PublicKeyData::expiresAt),
						NetworkEncryptionUtils.RSA_PUBLIC_KEY_CODEC.fieldOf("key").forGetter(PlayerPublicKey.PublicKeyData::key),
						Codecs.BASE_64.fieldOf("signature").forGetter(PlayerPublicKey.PublicKeyData::keySignature)
					)
					.apply(instance, PlayerPublicKey.PublicKeyData::new)
		);

		public PublicKeyData(PacketByteBuf buf) {
			this(buf.readInstant(), buf.readPublicKey(), buf.readByteArray(4096));
		}

		public void write(PacketByteBuf buf) {
			buf.writeInstant(this.expiresAt);
			buf.writePublicKey(this.key);
			buf.writeByteArray(this.keySignature);
		}

		boolean verifyKey(SignatureVerifier servicesSignatureVerifier) {
			return servicesSignatureVerifier.validate(this.toSerializedString().getBytes(StandardCharsets.US_ASCII), this.keySignature);
		}

		private String toSerializedString() {
			String string = NetworkEncryptionUtils.encodeRsaPublicKey(this.key);
			return this.expiresAt.toEpochMilli() + string;
		}

		public boolean isExpired() {
			return this.expiresAt.isBefore(Instant.now());
		}
	}
}
