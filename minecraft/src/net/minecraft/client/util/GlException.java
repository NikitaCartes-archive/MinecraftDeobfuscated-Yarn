package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlException extends RuntimeException {
	public GlException(String string) {
		super(string);
	}

	public GlException(String message, Throwable cause) {
		super(message, cause);
	}
}
