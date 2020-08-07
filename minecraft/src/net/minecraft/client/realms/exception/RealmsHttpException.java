package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsHttpException extends RuntimeException {
	public RealmsHttpException(String s, Exception e) {
		super(s, e);
	}
}
