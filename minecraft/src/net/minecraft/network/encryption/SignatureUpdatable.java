package net.minecraft.network.encryption;

import java.security.SignatureException;

@FunctionalInterface
public interface SignatureUpdatable {
	void update(SignatureUpdatable.SignatureUpdater updater) throws SignatureException;

	@FunctionalInterface
	public interface SignatureUpdater {
		void update(byte[] data) throws SignatureException;
	}
}
