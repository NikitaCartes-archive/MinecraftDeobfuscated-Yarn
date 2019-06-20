package net.minecraft;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2983 extends PrintStream {
	protected static final Logger field_13384 = LogManager.getLogger();
	protected final String field_13383;

	public class_2983(String string, OutputStream outputStream) {
		super(outputStream);
		this.field_13383 = string;
	}

	public void println(@Nullable String string) {
		this.method_12870(string);
	}

	public void println(Object object) {
		this.method_12870(String.valueOf(object));
	}

	protected void method_12870(@Nullable String string) {
		field_13384.info("[{}]: {}", this.field_13383, string);
	}
}
