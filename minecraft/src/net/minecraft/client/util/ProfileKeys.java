package net.minecraft.client.util;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import net.minecraft.util.Util;
import org.slf4j.Logger;

/**
 * A class to fetch, load, and save the player's public and private keys.
 */
@Environment(EnvType.CLIENT)
public class ProfileKeys {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Path PROFILE_KEYS_PATH = Path.of("profilekeys");
	private final UserApiService field_39958;
	private final Path jsonPath;
	private CompletableFuture<Optional<ProfileKeys.class_7653>> field_39959;

	public ProfileKeys(UserApiService userApiService, UUID uuid, Path path) {
		this.field_39958 = userApiService;
		this.jsonPath = path.resolve(PROFILE_KEYS_PATH).resolve(uuid + ".json");
		this.field_39959 = this.getKeyPair();
	}

	public CompletableFuture<Optional<PlayerPublicKey.PublicKeyData>> method_45104() {
		this.field_39959 = this.getKeyPair();
		return this.field_39959.thenApply(optional -> optional.map(arg -> arg.keyPair().publicKey().data()));
	}

	/**
	 * Gets the key pair from the file cache, or if it is unavailable or expired,
	 * the Mojang server.
	 */
	private CompletableFuture<Optional<ProfileKeys.class_7653>> getKeyPair() {
		return CompletableFuture.supplyAsync(() -> {
			Optional<PlayerKeyPair> optional = this.loadKeyPairFromFile().filter(keyPair -> !keyPair.publicKey().data().isExpired());
			if (optional.isPresent() && !((PlayerKeyPair)optional.get()).isExpired()) {
				if (SharedConstants.isDevelopment) {
					return optional;
				}

				this.saveKeyPairToFile(null);
			}

			try {
				PlayerKeyPair playerKeyPair = this.fetchKeyPair(this.field_39958);
				this.saveKeyPairToFile(playerKeyPair);
				return Optional.of(playerKeyPair);
			} catch (NetworkEncryptionException | MinecraftClientException | IOException var3) {
				LOGGER.error("Failed to retrieve profile key pair", (Throwable)var3);
				this.saveKeyPairToFile(null);
				return optional;
			}
		}, Util.getMainWorkerExecutor()).thenApply(optional -> optional.map(ProfileKeys.class_7653::new));
	}

	/**
	 * {@return the profile keys from the local cache}
	 * 
	 * <p>This can return expired keys.
	 * 
	 * @implNote The cache file is stored at {@code .minecraft/profilekeys/<uuid>.json}.
	 */
	private Optional<PlayerKeyPair> loadKeyPairFromFile() {
		if (this.field_39959.isDone()) {
			return ((Optional)this.field_39959.join()).map(ProfileKeys.class_7653::keyPair);
		} else if (Files.notExists(this.jsonPath, new LinkOption[0])) {
			return Optional.empty();
		} else {
			try {
				BufferedReader bufferedReader = Files.newBufferedReader(this.jsonPath);

				Optional var2;
				try {
					var2 = PlayerKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader)).result();
				} catch (Throwable var5) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

				return var2;
			} catch (Exception var6) {
				LOGGER.error("Failed to read profile key pair file {}", this.jsonPath, var6);
				return Optional.empty();
			}
		}
	}

	/**
	 * Saves the {@code keyPair} to the cache file.
	 */
	private void saveKeyPairToFile(@Nullable PlayerKeyPair keyPair) {
		try {
			Files.deleteIfExists(this.jsonPath);
		} catch (IOException var3) {
			LOGGER.error("Failed to delete profile key pair file {}", this.jsonPath, var3);
		}

		if (keyPair != null) {
			if (SharedConstants.isDevelopment) {
				PlayerKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, keyPair).result().ifPresent(json -> {
					try {
						Files.createDirectories(this.jsonPath.getParent());
						Files.writeString(this.jsonPath, json.toString());
					} catch (Exception var3x) {
						LOGGER.error("Failed to write profile key pair file {}", this.jsonPath, var3x);
					}
				});
			}
		}
	}

	/**
	 * {@return the key pair fetched from Mojang's server}
	 * 
	 * @throws NetworkEncryptionException when the fetched key is malformed
	 * @throws IOException when fetching fails
	 */
	private PlayerKeyPair fetchKeyPair(UserApiService userApiService) throws NetworkEncryptionException, IOException {
		KeyPairResponse keyPairResponse = userApiService.getKeyPair();
		if (keyPairResponse != null) {
			PlayerPublicKey.PublicKeyData publicKeyData = decodeKeyPairResponse(keyPairResponse);
			return new PlayerKeyPair(
				NetworkEncryptionUtils.decodeRsaPrivateKeyPem(keyPairResponse.getPrivateKey()),
				new PlayerPublicKey(publicKeyData),
				Instant.parse(keyPairResponse.getRefreshedAfter())
			);
		} else {
			throw new IOException("Could not retrieve profile key pair");
		}
	}

	/**
	 * {@return {@code keyPairResponse} decoded to {@link PlayerPublicKey.PublicKeyData}}
	 * 
	 * @throws NetworkEncryptionException when the response is malformed
	 */
	private static PlayerPublicKey.PublicKeyData decodeKeyPairResponse(KeyPairResponse keyPairResponse) throws NetworkEncryptionException {
		if (!Strings.isNullOrEmpty(keyPairResponse.getPublicKey())
			&& keyPairResponse.getPublicKeySignature() != null
			&& keyPairResponse.getPublicKeySignature().array().length != 0) {
			try {
				Instant instant = Instant.parse(keyPairResponse.getExpiresAt());
				PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(keyPairResponse.getPublicKey());
				ByteBuffer byteBuffer = keyPairResponse.getPublicKeySignature();
				return new PlayerPublicKey.PublicKeyData(instant, publicKey, byteBuffer.array());
			} catch (IllegalArgumentException | DateTimeException var4) {
				throw new NetworkEncryptionException(var4);
			}
		} else {
			throw new NetworkEncryptionException(new MissingException());
		}
	}

	/**
	 * {@return the signer, or {@code null} if there is no key pair associated with the profile}
	 */
	@Nullable
	public Signer getSigner() {
		return (Signer)((Optional)this.field_39959.join()).map(ProfileKeys.class_7653::signer).orElse(null);
	}

	/**
	 * {@return the public key, or {@code null} if there is no public key associated
	 * with the profile}
	 */
	public Optional<PlayerPublicKey> getPublicKey() {
		return ((Optional)this.field_39959.join()).map(arg -> arg.keyPair().publicKey());
	}

	@Environment(EnvType.CLIENT)
	static record class_7653(PlayerKeyPair keyPair, Signer signer) {
		public class_7653(PlayerKeyPair playerKeyPair) {
			this(playerKeyPair, Signer.create(playerKeyPair.privateKey(), "SHA256withRSA"));
		}
	}
}
