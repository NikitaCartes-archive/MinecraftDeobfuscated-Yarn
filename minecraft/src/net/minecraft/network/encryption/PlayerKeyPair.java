package net.minecraft.network.encryption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.PrivateKey;
import java.time.Instant;
import net.minecraft.util.dynamic.Codecs;

/**
 * An RSA key pair for a player.
 * 
 * <p>Users cannot generate the keys themselves; this must be provided from Mojang's
 * authentication server.
 * 
 * @see net.minecraft.client.util.ProfileKeys
 * @see PlayerPublicKey
 */
public record PlayerKeyPair(PrivateKey privateKey, PlayerPublicKey publicKey, Instant refreshedAfter) {
	public static final Codec<PlayerKeyPair> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NetworkEncryptionUtils.RSA_PRIVATE_KEY_CODEC.fieldOf("private_key").forGetter(PlayerKeyPair::privateKey),
					PlayerPublicKey.CODEC.fieldOf("public_key").forGetter(PlayerKeyPair::publicKey),
					Codecs.INSTANT.fieldOf("refreshed_after").forGetter(PlayerKeyPair::refreshedAfter)
				)
				.apply(instance, PlayerKeyPair::new)
	);

	/**
	 * {@return whether the keys are expired and can no longer be used}
	 */
	public boolean isExpired() {
		return this.refreshedAfter.isBefore(Instant.now());
	}
}
