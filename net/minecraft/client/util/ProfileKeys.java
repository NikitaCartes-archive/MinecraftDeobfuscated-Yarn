/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A class to fetch, load, and save the player's public and private keys.
 */
@Environment(value=EnvType.CLIENT)
public class ProfileKeys {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path PROFILE_KEYS_PATH = Path.of("profilekeys", new String[0]);
    private final UserApiService field_39958;
    private final Path jsonPath;
    private CompletableFuture<Optional<class_7653>> field_39959;

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
    private CompletableFuture<Optional<class_7653>> getKeyPair() {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PlayerKeyPair> optional = this.loadKeyPairFromFile().filter(keyPair -> !keyPair.publicKey().data().isExpired());
            if (optional.isPresent() && !optional.get().isExpired()) {
                if (!SharedConstants.isDevelopment) {
                    this.saveKeyPairToFile(null);
                } else {
                    return optional;
                }
            }
            try {
                PlayerKeyPair playerKeyPair = this.fetchKeyPair(this.field_39958);
                this.saveKeyPairToFile(playerKeyPair);
                return Optional.of(playerKeyPair);
            } catch (MinecraftClientException | IOException | NetworkEncryptionException exception) {
                LOGGER.error("Failed to retrieve profile key pair", exception);
                this.saveKeyPairToFile(null);
                return optional;
            }
        }, Util.getMainWorkerExecutor()).thenApply(optional -> optional.map(class_7653::new));
    }

    /**
     * {@return the profile keys from the local cache}
     * 
     * <p>This can return expired keys.
     * 
     * @implNote The cache file is stored at {@code .minecraft/profilekeys/<uuid>.json}.
     */
    private Optional<PlayerKeyPair> loadKeyPairFromFile() {
        Optional<PlayerKeyPair> optional;
        block10: {
            if (this.field_39959.isDone()) {
                return this.field_39959.join().map(class_7653::keyPair);
            }
            if (Files.notExists(this.jsonPath, new LinkOption[0])) {
                return Optional.empty();
            }
            BufferedReader bufferedReader = Files.newBufferedReader(this.jsonPath);
            try {
                optional = PlayerKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader)).result();
                if (bufferedReader == null) break block10;
            } catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Exception exception) {
                    LOGGER.error("Failed to read profile key pair file {}", (Object)this.jsonPath, (Object)exception);
                    return Optional.empty();
                }
            }
            bufferedReader.close();
        }
        return optional;
    }

    /**
     * Saves the {@code keyPair} to the cache file.
     */
    private void saveKeyPairToFile(@Nullable PlayerKeyPair keyPair) {
        try {
            Files.deleteIfExists(this.jsonPath);
        } catch (IOException iOException) {
            LOGGER.error("Failed to delete profile key pair file {}", (Object)this.jsonPath, (Object)iOException);
        }
        if (keyPair == null) {
            return;
        }
        if (!SharedConstants.isDevelopment) {
            return;
        }
        PlayerKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, keyPair).result().ifPresent(json -> {
            try {
                Files.createDirectories(this.jsonPath.getParent(), new FileAttribute[0]);
                Files.writeString(this.jsonPath, (CharSequence)json.toString(), new OpenOption[0]);
            } catch (Exception exception) {
                LOGGER.error("Failed to write profile key pair file {}", (Object)this.jsonPath, (Object)exception);
            }
        });
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
            PlayerPublicKey.PublicKeyData publicKeyData = ProfileKeys.decodeKeyPairResponse(keyPairResponse);
            return new PlayerKeyPair(NetworkEncryptionUtils.decodeRsaPrivateKeyPem(keyPairResponse.getPrivateKey()), new PlayerPublicKey(publicKeyData), Instant.parse(keyPairResponse.getRefreshedAfter()));
        }
        throw new IOException("Could not retrieve profile key pair");
    }

    /**
     * {@return {@code keyPairResponse} decoded to {@link PlayerPublicKey.PublicKeyData}}
     * 
     * @throws NetworkEncryptionException when the response is malformed
     */
    private static PlayerPublicKey.PublicKeyData decodeKeyPairResponse(KeyPairResponse keyPairResponse) throws NetworkEncryptionException {
        if (Strings.isNullOrEmpty(keyPairResponse.getPublicKey()) || keyPairResponse.getPublicKeySignature() == null || keyPairResponse.getPublicKeySignature().array().length == 0) {
            throw new NetworkEncryptionException(new InsecurePublicKeyException.MissingException());
        }
        try {
            Instant instant = Instant.parse(keyPairResponse.getExpiresAt());
            PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(keyPairResponse.getPublicKey());
            ByteBuffer byteBuffer = keyPairResponse.getPublicKeySignature();
            return new PlayerPublicKey.PublicKeyData(instant, publicKey, byteBuffer.array());
        } catch (IllegalArgumentException | DateTimeException runtimeException) {
            throw new NetworkEncryptionException(runtimeException);
        }
    }

    /**
     * {@return the signer, or {@code null} if there is no key pair associated with the profile}
     */
    @Nullable
    public Signer getSigner() {
        return this.field_39959.join().map(class_7653::signer).orElse(null);
    }

    /**
     * {@return the public key, or {@code null} if there is no public key associated
     * with the profile}
     */
    public Optional<PlayerPublicKey> getPublicKey() {
        return this.field_39959.join().map(arg -> arg.keyPair().publicKey());
    }

    @Environment(value=EnvType.CLIENT)
    record class_7653(PlayerKeyPair keyPair, Signer signer) {
        public class_7653(PlayerKeyPair playerKeyPair) {
            this(playerKeyPair, Signer.create(playerKeyPair.privateKey(), "SHA256withRSA"));
        }
    }
}

