package net.minecraft.network.encryption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
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
	public static final Text field_39953 = Text.translatable("multiplayer.disconnect.missing_public_key");
	public static final Text field_39954 = Text.translatable("multiplayer.disconnect.expired_public_key");
	private static final Text field_39956 = Text.translatable("multiplayer.disconnect.invalid_public_key_signature");
	public static final Duration field_39955 = Duration.ofHours(8L);
	public static final Codec<PlayerPublicKey> CODEC = PlayerPublicKey.PublicKeyData.CODEC.xmap(PlayerPublicKey::new, PlayerPublicKey::data);

	/**
	 * Verifies the public key and decodes it.
	 * 
	 * <p>The checks whether the public key is present, signed with the Mojang's private key,
	 * and not expired.
	 * 
	 * @throws InsecurePublicKeyException.MissingException when the key is missing or empty
	 * @throws InsecurePublicKeyException.InvalidException when the key does not belong to the profile, is unsigned, or expired
	 * @throws NetworkEncryptionException when the key is malformed
	 */
	public static PlayerPublicKey verifyAndDecode(
		SignatureVerifier servicesSignatureVerifier, UUID playerUuid, PlayerPublicKey.PublicKeyData publicKeyData, Duration duration
	) throws PlayerPublicKey.class_7652 {
		if (publicKeyData.method_45103(duration)) {
			throw new PlayerPublicKey.class_7652(field_39954);
		} else if (!publicKeyData.verifyKey(servicesSignatureVerifier, playerUuid)) {
			throw new PlayerPublicKey.class_7652(field_39956);
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

		public boolean method_45103(Duration duration) {
			return this.expiresAt.plus(duration).isBefore(Instant.now());
		}
	}

	public static class class_7652 extends TextifiedException {
		public class_7652(Text text) {
			super(text);
		}
	}
}
