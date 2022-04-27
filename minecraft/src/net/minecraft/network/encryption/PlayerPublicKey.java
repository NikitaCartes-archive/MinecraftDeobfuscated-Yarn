package net.minecraft.network.encryption;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.InvalidException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Optional;
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
public record PlayerPublicKey(Instant expiresAt, String keyString, String signature) {
	public static final Codec<PlayerPublicKey> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.INSTANT.fieldOf("expires_at").forGetter(PlayerPublicKey::expiresAt),
					Codec.STRING.fieldOf("key").forGetter(PlayerPublicKey::keyString),
					Codec.STRING.fieldOf("signature").forGetter(PlayerPublicKey::signature)
				)
				.apply(instance, PlayerPublicKey::new)
	);
	private static final String PUBLIC_KEY = "publicKey";

	public PlayerPublicKey(Pair<Instant, String> expiresAtAndKey, String signature) {
		this(expiresAtAndKey.getFirst(), expiresAtAndKey.getSecond(), signature);
	}

	/**
	 * {@return the public key extracted from the {@code gameProfile}}
	 * 
	 * @apiNote This can return an {@linkplain java.util.Optional#empty() empty value}
	 * if the profile lacks the public key.
	 */
	public static Optional<PlayerPublicKey> fromGameProfile(GameProfile gameProfile) {
		Property property = Iterables.getFirst(gameProfile.getProperties().get("publicKey"), null);
		if (property == null) {
			return Optional.empty();
		} else {
			String string = property.getValue();
			String string2 = property.getSignature();
			return !Strings.isNullOrEmpty(string) && !Strings.isNullOrEmpty(string2) ? parse(string).map(pair -> new PlayerPublicKey(pair, string2)) : Optional.empty();
		}
	}

	/**
	 * Writes the public key to the {@code gameProfile} properties under the
	 * {@value PUBLIC_KEY} key.
	 */
	public GameProfile write(GameProfile gameProfile) {
		gameProfile.getProperties().put("publicKey", this.toProperty());
		return gameProfile;
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
	public PlayerPublicKey.PublicKeyData verifyAndDecode(MinecraftSessionService sessionService) throws InsecurePublicKeyException, NetworkEncryptionException {
		if (Strings.isNullOrEmpty(this.keyString)) {
			throw new MissingException();
		} else {
			String string = sessionService.getSecurePropertyValue(this.toProperty());
			if (!(this.expiresAt.toEpochMilli() + this.keyString).equals(string)) {
				throw new InvalidException("Invalid profile public key signature");
			} else {
				Pair<Instant, String> pair = (Pair<Instant, String>)parse(string).orElseThrow(() -> new InvalidException("Invalid profile public key"));
				if (pair.getFirst().isBefore(Instant.now())) {
					throw new InvalidException("Expired profile public key");
				} else {
					PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(pair.getSecond());
					return new PlayerPublicKey.PublicKeyData(publicKey, this);
				}
			}
		}
	}

	/**
	 * {@return the expiry time and the key content parsed from {@code contents}}
	 */
	private static Optional<Pair<Instant, String>> parse(String contents) {
		int i = contents.indexOf("-----BEGIN RSA PUBLIC KEY-----");

		long l;
		try {
			l = Long.parseLong(contents.substring(0, i));
		} catch (NumberFormatException var5) {
			return Optional.empty();
		}

		return Optional.of(Pair.of(Instant.ofEpochMilli(l), contents.substring(i)));
	}

	/**
	 * {@return the public key as an authlib {@code Property}}
	 */
	public Property toProperty() {
		return new Property("publicKey", this.expiresAt.toEpochMilli() + this.keyString, this.signature);
	}

	/**
	 * {@return whether the key is expired and can no longer be used}
	 */
	public boolean isExpired() {
		return this.expiresAt.isBefore(Instant.now());
	}

	/**
	 * A record holding a player's RSA public key data.
	 * 
	 * <p>This should not be manually created. Use
	 * {@link PlayerPublicKey#verifyAndDecode(MinecraftSessionService)} to obtain an instance.
	 */
	public static record PublicKeyData(PublicKey key, PlayerPublicKey data) {
		/**
		 * {@return the SHA1withRSA signature instance used for verifying signatures}
		 * 
		 * @apiNote The returned signature cannot be used for signing. Use {#link
		 * net.minecraft.client.util.ProfileKeys#createSignatureInstance()} to create the
		 * signature for signing the data.
		 * 
		 * @throws NetworkEncryptionException when creation fails
		 * 
		 * @see net.minecraft.client.util.ProfileKeys#createSignatureInstance()
		 */
		public Signature createSignatureInstance() throws NetworkEncryptionException {
			try {
				Signature signature = Signature.getInstance("SHA1withRSA");
				signature.initVerify(this.key);
				return signature;
			} catch (GeneralSecurityException var2) {
				throw new NetworkEncryptionException(var2);
			}
		}
	}
}
