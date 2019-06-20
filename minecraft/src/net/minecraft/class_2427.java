package net.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;

public class class_2427 implements class_2405 {
	private static final Gson field_17170 = new GsonBuilder().setPrettyPrinting().create();
	private final class_2403 field_11323;

	public class_2427(class_2403 arg) {
		this.field_11323 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		JsonObject jsonObject = new JsonObject();
		class_2378.field_11144.method_10235().forEach(argx -> jsonObject.add(argx.toString(), method_17175(class_2378.field_11144.method_10223(argx))));
		Path path = this.field_11323.method_10313().resolve("reports/registries.json");
		class_2405.method_10320(field_17170, arg, jsonObject, path);
	}

	private static <T> JsonElement method_17175(class_2385<T> arg) {
		JsonObject jsonObject = new JsonObject();
		if (arg instanceof class_2348) {
			class_2960 lv = ((class_2348)arg).method_10137();
			jsonObject.addProperty("default", lv.toString());
		}

		int i = class_2378.field_11144.method_10249(arg);
		jsonObject.addProperty("protocol_id", i);
		JsonObject jsonObject2 = new JsonObject();

		for (class_2960 lv2 : arg.method_10235()) {
			T object = arg.method_10223(lv2);
			int j = arg.method_10249(object);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("protocol_id", j);
			jsonObject2.add(lv2.toString(), jsonObject3);
		}

		jsonObject.add("entries", jsonObject2);
		return jsonObject;
	}

	@Override
	public String method_10321() {
		return "Registry Dump";
	}
}
