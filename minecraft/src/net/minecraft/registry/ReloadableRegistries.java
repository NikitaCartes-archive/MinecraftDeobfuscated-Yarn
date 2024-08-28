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
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
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

	public static CompletableFuture<ReloadableRegistries.ReloadResult> reload(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
		List<Registry.PendingTagLoad<?>> pendingTagLoads,
		ResourceManager resourceManager,
		Executor prepareExecutor
	) {
		List<RegistryWrapper.Impl<?>> list = TagGroupLoader.collectRegistries(
			dynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE), pendingTagLoads
		);
		RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(list.stream());
		RegistryOps<JsonElement> registryOps = wrapperLookup.getOps(JsonOps.INSTANCE);
		List<CompletableFuture<MutableRegistry<?>>> list2 = LootDataType.stream().map(type -> prepare(type, registryOps, resourceManager, prepareExecutor)).toList();
		CompletableFuture<List<MutableRegistry<?>>> completableFuture = Util.combineSafe(list2);
		return completableFuture.thenApplyAsync(registries -> toResult(dynamicRegistries, wrapperLookup, registries), prepareExecutor);
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
				TagGroupLoader.loadInitial(resourceManager, mutableRegistry);
				return mutableRegistry;
			},
			prepareExecutor
		);
	}

	private static ReloadableRegistries.ReloadResult toResult(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, RegistryWrapper.WrapperLookup nonReloadables, List<MutableRegistry<?>> registries
	) {
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = with(dynamicRegistries, registries);
		RegistryWrapper.WrapperLookup wrapperLookup = concat(nonReloadables, combinedDynamicRegistries.get(ServerDynamicRegistryType.RELOADABLE));
		validate(wrapperLookup);
		return new ReloadableRegistries.ReloadResult(combinedDynamicRegistries, wrapperLookup);
	}

	private static RegistryWrapper.WrapperLookup concat(RegistryWrapper.WrapperLookup first, RegistryWrapper.WrapperLookup second) {
		return RegistryWrapper.WrapperLookup.of(Stream.concat(first.stream(), second.stream()));
	}

	private static void validate(RegistryWrapper.WrapperLookup registries) {
		ErrorReporter.Impl impl = new ErrorReporter.Impl();
		LootTableReporter lootTableReporter = new LootTableReporter(impl, LootContextTypes.GENERIC, registries);
		LootDataType.stream().forEach(type -> validateLootData(lootTableReporter, type, registries));
		impl.getErrors().forEach((id, error) -> LOGGER.warn("Found loot table element validation problem in {}: {}", id, error));
	}

	private static CombinedDynamicRegistries<ServerDynamicRegistryType> with(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, List<MutableRegistry<?>> registries
	) {
		return dynamicRegistries.with(ServerDynamicRegistryType.RELOADABLE, new DynamicRegistryManager.ImmutableImpl(registries).toImmutable());
	}

	private static <T> void validateLootData(LootTableReporter reporter, LootDataType<T> lootDataType, RegistryWrapper.WrapperLookup registries) {
		RegistryWrapper<T> registryWrapper = registries.getOrThrow(lootDataType.registryKey());
		registryWrapper.streamEntries().forEach(entry -> lootDataType.validate(reporter, entry.registryKey(), (T)entry.value()));
	}

	public static class Lookup {
		private final RegistryWrapper.WrapperLookup registries;

		public Lookup(RegistryWrapper.WrapperLookup registries) {
			this.registries = registries;
		}

		public RegistryEntryLookup.RegistryLookup createRegistryLookup() {
			return this.registries;
		}

		public Collection<Identifier> getIds(RegistryKey<? extends Registry<?>> registryRef) {
			return this.registries.getOrThrow(registryRef).streamKeys().map(RegistryKey::getValue).toList();
		}

		public LootTable getLootTable(RegistryKey<LootTable> key) {
			return (LootTable)this.registries
				.getOptional(RegistryKeys.LOOT_TABLE)
				.flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key))
				.map(RegistryEntry::value)
				.orElse(LootTable.EMPTY);
		}
	}

	public static record ReloadResult(CombinedDynamicRegistries<ServerDynamicRegistryType> layers, RegistryWrapper.WrapperLookup lookupWithUpdatedTags) {
	}
}
