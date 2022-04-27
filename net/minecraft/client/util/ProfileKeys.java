/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
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
    private final Path jsonPath;
    private final CompletableFuture<PlayerKeyPair> keyPairFuture;

    public ProfileKeys(UserApiService userApiService, UUID uuid, Path root) {
        this.jsonPath = root.resolve(PROFILE_KEYS_PATH).resolve(uuid + ".json");
        this.keyPairFuture = this.getKeyPair(userApiService);
    }

    /**
     * Gets the key pair from the file cache, or if it is unavailable or expired,
     * the Mojang server.
     */
    private CompletableFuture<PlayerKeyPair> getKeyPair(UserApiService userApiService) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PlayerKeyPair> optional = this.loadKeyPairFromFile().filter(keyPair -> !keyPair.publicKey().isExpired());
            if (optional.isPresent() && !optional.get().isExpired()) {
                return optional.get();
            }
            try {
                PlayerKeyPair playerKeyPair = this.fetchKeyPair(userApiService);
                this.saveKeyPairToFile(playerKeyPair);
                return playerKeyPair;
            } catch (IOException | NetworkEncryptionException exception) {
                LOGGER.error("Failed to retrieve profile key pair", exception);
                this.saveKeyPairToFile(null);
                return optional.orElse(null);
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
        Optional<PlayerKeyPair> optional;
        block9: {
            if (Files.notExists(this.jsonPath, new LinkOption[0])) {
                return Optional.empty();
            }
            BufferedReader bufferedReader = Files.newBufferedReader(this.jsonPath);
            try {
                optional = PlayerKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader)).result();
                if (bufferedReader == null) break block9;
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
            return new PlayerKeyPair(NetworkEncryptionUtils.decodeRsaPrivateKeyPem(keyPairResponse.getPrivateKey()), new PlayerPublicKey(Instant.parse(keyPairResponse.getExpiresAt()), keyPairResponse.getPublicKey(), keyPairResponse.getPublicKeySignature()), Instant.parse(keyPairResponse.getRefreshedAfter()));
        }
        throw new IOException("Could not retrieve profile key pair");
    }

    /**
     * {@return the SHA1withRSA signature instance used for signing, or {@code null} if
     * there is no private key associated with the profile}
     * 
     * @apiNote Use {#link PlayerPublicKey.PublicKeyData#createSignatureInstance()}
     * to create the signature for verifying the signatures.
     * 
     * @throws GeneralSecurityException when creation fails
     * 
     * @see PlayerPublicKey.PublicKeyData#createSignatureInstance()
     */
    @Nullable
    public Signature createSignatureInstance() throws GeneralSecurityException {
        PrivateKey privateKey = this.getPrivateKey();
        if (privateKey == null) {
            return null;
        }
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        return signature;
    }

    /**
     * {@return the public key, or {@code null} if there is no public key associated
     * with the profile}
     */
    @Nullable
    public PlayerPublicKey getPublicKey() {
        PlayerKeyPair playerKeyPair = this.keyPairFuture.join();
        return playerKeyPair != null ? playerKeyPair.publicKey() : null;
    }

    /**
     * {@return the private key, or {@code null} if there is no private key associated
     * with the profile}
     */
    @Nullable
    private PrivateKey getPrivateKey() {
        PlayerKeyPair playerKeyPair = this.keyPairFuture.join();
        return playerKeyPair != null ? playerKeyPair.privateKey() : null;
    }
}

