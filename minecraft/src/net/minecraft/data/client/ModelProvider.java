package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

public class ModelProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator.PathResolver blockstatesPathResolver;
	private final DataGenerator.PathResolver modelsPathResolver;

	public ModelProvider(DataGenerator generator) {
		this.blockstatesPathResolver = generator.createPathResolver(DataGenerator.OutputType.RESOURCE_PACK, "blockstates");
		this.modelsPathResolver = generator.createPathResolver(DataGenerator.OutputType.RESOURCE_PACK, "models");
	}

	@Override
	public void run(DataWriter writer) {
		Map<Block, BlockStateSupplier> map = Maps.<Block, BlockStateSupplier>newHashMap();
		Consumer<BlockStateSupplier> consumer = blockStateSupplier -> {
			Block block = blockStateSupplier.getBlock();
			BlockStateSupplier blockStateSupplier2 = (BlockStateSupplier)map.put(block, blockStateSupplier);
			if (blockStateSupplier2 != null) {
				throw new IllegalStateException("Duplicate blockstate definition for " + block);
			}
		};
		Map<Identifier, Supplier<JsonElement>> map2 = Maps.<Identifier, Supplier<JsonElement>>newHashMap();
		Set<Item> set = Sets.<Item>newHashSet();
		BiConsumer<Identifier, Supplier<JsonElement>> biConsumer = (id, jsonSupplier) -> {
			Supplier<JsonElement> supplier = (Supplier<JsonElement>)map2.put(id, jsonSupplier);
			if (supplier != null) {
				throw new IllegalStateException("Duplicate model definition for " + id);
			}
		};
		Consumer<Item> consumer2 = set::add;
		new BlockStateModelGenerator(consumer, biConsumer, consumer2).register();
		new ItemModelGenerator(biConsumer).register();
		List<Block> list = Registry.BLOCK.stream().filter(block -> !map.containsKey(block)).toList();
		if (!list.isEmpty()) {
			throw new IllegalStateException("Missing blockstate definitions for: " + list);
		} else {
			Registry.BLOCK.forEach(block -> {
				Item item = (Item)Item.BLOCK_ITEMS.get(block);
				if (item != null) {
					if (set.contains(item)) {
						return;
					}

					Identifier identifier = ModelIds.getItemModelId(item);
					if (!map2.containsKey(identifier)) {
						map2.put(identifier, new SimpleModelSupplier(ModelIds.getBlockModelId(block)));
					}
				}
			});
			this.writeJsons(writer, map, block -> this.blockstatesPathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue()));
			this.writeJsons(writer, map2, this.modelsPathResolver::resolveJson);
		}
	}

	private <T> void writeJsons(DataWriter cache, Map<T, ? extends Supplier<JsonElement>> models, Function<T, Path> pathGetter) {
		models.forEach((id, jsonSupplier) -> {
			Path path = (Path)pathGetter.apply(id);

			try {
				DataProvider.writeToPath(cache, (JsonElement)jsonSupplier.get(), path);
			} catch (Exception var6) {
				LOGGER.error("Couldn't save {}", path, var6);
			}
		});
	}

	@Override
	public String getName() {
		return "Block State Definitions";
	}
}
