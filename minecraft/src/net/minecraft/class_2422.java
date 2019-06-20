package net.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;

public class class_2422 implements class_2405 {
	private static final Gson field_17168 = new GsonBuilder().setPrettyPrinting().create();
	private final class_2403 field_11307;

	public class_2422(class_2403 arg) {
		this.field_11307 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (class_2248 lv : class_2378.field_11146) {
			class_2960 lv2 = class_2378.field_11146.method_10221(lv);
			JsonObject jsonObject2 = new JsonObject();
			class_2689<class_2248, class_2680> lv3 = lv.method_9595();
			if (!lv3.method_11659().isEmpty()) {
				JsonObject jsonObject3 = new JsonObject();

				for (class_2769<?> lv4 : lv3.method_11659()) {
					JsonArray jsonArray = new JsonArray();

					for (Comparable<?> comparable : lv4.method_11898()) {
						jsonArray.add(class_156.method_650(lv4, comparable));
					}

					jsonObject3.add(lv4.method_11899(), jsonArray);
				}

				jsonObject2.add("properties", jsonObject3);
			}

			JsonArray jsonArray2 = new JsonArray();

			for (class_2680 lv5 : lv3.method_11662()) {
				JsonObject jsonObject4 = new JsonObject();
				JsonObject jsonObject5 = new JsonObject();

				for (class_2769<?> lv6 : lv3.method_11659()) {
					jsonObject5.addProperty(lv6.method_11899(), class_156.method_650(lv6, lv5.method_11654(lv6)));
				}

				if (jsonObject5.size() > 0) {
					jsonObject4.add("properties", jsonObject5);
				}

				jsonObject4.addProperty("id", class_2248.method_9507(lv5));
				if (lv5 == lv.method_9564()) {
					jsonObject4.addProperty("default", true);
				}

				jsonArray2.add(jsonObject4);
			}

			jsonObject2.add("states", jsonArray2);
			jsonObject.add(lv2.toString(), jsonObject2);
		}

		Path path = this.field_11307.method_10313().resolve("reports/blocks.json");
		class_2405.method_10320(field_17168, arg, jsonObject, path);
	}

	@Override
	public String method_10321() {
		return "Block List";
	}
}
