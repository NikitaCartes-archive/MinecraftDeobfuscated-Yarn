package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;

@Environment(EnvType.CLIENT)
public class RetryCallException extends RealmsServiceException {
	public static final int DEFAULT_DELAY_SECONDS = 5;
	public final int delaySeconds;

	public RetryCallException(int delaySeconds, int httpResultCode) {
		super(RealmsError.SimpleHttpError.retryable(httpResultCode));
		if (delaySeconds >= 0 && delaySeconds <= 120) {
			this.delaySeconds = delaySeconds;
		} else {
			this.delaySeconds = 5;
		}
	}
}
