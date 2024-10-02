package net.minecraft.data.server.loottable;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.RandomSequence;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataOutput.PathResolver pathResolver;
	private final Set<RegistryKey<LootTable>> lootTableIds;
	private final List<LootTableProvider.LootTypeGenerator> lootTypeGenerators;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	public LootTableProvider(
		DataOutput output,
		Set<RegistryKey<LootTable>> lootTableIds,
		List<LootTableProvider.LootTypeGenerator> lootTypeGenerators,
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
	) {
		this.pathResolver = output.getResolver(RegistryKeys.LOOT_TABLE);
		this.lootTypeGenerators = lootTypeGenerators;
		this.lootTableIds = lootTableIds;
		this.registriesFuture = registriesFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture.thenCompose(registries -> this.run(writer, registries));
	}

	private CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup registries) {
		MutableRegistry<LootTable> mutableRegistry = new SimpleRegistry<>(RegistryKeys.LOOT_TABLE, Lifecycle.experimental());
		Map<RandomSeed.XoroshiroSeed, Identifier> map = new Object2ObjectOpenHashMap<>();
		this.lootTypeGenerators.forEach(lootTypeGenerator -> ((LootTableGenerator)lootTypeGenerator.provider().apply(registries)).accept((lootTable, builder) -> {
				Identifier identifier = getId(lootTable);
				Identifier identifier2 = (Identifier)map.put(RandomSequence.createSeed(identifier), identifier);
				if (identifier2 != null) {
					Util.logErrorOrPause("Loot table random sequence seed collision on " + identifier2 + " and " + lootTable.getValue());
				}

				builder.randomSequenceId(identifier);
				LootTable lootTable2 = builder.type(lootTypeGenerator.paramSet).build();
				mutableRegistry.add(lootTable, lootTable2, RegistryEntryInfo.DEFAULT);
			}));
		mutableRegistry.freeze();
		ErrorReporter.Impl impl = new ErrorReporter.Impl();
		RegistryEntryLookup.RegistryLookup registryLookup = new DynamicRegistryManager.ImmutableImpl(List.of(mutableRegistry)).toImmutable();
		LootTableReporter lootTableReporter = new LootTableReporter(impl, LootContextTypes.GENERIC, registryLookup);

		for (RegistryKey<LootTable> registryKey : Sets.difference(this.lootTableIds, mutableRegistry.getKeys())) {
			impl.report("Missing built-in table: " + registryKey.getValue());
		}

		mutableRegistry.streamEntries()
			.forEach(
				entry -> ((LootTable)entry.value())
						.validate(
							lootTableReporter.withContextType(((LootTable)entry.value()).getType()).makeChild("{" + entry.registryKey().getValue() + "}", entry.registryKey())
						)
			);
		Multimap<String, String> multimap = impl.getErrors();
		if (!multimap.isEmpty()) {
			multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			return CompletableFuture.allOf((CompletableFuture[])mutableRegistry.getEntrySet().stream().map(entry -> {
				RegistryKey<LootTable> registryKeyx = (RegistryKey<LootTable>)entry.getKey();
				LootTable lootTable = (LootTable)entry.getValue();
				Path path = this.pathResolver.resolveJson(registryKeyx.getValue());
				return DataProvider.writeCodecToPath(writer, registries, LootTable.CODEC, lootTable, path);
			}).toArray(CompletableFuture[]::new));
		}
	}

	private static Identifier getId(RegistryKey<LootTable> lootTableKey) {
		return lootTableKey.getValue();
	}

	@Override
	public final String getName() {
		return "Loot Tables";
	}

	public static record LootTypeGenerator(Function<RegistryWrapper.WrapperLookup, LootTableGenerator> provider, LootContextType paramSet) {
	}
}
