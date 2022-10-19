package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String name;
	private final DataOutput.PathResolver pathResolver;
	private final Set<Identifier> lootTableIds;
	private final List<LootTableProvider.LootTypeGenerator> lootTypeGenerators;

	public LootTableProvider(String name, DataOutput output, Set<Identifier> lootTableIds, List<LootTableProvider.LootTypeGenerator> lootTableGenerators) {
		this.name = name;
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
		this.lootTypeGenerators = lootTableGenerators;
		this.lootTableIds = lootTableIds;
	}

	@Override
	public void run(DataWriter writer) {
		Map<Identifier, LootTable> map = Maps.<Identifier, LootTable>newHashMap();
		this.lootTypeGenerators.forEach(lootTypeGenerator -> ((LootTableGenerator)lootTypeGenerator.provider().get()).accept((id, builder) -> {
				if (map.put(id, builder.type(lootTypeGenerator.paramSet).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + id);
				}
			}));
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);

		for (Identifier identifier : Sets.difference(this.lootTableIds, map.keySet())) {
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
					DataProvider.writeToPath(writer, LootManager.toJson(table), path);
				} catch (IOException var6x) {
					LOGGER.error("Couldn't save loot table {}", path, var6x);
				}
			});
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static record LootTypeGenerator(Supplier<LootTableGenerator> provider, LootContextType paramSet) {
	}
}
