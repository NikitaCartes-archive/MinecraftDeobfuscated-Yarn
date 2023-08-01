package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;

@Environment(EnvType.CLIENT)
public class RealmsServiceException extends Exception {
	public final RealmsError error;

	public RealmsServiceException(RealmsError error) {
		this.error = error;
	}

	public String getMessage() {
		return this.error.getErrorMessage();
	}
}
