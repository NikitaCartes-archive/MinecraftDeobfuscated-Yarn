package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataLookup;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.RandomSequence;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataOutput.PathResolver pathResolver;
	private final Set<Identifier> lootTableIds;
	private final List<LootTableProvider.LootTypeGenerator> lootTypeGenerators;

	public LootTableProvider(DataOutput output, Set<Identifier> lootTableIds, List<LootTableProvider.LootTypeGenerator> lootTypeGenerators) {
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
		this.lootTypeGenerators = lootTypeGenerators;
		this.lootTableIds = lootTableIds;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		final Map<Identifier, LootTable> map = Maps.<Identifier, LootTable>newHashMap();
		Map<RandomSeed.XoroshiroSeed, Identifier> map2 = new Object2ObjectOpenHashMap<>();
		this.lootTypeGenerators.forEach(generator -> ((LootTableGenerator)generator.provider().get()).accept((id, builder) -> {
				Identifier identifierx = (Identifier)map2.put(RandomSequence.createSeed(id), id);
				if (identifierx != null) {
					Util.error("Loot table random sequence seed collision on " + identifierx + " and " + id);
				}

				builder.randomSequenceId(id);
				if (map.put(id, builder.type(generator.paramSet).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + id);
				}
			}));
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, new LootDataLookup() {
			@Nullable
			@Override
			public <T> T getElement(LootDataKey<T> lootDataKey) {
				return (T)(lootDataKey.type() == LootDataType.LOOT_TABLES ? map.get(lootDataKey.id()) : null);
			}
		});

		for (Identifier identifier : Sets.difference(this.lootTableIds, map.keySet())) {
			lootTableReporter.report("Missing built-in table: " + identifier);
		}

		map.forEach(
			(id, table) -> table.validate(lootTableReporter.withContextType(table.getType()).makeChild("{" + id + "}", new LootDataKey<>(LootDataType.LOOT_TABLES, id)))
		);
		Multimap<String, String> multimap = lootTableReporter.getMessages();
		if (!multimap.isEmpty()) {
			multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			return CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map(entry -> {
				Identifier identifierx = (Identifier)entry.getKey();
				LootTable lootTable = (LootTable)entry.getValue();
				Path path = this.pathResolver.resolveJson(identifierx);
				return DataProvider.writeCodecToPath(writer, LootTable.CODEC, lootTable, path);
			}).toArray(CompletableFuture[]::new));
		}
	}

	@Override
	public final String getName() {
		return "Loot Tables";
	}

	public static record LootTypeGenerator(Supplier<LootTableGenerator> provider, LootContextType paramSet) {
	}
}
