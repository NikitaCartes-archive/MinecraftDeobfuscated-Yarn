package net.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2924 implements class_2596<class_2921> {
	private static final Gson field_13282 = new GsonBuilder()
		.registerTypeAdapter(class_2926.class_2930.class, new class_2926.class_2930.class_2931())
		.registerTypeAdapter(class_2926.class_2927.class, new class_2926.class_2927.class_2928())
		.registerTypeAdapter(class_2926.class, new class_2926.class_2929())
		.registerTypeHierarchyAdapter(class_2561.class, new class_2561.class_2562())
		.registerTypeHierarchyAdapter(class_2583.class, new class_2583.class_2584())
		.registerTypeAdapterFactory(new class_3530())
		.create();
	private class_2926 field_13281;

	public class_2924() {
	}

	public class_2924(class_2926 arg) {
		this.field_13281 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13281 = class_3518.method_15284(field_13282, arg.method_10800(32767), class_2926.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(field_13282.toJson(this.field_13281));
	}

	public void method_12671(class_2921 arg) {
		arg.method_12667(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2926 method_12672() {
		return this.field_13281;
	}
}
