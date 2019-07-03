package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4353 implements UncaughtExceptionHandler {
	private final Logger field_19603;

	public class_4353(Logger logger) {
		this.field_19603 = logger;
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		this.field_19603.error("Caught previously unhandled exception :");
		this.field_19603.error(throwable);
	}
}
