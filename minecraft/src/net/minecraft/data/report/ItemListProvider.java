package net.minecraft.data.report;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemListProvider implements DataProvider {
	private final DataGenerator root;

	public ItemListProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (Item item : Registry.ITEM) {
			Identifier identifier = Registry.ITEM.getId(item);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("protocol_id", Item.getRawIdByItem(item));
			jsonObject.add(identifier.toString(), jsonObject2);
		}

		Path path = this.root.getOutput().resolve("reports/items.json");
		Files.createDirectories(path.getParent());
		BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		Throwable var18 = null;

		try {
			String string = new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)jsonObject);
			bufferedWriter.write(string);
		} catch (Throwable var14) {
			var18 = var14;
			throw var14;
		} finally {
			if (bufferedWriter != null) {
				if (var18 != null) {
					try {
						bufferedWriter.close();
					} catch (Throwable var13) {
						var18.addSuppressed(var13);
					}
				} else {
					bufferedWriter.close();
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Item List";
	}
}
