package net.minecraft.network.encryption;

/**
 * A checked exception that wraps other exceptions, thrown
 * when a cryptographic operation fails.
 */
public class NetworkEncryptionException extends Exception {
	public NetworkEncryptionException(Throwable throwable) {
		super(throwable);
	}
}
