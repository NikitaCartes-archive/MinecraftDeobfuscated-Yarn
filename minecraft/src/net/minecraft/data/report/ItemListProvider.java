package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class ItemListProvider implements DataProvider {
	private static final Gson field_17170 = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator root;

	public ItemListProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void method_10319(DataCache dataCache) throws IOException {
		JsonObject jsonObject = new JsonObject();
		Registry.field_11144.getIds().forEach(identifier -> jsonObject.add(identifier.toString(), method_17175(Registry.field_11144.method_10223(identifier))));
		Path path = this.root.getOutput().resolve("reports/registries.json");
		DataProvider.method_10320(field_17170, dataCache, jsonObject, path);
	}

	private static <T> JsonElement method_17175(MutableRegistry<T> mutableRegistry) {
		JsonObject jsonObject = new JsonObject();
		if (mutableRegistry instanceof DefaultedRegistry) {
			Identifier identifier = ((DefaultedRegistry)mutableRegistry).method_10137();
			jsonObject.addProperty("default", identifier.toString());
		}

		int i = Registry.field_11144.getRawId(mutableRegistry);
		jsonObject.addProperty("protocol_id", i);
		JsonObject jsonObject2 = new JsonObject();

		for (Identifier identifier2 : mutableRegistry.getIds()) {
			T object = mutableRegistry.method_10223(identifier2);
			int j = mutableRegistry.getRawId(object);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("protocol_id", j);
			jsonObject2.add(identifier2.toString(), jsonObject3);
		}

		jsonObject.add("entries", jsonObject2);
		return jsonObject;
	}

	@Override
	public String getName() {
		return "Registry Dump";
	}
}
