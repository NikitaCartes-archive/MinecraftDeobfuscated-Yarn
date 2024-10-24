package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.component.ComponentMap;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

public class ItemListProvider implements DataProvider {
	private final DataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	public ItemListProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		this.output = output;
		this.registriesFuture = registriesFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("items.json");
		return this.registriesFuture
			.thenCompose(
				registries -> {
					JsonObject jsonObject = new JsonObject();
					RegistryOps<JsonElement> registryOps = registries.getOps(JsonOps.INSTANCE);
					registries.getOrThrow(RegistryKeys.ITEM)
						.streamEntries()
						.forEach(
							entry -> {
								JsonObject jsonObject2 = new JsonObject();
								jsonObject2.add(
									"components",
									ComponentMap.CODEC
										.encodeStart(registryOps, ((Item)entry.value()).getComponents())
										.getOrThrow(components -> new IllegalStateException("Failed to encode components: " + components))
								);
								jsonObject.add(entry.getIdAsString(), jsonObject2);
							}
						);
					return DataProvider.writeToPath(writer, jsonObject, path);
				}
			);
	}

	@Override
	public final String getName() {
		return "Item List";
	}
}
