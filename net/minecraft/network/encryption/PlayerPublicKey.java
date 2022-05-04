/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.google.common.base.Strings;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
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
public record PlayerPublicKey(PublicKeyData data, PublicKey key) {
    public static final Codec<PlayerPublicKey> CODEC = PublicKeyData.CODEC.comapFlatMap(publicKeyData -> {
        try {
            return DataResult.success(PlayerPublicKey.fromKeyData(publicKeyData));
        } catch (NetworkEncryptionException networkEncryptionException) {
            return DataResult.error("Malformed public key");
        }
    }, PlayerPublicKey::data);
    private static final String PUBLIC_KEY = "publicKey";

    public static PlayerPublicKey fromKeyData(PublicKeyData publicKeyData) throws NetworkEncryptionException {
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
    public static PlayerPublicKey verifyAndDecode(MinecraftSessionService sessionService, PublicKeyData publicKeyData) throws InsecurePublicKeyException, NetworkEncryptionException {
        if (Strings.isNullOrEmpty(publicKeyData.key())) {
            throw new InsecurePublicKeyException.MissingException();
        }
        if (publicKeyData.isExpired()) {
            throw new InsecurePublicKeyException.InvalidException("Expired profile public key");
        }
        String string = sessionService.getSecurePropertyValue(publicKeyData.toProperty());
        if (!publicKeyData.toSerializedString().equals(string)) {
            throw new InsecurePublicKeyException.InvalidException("Invalid profile public key signature");
        }
        return PlayerPublicKey.fromKeyData(publicKeyData);
    }

    public Signature createSignatureInstance() throws NetworkEncryptionException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(this.key);
            return signature;
        } catch (GeneralSecurityException generalSecurityException) {
            throw new NetworkEncryptionException(generalSecurityException);
        }
    }

    public record PublicKeyData(Instant expiresAt, String key, String signature) {
        public static final Codec<PublicKeyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.INSTANT.fieldOf("expires_at")).forGetter(PublicKeyData::expiresAt), ((MapCodec)Codec.STRING.fieldOf("key")).forGetter(PublicKeyData::key), ((MapCodec)Codec.STRING.fieldOf("signature")).forGetter(PublicKeyData::signature)).apply((Applicative<PublicKeyData, ?>)instance, PublicKeyData::new));

        public Property toProperty() {
            return new Property(PlayerPublicKey.PUBLIC_KEY, this.toSerializedString(), this.signature);
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

