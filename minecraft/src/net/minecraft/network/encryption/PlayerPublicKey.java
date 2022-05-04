package net.minecraft.network.encryption;

import com.google.common.base.Strings;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.InvalidException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.authlib.properties.Property;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
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
public record PlayerPublicKey(PlayerPublicKey.PublicKeyData data, PublicKey key) {
	public static final Codec<PlayerPublicKey> CODEC = PlayerPublicKey.PublicKeyData.CODEC.comapFlatMap(publicKeyData -> {
		try {
			return DataResult.success(fromKeyData(publicKeyData));
		} catch (NetworkEncryptionException var2) {
			return DataResult.error("Malformed public key");
		}
	}, PlayerPublicKey::data);
	private static final String PUBLIC_KEY = "publicKey";

	public static PlayerPublicKey fromKeyData(PlayerPublicKey.PublicKeyData publicKeyData) throws NetworkEncryptionException {
		return new PlayerPublicKey(publicKeyData, publicKeyData.decodeKey());
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
	public static PlayerPublicKey verifyAndDecode(MinecraftSessionService sessionService, PlayerPublicKey.PublicKeyData publicKeyData) throws InsecurePublicKeyException, NetworkEncryptionException {
		if (Strings.isNullOrEmpty(publicKeyData.key())) {
			throw new MissingException();
		} else if (publicKeyData.isExpired()) {
			throw new InvalidException("Expired profile public key");
		} else {
			String string = sessionService.getSecurePropertyValue(publicKeyData.toProperty());
			if (!publicKeyData.toSerializedString().equals(string)) {
				throw new InvalidException("Invalid profile public key signature");
			} else {
				return fromKeyData(publicKeyData);
			}
		}
	}

	public Signature createSignatureInstance() throws NetworkEncryptionException {
		try {
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(this.key);
			return signature;
		} catch (GeneralSecurityException var2) {
			throw new NetworkEncryptionException(var2);
		}
	}

	public static record PublicKeyData(Instant expiresAt, String key, String signature) {
		public static final Codec<PlayerPublicKey.PublicKeyData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.INSTANT.fieldOf("expires_at").forGetter(PlayerPublicKey.PublicKeyData::expiresAt),
						Codec.STRING.fieldOf("key").forGetter(PlayerPublicKey.PublicKeyData::key),
						Codec.STRING.fieldOf("signature").forGetter(PlayerPublicKey.PublicKeyData::signature)
					)
					.apply(instance, PlayerPublicKey.PublicKeyData::new)
		);

		public Property toProperty() {
			return new Property("publicKey", this.toSerializedString(), this.signature);
		}

		public String toSerializedString() {
			return this.expiresAt.toEpochMilli() + this.key;
		}

		public PublicKey decodeKey() throws NetworkEncryptionException {
			return NetworkEncryptionUtils.decodeRsaPublicKeyPem(this.key);
		}

		public boolean isExpired() {
			return this.expiresAt.isBefore(Instant.now());
		}
	}
}
