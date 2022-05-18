package net.minecraft.network.encryption;

import com.mojang.authlib.yggdrasil.ServicesKeyInfo;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.slf4j.Logger;

public interface SignatureVerifier {
	SignatureVerifier NOOP = (updatable, signatureData) -> true;
	Logger LOGGER = LogUtils.getLogger();

	boolean validate(SignatureUpdatable updatable, byte[] signatureData);

	default boolean validate(byte[] signedData, byte[] signatureData) {
		return this.validate(updater -> updater.update(signedData), signatureData);
	}

	private static boolean verify(SignatureUpdatable updatable, byte[] signatureData, Signature signature) throws SignatureException {
		updatable.update(signature::update);
		return signature.verify(signatureData);
	}

	static SignatureVerifier create(PublicKey publicKey, String algorithm) {
		return (updatable, signatureData) -> {
			try {
				Signature signature = Signature.getInstance(algorithm);
				signature.initVerify(publicKey);
				return verify(updatable, signatureData, signature);
			} catch (Exception var5) {
				LOGGER.error("Failed to verify signature", (Throwable)var5);
				return false;
			}
		};
	}

	static SignatureVerifier create(ServicesKeyInfo servicesKeyInfo) {
		return (signatureUpdatable, bs) -> {
			Signature signature = servicesKeyInfo.signature();

			try {
				return verify(signatureUpdatable, bs, signature);
			} catch (SignatureException var5) {
				LOGGER.error("Failed to verify Services signature", (Throwable)var5);
				return false;
			}
		};
	}
}
