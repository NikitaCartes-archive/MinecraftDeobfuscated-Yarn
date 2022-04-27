/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Optional;
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
 * 
 *  * @param keyString the PEM-formatted X.509 encoded key
 * @param signature the signature certifying that this key is provided from Mojang's server
 */
public record PlayerPublicKey(Instant expiresAt, String keyString, String signature) {
    public static final Codec<PlayerPublicKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.INSTANT.fieldOf("expires_at")).forGetter(PlayerPublicKey::expiresAt), ((MapCodec)Codec.STRING.fieldOf("key")).forGetter(PlayerPublicKey::keyString), ((MapCodec)Codec.STRING.fieldOf("signature")).forGetter(PlayerPublicKey::signature)).apply((Applicative<PlayerPublicKey, ?>)instance, PlayerPublicKey::new));
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
        Property property = Iterables.getFirst(gameProfile.getProperties().get(PUBLIC_KEY), null);
        if (property == null) {
            return Optional.empty();
        }
        String string = property.getValue();
        String string2 = property.getSignature();
        if (Strings.isNullOrEmpty(string) || Strings.isNullOrEmpty(string2)) {
            return Optional.empty();
        }
        return PlayerPublicKey.parse(string).map(pair -> new PlayerPublicKey((Pair<Instant, String>)pair, string2));
    }

    /**
     * Writes the public key to the {@code gameProfile} properties under the
     * {@value PUBLIC_KEY} key.
     */
    public GameProfile write(GameProfile gameProfile) {
        gameProfile.getProperties().put(PUBLIC_KEY, this.toProperty());
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
    public PublicKeyData verifyAndDecode(MinecraftSessionService sessionService) throws InsecurePublicKeyException, NetworkEncryptionException {
        if (Strings.isNullOrEmpty(this.keyString)) {
            throw new InsecurePublicKeyException.MissingException();
        }
        String string = sessionService.getSecurePropertyValue(this.toProperty());
        if (!(this.expiresAt.toEpochMilli() + this.keyString).equals(string)) {
            throw new InsecurePublicKeyException.InvalidException("Invalid profile public key signature");
        }
        Pair<Instant, String> pair = PlayerPublicKey.parse(string).orElseThrow(() -> new InsecurePublicKeyException.InvalidException("Invalid profile public key"));
        if (pair.getFirst().isBefore(Instant.now())) {
            throw new InsecurePublicKeyException.InvalidException("Expired profile public key");
        }
        PublicKey publicKey = NetworkEncryptionUtils.decodeRsaPublicKeyPem(pair.getSecond());
        return new PublicKeyData(publicKey, this);
    }

    /**
     * {@return the expiry time and the key content parsed from {@code contents}}
     */
    private static Optional<Pair<Instant, String>> parse(String contents) {
        long l;
        int i = contents.indexOf("-----BEGIN RSA PUBLIC KEY-----");
        try {
            l = Long.parseLong(contents.substring(0, i));
        } catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
        return Optional.of(Pair.of(Instant.ofEpochMilli(l), contents.substring(i)));
    }

    /**
     * {@return the public key as an authlib {@code Property}}
     */
    public Property toProperty() {
        return new Property(PUBLIC_KEY, this.expiresAt.toEpochMilli() + this.keyString, this.signature);
    }

    /**
     * {@return whether the key is expired and can no longer be used}
     */
    public boolean isExpired() {
        return this.expiresAt.isBefore(Instant.now());
    }

    public record PublicKeyData(PublicKey key, PlayerPublicKey data) {
        public Signature createSignatureInstance() throws NetworkEncryptionException {
            try {
                Signature signature = Signature.getInstance("SHA1withRSA");
                signature.initVerify(this.key);
                return signature;
            } catch (GeneralSecurityException generalSecurityException) {
                throw new NetworkEncryptionException(generalSecurityException);
            }
        }
    }
}

