package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator.PathResolver pathResolver;
	private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators = ImmutableList.of(
		Pair.of(FishingLootTableGenerator::new, LootContextTypes.FISHING),
		Pair.of(ChestLootTableGenerator::new, LootContextTypes.CHEST),
		Pair.of(EntityLootTableGenerator::new, LootContextTypes.ENTITY),
		Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK),
		Pair.of(BarterLootTableGenerator::new, LootContextTypes.BARTER),
		Pair.of(GiftLootTableGenerator::new, LootContextTypes.GIFT)
	);

	public LootTableProvider(DataGenerator root) {
		this.pathResolver = root.createPathResolver(DataGenerator.OutputType.DATA_PACK, "loot_tables");
	}

	@Override
	public void run(DataWriter cache) {
		Map<Identifier, LootTable> map = Maps.<Identifier, LootTable>newHashMap();
		this.lootTypeGenerators.forEach(generator -> ((Consumer)((Supplier)generator.getFirst()).get()).accept((BiConsumer)(id, builder) -> {
				if (map.put(id, builder.type((LootContextType)generator.getSecond()).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + id);
				}
			}));
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);

		for (Identifier identifier : Sets.difference(LootTables.getAll(), map.keySet())) {
			lootTableReporter.report("Missing built-in table: " + identifier);
		}

		map.forEach((id, table) -> LootManager.validate(lootTableReporter, id, table));
		Multimap<String, String> multimap = lootTableReporter.getMessages();
		if (!multimap.isEmpty()) {
			multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((id, table) -> {
				Path path = this.pathResolver.resolveJson(id);

				try {
					DataProvider.writeToPath(cache, LootManager.toJson(table), path);
				} catch (IOException var6x) {
					LOGGER.error("Couldn't save loot table {}", path, var6x);
				}
			});
		}
	}

	@Override
	public String getName() {
		return "LootTables";
	}
}
