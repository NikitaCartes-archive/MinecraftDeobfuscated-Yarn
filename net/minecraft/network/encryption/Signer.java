/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.logging.LogUtils;
import java.security.PrivateKey;
import java.security.Signature;
import net.minecraft.network.encryption.SignatureUpdatable;
import org.slf4j.Logger;

public interface Signer {
    public static final Logger LOGGER = LogUtils.getLogger();

    public byte[] sign(SignatureUpdatable var1);

    default public byte[] sign(byte[] data) {
        return this.sign(updater -> updater.update(data));
    }

    public static Signer create(PrivateKey privateKey, String algorithm) {
        return updatable -> {
            try {
                Signature signature = Signature.getInstance(algorithm);
                signature.initSign(privateKey);
                updatable.update(signature::update);
                return signature.sign();
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to sign message", exception);
            }
        };
    }
}

