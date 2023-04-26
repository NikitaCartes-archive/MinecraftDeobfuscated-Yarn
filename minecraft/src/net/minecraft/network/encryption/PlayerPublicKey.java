package net.minecraft.network.encryption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
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
	public static final Text EXPIRED_PUBLIC_KEY_TEXT = Text.translatable("multiplayer.disconnect.expired_public_key");
	private static final Text INVALID_PUBLIC_KEY_SIGNATURE_TEXT = Text.translatable("multiplayer.disconnect.invalid_public_key_signature.new");
	public static final Duration EXPIRATION_GRACE_PERIOD = Duration.ofHours(8L);
	public static final Codec<PlayerPublicKey> CODEC = PlayerPublicKey.PublicKeyData.CODEC.xmap(PlayerPublicKey::new, PlayerPublicKey::data);

	/**
	 * Verifies the public key and decodes it.
	 * 
	 * <p>The checks whether the public key is present, signed with the Mojang's private key,
	 * and not expired (taking into account the provided grace period).
	 * 
	 * @throws PublicKeyException when the key is expired or malformed
	 */
	public static PlayerPublicKey verifyAndDecode(
		SignatureVerifier servicesSignatureVerifier, UUID playerUuid, PlayerPublicKey.PublicKeyData publicKeyData, Duration gracePeriod
	) throws PlayerPublicKey.PublicKeyException {
		if (publicKeyData.isExpired(gracePeriod)) {
			throw new PlayerPublicKey.PublicKeyException(EXPIRED_PUBLIC_KEY_TEXT);
		} else if (!publicKeyData.verifyKey(servicesSignatureVerifier, playerUuid)) {
			throw new PlayerPublicKey.PublicKeyException(INVALID_PUBLIC_KEY_SIGNATURE_TEXT);
		} else {
			return new PlayerPublicKey(publicKeyData);
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
						Codecs.BASE_64.fieldOf("signature_v2").forGetter(PlayerPublicKey.PublicKeyData::keySignature)
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

		boolean verifyKey(SignatureVerifier servicesSignatureVerifier, UUID playerUuid) {
			return servicesSignatureVerifier.validate(this.toSerializedString(playerUuid), this.keySignature);
		}

		private byte[] toSerializedString(UUID playerUuid) {
			byte[] bs = this.key.getEncoded();
			byte[] cs = new byte[24 + bs.length];
			ByteBuffer byteBuffer = ByteBuffer.wrap(cs).order(ByteOrder.BIG_ENDIAN);
			byteBuffer.putLong(playerUuid.getMostSignificantBits()).putLong(playerUuid.getLeastSignificantBits()).putLong(this.expiresAt.toEpochMilli()).put(bs);
			return cs;
		}

		public boolean isExpired() {
			return this.expiresAt.isBefore(Instant.now());
		}

		/**
		 * {@return whether the key is expired, with the provided grace period taken into account}
		 */
		public boolean isExpired(Duration gracePeriod) {
			return this.expiresAt.plus(gracePeriod).isBefore(Instant.now());
		}

		public boolean equals(Object o) {
			return !(o instanceof PlayerPublicKey.PublicKeyData publicKeyData)
				? false
				: this.expiresAt.equals(publicKeyData.expiresAt) && this.key.equals(publicKeyData.key) && Arrays.equals(this.keySignature, publicKeyData.keySignature);
		}
	}

	public static class PublicKeyException extends TextifiedException {
		public PublicKeyException(Text text) {
			super(text);
		}
	}
}
