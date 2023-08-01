package net.minecraft.client.util;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse.KeyPair;
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
import java.time.Duration;
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
import net.minecraft.util.Util;
import org.slf4j.Logger;

/**
 * A class to fetch, load, and save the player's public and private keys.
 */
@Environment(EnvType.CLIENT)
public class ProfileKeysImpl implements ProfileKeys {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Duration TIME_UNTIL_FIRST_EXPIRY_CHECK = Duration.ofHours(1L);
	private static final Path PROFILE_KEYS_PATH = Path.of("profilekeys");
	private final UserApiService userApiService;
	private final Path jsonPath;
	private CompletableFuture<Optional<PlayerKeyPair>> keyFuture;
	private Instant expiryCheckTime = Instant.EPOCH;

	public ProfileKeysImpl(UserApiService userApiService, UUID uuid, Path root) {
		this.userApiService = userApiService;
		this.jsonPath = root.resolve(PROFILE_KEYS_PATH).resolve(uuid + ".json");
		this.keyFuture = CompletableFuture.supplyAsync(
				() -> this.loadKeyPairFromFile().filter(key -> !key.publicKey().data().isExpired()), Util.getMainWorkerExecutor()
			)
			.thenCompose(this::getKeyPair);
	}

	@Override
	public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair() {
		this.expiryCheckTime = Instant.now().plus(TIME_UNTIL_FIRST_EXPIRY_CHECK);
		this.keyFuture = this.keyFuture.thenCompose(this::getKeyPair);
		return this.keyFuture;
	}

	@Override
	public boolean isExpired() {
		return this.keyFuture.isDone() && Instant.now().isAfter(this.expiryCheckTime)
			? (Boolean)((Optional)this.keyFuture.join()).map(PlayerKeyPair::isExpired).orElse(true)
			: false;
	}

	/**
	 * Gets the key pair from the file cache, or if it is unavailable or expired,
	 * the Mojang server.
	 */
	private CompletableFuture<Optional<PlayerKeyPair>> getKeyPair(Optional<PlayerKeyPair> currentKey) {
		return CompletableFuture.supplyAsync(() -> {
			if (currentKey.isPresent() && !((PlayerKeyPair)currentKey.get()).isExpired()) {
				if (!SharedConstants.isDevelopment) {
					this.saveKeyPairToFile(null);
				}

				return currentKey;
			} else {
				try {
					PlayerKeyPair playerKeyPair = this.fetchKeyPair(this.userApiService);
					this.saveKeyPairToFile(playerKeyPair);
					return Optional.of(playerKeyPair);
				} catch (NetworkEncryptionException | MinecraftClientException | IOException var3) {
					LOGGER.error("Failed to retrieve profile key pair", (Throwable)var3);
					this.saveKeyPairToFile(null);
					return currentKey;
				}
			}
		}, Util.getMainWorkerExecutor());
	}

	/**
	 * {@return the profile keys from the local cache}
	 * 
	 * <p>This can return expired keys.
	 * 
	 * @implNote The cache file is stored at {@code .minecraft/profilekeys/<uuid>.json}.
	 */
	private Optional<PlayerKeyPair> loadKeyPairFromFile() {
		if (Files.notExists(this.jsonPath, new LinkOption[0])) {
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
	 * Saves the {@code keyPair} to the cache file if {@link
	 * net.minecraft.SharedConstants#isDevelopment} is {@code true};
	 * otherwise, just deletes the cache file.
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
				NetworkEncryptionUtils.decodeRsaPrivateKeyPem(keyPairResponse.keyPair().privateKey()),
				new PlayerPublicKey(publicKeyData),
				Instant.parse(keyPairResponse.refreshedAfter())
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
		KeyPair keyPair = keyPairResponse.keyPair();
		if (!Strings.isNullOrEmpty(keyPair.publicKey()) && keyPairResponse.publicKeySignature() != null && keyPairResponse.publicKeySignature().array().length != 0) {
			try {
				Instant instant = Instant.parse(keyPairResponse.expiresAt());
				PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(keyPair.publicKey());
				ByteBuffer byteBuffer = keyPairResponse.publicKeySignature();
				return new PlayerPublicKey.PublicKeyData(instant, publicKey, byteBuffer.array());
			} catch (IllegalArgumentException | DateTimeException var5) {
				throw new NetworkEncryptionException(var5);
			}
		} else {
			throw new NetworkEncryptionException(new MissingException());
		}
	}
}
