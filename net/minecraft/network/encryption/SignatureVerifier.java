/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.authlib.yggdrasil.ServicesKeyInfo;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import net.minecraft.network.encryption.SignatureUpdatable;
import org.slf4j.Logger;

public interface SignatureVerifier {
    public static final SignatureVerifier NOOP = (updatable, signatureData) -> true;
    public static final Logger LOGGER = LogUtils.getLogger();

    public boolean validate(SignatureUpdatable var1, byte[] var2);

    default public boolean validate(byte[] signedData, byte[] signatureData) {
        return this.validate(updater -> updater.update(signedData), signatureData);
    }

    private static boolean verify(SignatureUpdatable updatable, byte[] signatureData, Signature signature) throws SignatureException {
        updatable.update(signature::update);
        return signature.verify(signatureData);
    }

    public static SignatureVerifier create(PublicKey publicKey, String algorithm) {
        return (updatable, signatureData) -> {
            try {
                Signature signature = Signature.getInstance(algorithm);
                signature.initVerify(publicKey);
                return SignatureVerifier.verify(updatable, signatureData, signature);
            } catch (Exception exception) {
                LOGGER.error("Failed to verify signature", exception);
                return false;
            }
        };
    }

    public static SignatureVerifier create(ServicesKeyInfo servicesKeyInfo) {
        return (updatable, signatureData) -> {
            Signature signature = servicesKeyInfo.signature();
            try {
                return SignatureVerifier.verify(updatable, signatureData, signature);
            } catch (SignatureException signatureException) {
                LOGGER.error("Failed to verify Services signature", signatureException);
                return false;
            }
        };
    }
}

