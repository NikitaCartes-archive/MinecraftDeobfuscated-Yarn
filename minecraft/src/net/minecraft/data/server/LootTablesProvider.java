package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTablesProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator root;
	private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators = ImmutableList.of(
		Pair.of(FishingLootTableGenerator::new, LootContextTypes.FISHING),
		Pair.of(ChestLootTableGenerator::new, LootContextTypes.CHEST),
		Pair.of(EntityLootTableGenerator::new, LootContextTypes.ENTITY),
		Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK),
		Pair.of(BarterLootTableGenerator::new, LootContextTypes.BARTER),
		Pair.of(GiftLootTableGenerator::new, LootContextTypes.GIFT)
	);

	public LootTablesProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.root.getOutput();
		Map<Identifier, LootTable> map = Maps.<Identifier, LootTable>newHashMap();
		this.lootTypeGenerators.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept((BiConsumer)(identifierx, builder) -> {
				if (map.put(identifierx, builder.withType((LootContextType)pair.getSecond()).create()) != null) {
					throw new IllegalStateException("Duplicate loot table " + identifierx);
				}
			}));
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, identifierx -> null, map::get);

		for (Identifier identifier : Sets.difference(LootTables.getAll(), map.keySet())) {
			lootTableReporter.report("Missing built-in table: " + identifier);
		}

		map.forEach((identifierx, lootTable) -> LootManager.check(lootTableReporter, identifierx, lootTable));
		Multimap<String, String> multimap = lootTableReporter.getMessages();
		if (!multimap.isEmpty()) {
			multimap.forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((identifierx, lootTable) -> {
				Path path2 = getOutput(path, identifierx);

				try {
					DataProvider.writeToPath(GSON, cache, LootManager.toJson(lootTable), path2);
				} catch (IOException var6) {
					LOGGER.error("Couldn't save loot table {}", path2, var6);
				}
			});
		}
	}

	private static Path getOutput(Path rootOutput, Identifier lootTableId) {
		return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "LootTables";
	}
}
