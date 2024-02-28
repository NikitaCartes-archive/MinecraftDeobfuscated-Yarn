package net.minecraft.data.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.component.Component;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ItemListProvider implements DataProvider {
	private final DataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

	public ItemListProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		this.output = output;
		this.registryLookupFuture = registryLookupFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("items.json");
		return this.registryLookupFuture.thenCompose(registryLookup -> {
			JsonObject jsonObject = new JsonObject();
			RegistryOps<JsonElement> registryOps = registryLookup.getOps(JsonOps.INSTANCE);
			registryLookup.getWrapperOrThrow(RegistryKeys.ITEM).streamEntries().forEach(entry -> {
				JsonObject jsonObject2 = new JsonObject();
				JsonArray jsonArray = new JsonArray();
				((Item)entry.value()).getComponents().forEach(component -> jsonArray.add(toJson(component, registryOps)));
				jsonObject2.add("components", jsonArray);
				jsonObject.add(entry.getIdAsString(), jsonObject2);
			});
			return DataProvider.writeToPath(writer, jsonObject, path);
		});
	}

	private static <T> JsonElement toJson(Component<T> component, DynamicOps<JsonElement> ops) {
		Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(component.type());
		JsonElement jsonElement = Util.getResult(
			component.encode(ops), error -> new IllegalStateException("Failed to serialize component " + identifier + ": " + error)
		);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", identifier.toString());
		jsonObject.add("value", jsonElement);
		return jsonObject;
	}

	@Override
	public final String getName() {
		return "Item List";
	}
}
