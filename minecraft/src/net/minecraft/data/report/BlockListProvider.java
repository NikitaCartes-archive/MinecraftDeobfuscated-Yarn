package net.minecraft.data.report;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class BlockListProvider implements DataProvider {
	private final DataGenerator root;

	public BlockListProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (Block block : Registry.BLOCK) {
			Identifier identifier = Registry.BLOCK.getId(block);
			JsonObject jsonObject2 = new JsonObject();
			StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
			if (!stateFactory.getProperties().isEmpty()) {
				JsonObject jsonObject3 = new JsonObject();

				for (Property<?> property : stateFactory.getProperties()) {
					JsonArray jsonArray = new JsonArray();

					for (Comparable<?> comparable : property.getValues()) {
						jsonArray.add(SystemUtil.method_650(property, comparable));
					}

					jsonObject3.add(property.getName(), jsonArray);
				}

				jsonObject2.add("properties", jsonObject3);
			}

			JsonArray jsonArray2 = new JsonArray();

			for (BlockState blockState : stateFactory.getStates()) {
				JsonObject jsonObject4 = new JsonObject();
				JsonObject jsonObject5 = new JsonObject();

				for (Property<?> property2 : stateFactory.getProperties()) {
					jsonObject5.addProperty(property2.getName(), SystemUtil.method_650(property2, blockState.get(property2)));
				}

				if (jsonObject5.size() > 0) {
					jsonObject4.add("properties", jsonObject5);
				}

				jsonObject4.addProperty("id", Block.getRawIdFromState(blockState));
				if (blockState == block.getDefaultState()) {
					jsonObject4.addProperty("default", true);
				}

				jsonArray2.add(jsonObject4);
			}

			jsonObject2.add("states", jsonArray2);
			jsonObject.add(identifier.toString(), jsonObject2);
		}

		Path path = this.root.getOutput().resolve("reports/blocks.json");
		Files.createDirectories(path.getParent());
		BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		Throwable var26 = null;

		try {
			String string = new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)jsonObject);
			bufferedWriter.write(string);
		} catch (Throwable var22) {
			var26 = var22;
			throw var22;
		} finally {
			if (bufferedWriter != null) {
				if (var26 != null) {
					try {
						bufferedWriter.close();
					} catch (Throwable var21) {
						var26.addSuppressed(var21);
					}
				} else {
					bufferedWriter.close();
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Block List";
	}
}
