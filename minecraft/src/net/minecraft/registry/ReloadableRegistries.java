package net.minecraft.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class ReloadableRegistries {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

	public static CompletableFuture<CombinedDynamicRegistries<ServerDynamicRegistryType>> reload(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, ResourceManager resourceManager, Executor prepareExecutor
	) {
		DynamicRegistryManager.Immutable immutable = dynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE);
		RegistryOps<JsonElement> registryOps = new ReloadableRegistries.ReloadableWrapperLookup(immutable).getOps(JsonOps.INSTANCE);
		List<CompletableFuture<MutableRegistry<?>>> list = LootDataType.stream().map(type -> prepare(type, registryOps, resourceManager, prepareExecutor)).toList();
		CompletableFuture<List<MutableRegistry<?>>> completableFuture = Util.combineSafe(list);
		return completableFuture.thenApplyAsync(registries -> apply(dynamicRegistries, registries), prepareExecutor);
	}

	private static <T> CompletableFuture<MutableRegistry<?>> prepare(
		LootDataType<T> type, RegistryOps<JsonElement> ops, ResourceManager resourceManager, Executor prepareExecutor
	) {
		return CompletableFuture.supplyAsync(
			() -> {
				MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(type.registryKey(), Lifecycle.experimental());
				Map<Identifier, JsonElement> map = new HashMap();
				String string = RegistryKeys.getPath(type.registryKey());
				JsonDataLoader.load(resourceManager, string, GSON, map);
				map.forEach(
					(id, json) -> type.parse(id, ops, json)
							.ifPresent(value -> mutableRegistry.add(RegistryKey.of(type.registryKey(), id), (T)value, DEFAULT_REGISTRY_ENTRY_INFO))
				);
				return mutableRegistry;
			},
			prepareExecutor
		);
	}

	private static CombinedDynamicRegistries<ServerDynamicRegistryType> apply(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, List<MutableRegistry<?>> registries
	) {
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = with(dynamicRegistries, registries);
		ErrorReporter.Impl impl = new ErrorReporter.Impl();
		DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getCombinedRegistryManager();
		LootTableReporter lootTableReporter = new LootTableReporter(impl, LootContextTypes.GENERIC, immutable.createRegistryLookup());
		LootDataType.stream().forEach(lootDataType -> validateLootData(lootTableReporter, lootDataType, immutable));
		impl.getErrors().forEach((path, message) -> LOGGER.warn("Found loot table element validation problem in {}: {}", path, message));
		return combinedDynamicRegistries;
	}

	private static CombinedDynamicRegistries<ServerDynamicRegistryType> with(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, List<MutableRegistry<?>> registries
	) {
		DynamicRegistryManager dynamicRegistryManager = new DynamicRegistryManager.ImmutableImpl(registries);
		((MutableRegistry)dynamicRegistryManager.<LootTable>get(RegistryKeys.LOOT_TABLE)).add(LootTables.EMPTY, LootTable.EMPTY, DEFAULT_REGISTRY_ENTRY_INFO);
		return dynamicRegistries.with(ServerDynamicRegistryType.RELOADABLE, dynamicRegistryManager.toImmutable());
	}

	private static <T> void validateLootData(LootTableReporter reporter, LootDataType<T> lootDataType, DynamicRegistryManager registryManager) {
		Registry<T> registry = registryManager.get(lootDataType.registryKey());
		registry.streamEntries().forEach(entry -> lootDataType.validate(reporter, entry.registryKey(), (T)entry.value()));
	}

	public static class Lookup {
		private final DynamicRegistryManager.Immutable registryManager;

		public Lookup(DynamicRegistryManager.Immutable registryManager) {
			this.registryManager = registryManager;
		}

		public DynamicRegistryManager.Immutable getRegistryManager() {
			return this.registryManager;
		}

		public RegistryEntryLookup.RegistryLookup createRegistryLookup() {
			return this.registryManager.createRegistryLookup();
		}

		public Collection<Identifier> getIds(RegistryKey<? extends Registry<?>> registryRef) {
			return this.registryManager
				.getOptional(registryRef)
				.stream()
				.flatMap(registry -> registry.streamEntries().map(entry -> entry.registryKey().getValue()))
				.toList();
		}

		public LootTable getLootTable(RegistryKey<LootTable> key) {
			return (LootTable)this.registryManager
				.getOptionalWrapper(RegistryKeys.LOOT_TABLE)
				.flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key))
				.map(RegistryEntry::value)
				.orElse(LootTable.EMPTY);
		}
	}

	static class ReloadableWrapperLookup implements RegistryWrapper.WrapperLookup {
		private final DynamicRegistryManager registryManager;

		ReloadableWrapperLookup(DynamicRegistryManager registryManager) {
			this.registryManager = registryManager;
		}

		@Override
		public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
			return this.registryManager.streamAllRegistryKeys();
		}

		@Override
		public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return this.registryManager.getOptional(registryRef).map(Registry::getTagCreatingWrapper);
		}
	}
}
