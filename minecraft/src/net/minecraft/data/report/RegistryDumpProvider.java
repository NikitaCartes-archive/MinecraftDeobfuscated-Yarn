package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class RegistryDumpProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;

	public RegistryDumpProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataWriter cache) throws IOException {
		JsonObject jsonObject = new JsonObject();
		Registry.REGISTRIES.streamEntries().forEach(entry -> jsonObject.add(entry.registryKey().getValue().toString(), toJson((Registry)entry.value())));
		Path path = this.generator.getOutput().resolve("reports/registries.json");
		DataProvider.writeToPath(GSON, cache, jsonObject, path);
	}

	private static <T> JsonElement toJson(Registry<T> registry) {
		JsonObject jsonObject = new JsonObject();
		if (registry instanceof DefaultedRegistry) {
			Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
			jsonObject.addProperty("default", identifier.toString());
		}

		int i = Registry.REGISTRIES.getRawId(registry);
		jsonObject.addProperty("protocol_id", i);
		JsonObject jsonObject2 = new JsonObject();
		registry.streamEntries().forEach(entry -> {
			T object = (T)entry.value();
			int ixx = registry.getRawId(object);
			JsonObject jsonObject2xx = new JsonObject();
			jsonObject2xx.addProperty("protocol_id", ixx);
			jsonObject2.add(entry.registryKey().getValue().toString(), jsonObject2xx);
		});
		jsonObject.add("entries", jsonObject2);
		return jsonObject;
	}

	@Override
	public String getName() {
		return "Registry Dump";
	}
}
