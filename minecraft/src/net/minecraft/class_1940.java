package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1940 {
	private final long field_9263;
	private final class_1934 field_9257;
	private final boolean field_9262;
	private final boolean field_9261;
	private final class_1942 field_9260;
	private boolean field_9259;
	private boolean field_9258;
	private JsonElement field_9264 = new JsonObject();

	public class_1940(long l, class_1934 arg, boolean bl, boolean bl2, class_1942 arg2) {
		this.field_9263 = l;
		this.field_9257 = arg;
		this.field_9262 = bl;
		this.field_9261 = bl2;
		this.field_9260 = arg2;
	}

	public class_1940(class_31 arg) {
		this(arg.method_184(), arg.method_210(), arg.method_220(), arg.method_152(), arg.method_153());
	}

	public class_1940 method_8575() {
		this.field_9258 = true;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public class_1940 method_8578() {
		this.field_9259 = true;
		return this;
	}

	public class_1940 method_8579(JsonElement jsonElement) {
		this.field_9264 = jsonElement;
		return this;
	}

	public boolean method_8581() {
		return this.field_9258;
	}

	public long method_8577() {
		return this.field_9263;
	}

	public class_1934 method_8574() {
		return this.field_9257;
	}

	public boolean method_8582() {
		return this.field_9261;
	}

	public boolean method_8583() {
		return this.field_9262;
	}

	public class_1942 method_8576() {
		return this.field_9260;
	}

	public boolean method_8573() {
		return this.field_9259;
	}

	public JsonElement method_8584() {
		return this.field_9264;
	}
}
