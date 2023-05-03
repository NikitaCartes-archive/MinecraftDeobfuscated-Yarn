package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class LootManager implements ResourceReloader, LootDataLookup {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final LootDataKey<LootTable> EMPTY_LOOT_TABLE = new LootDataKey<>(LootDataType.LOOT_TABLES, LootTables.EMPTY);
	private Map<LootDataKey<?>, ?> keyToValue = Map.of();
	private Multimap<LootDataType<?>, Identifier> typeToIds = ImmutableMultimap.of();

	@Override
	public final CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		Map<LootDataType<?>, Map<Identifier, ?>> map = new HashMap();
		CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])LootDataType.stream()
			.map(type -> load(type, manager, prepareExecutor, map))
			.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(completableFutures).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(v -> this.validate(map), applyExecutor);
	}

	private static <T> CompletableFuture<?> load(
		LootDataType<T> type, ResourceManager resourceManager, Executor executor, Map<LootDataType<?>, Map<Identifier, ?>> results
	) {
		Map<Identifier, T> map = new HashMap();
		results.put(type, map);
		return CompletableFuture.runAsync(() -> {
			Map<Identifier, JsonElement> map2 = new HashMap();
			JsonDataLoader.load(resourceManager, type.getId(), type.getGson(), map2);
			map2.forEach((id, json) -> type.parse(id, json).ifPresent(value -> map.put(id, value)));
		}, executor);
	}

	private void validate(Map<LootDataType<?>, Map<Identifier, ?>> lootData) {
		Object object = ((Map)lootData.get(LootDataType.LOOT_TABLES)).remove(LootTables.EMPTY);
		if (object != null) {
			LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", LootTables.EMPTY);
		}

		Builder<LootDataKey<?>, Object> builder = ImmutableMap.builder();
		com.google.common.collect.ImmutableMultimap.Builder<LootDataType<?>, Identifier> builder2 = ImmutableMultimap.builder();
		lootData.forEach((type, idToValue) -> idToValue.forEach((id, value) -> {
				builder.put(new LootDataKey(type, id), value);
				builder2.put(type, id);
			}));
		builder.put(EMPTY_LOOT_TABLE, LootTable.EMPTY);
		final Map<LootDataKey<?>, ?> map = builder.build();
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, new LootDataLookup() {
			@Nullable
			@Override
			public <T> T getElement(LootDataKey<T> lootDataKey) {
				return (T)map.get(lootDataKey);
			}
		});
		map.forEach((key, value) -> validate(lootTableReporter, key, value));
		lootTableReporter.getMessages().forEach((name, message) -> LOGGER.warn("Found loot table element validation problem in {}: {}", name, message));
		this.keyToValue = map;
		this.typeToIds = builder2.build();
	}

	private static <T> void validate(LootTableReporter reporter, LootDataKey<T> key, Object value) {
		key.type().validate(reporter, key, (T)value);
	}

	@Nullable
	@Override
	public <T> T getElement(LootDataKey<T> lootDataKey) {
		return (T)this.keyToValue.get(lootDataKey);
	}

	public Collection<Identifier> getIds(LootDataType<?> type) {
		return this.typeToIds.get(type);
	}

	public static LootCondition and(LootCondition[] predicates) {
		return new LootManager.AndCondition(predicates);
	}

	public static LootFunction and(LootFunction[] modifiers) {
		return new LootManager.AndFunction(modifiers);
	}

	static class AndCondition implements LootCondition {
		private final LootCondition[] terms;
		private final Predicate<LootContext> predicate;

		AndCondition(LootCondition[] terms) {
			this.terms = terms;
			this.predicate = LootConditionTypes.matchingAll(terms);
		}

		public final boolean test(LootContext lootContext) {
			return this.predicate.test(lootContext);
		}

		@Override
		public void validate(LootTableReporter reporter) {
			LootCondition.super.validate(reporter);

			for (int i = 0; i < this.terms.length; i++) {
				this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
			}
		}

		@Override
		public LootConditionType getType() {
			throw new UnsupportedOperationException();
		}
	}

	static class AndFunction implements LootFunction {
		protected final LootFunction[] functions;
		private final BiFunction<ItemStack, LootContext, ItemStack> applier;

		public AndFunction(LootFunction[] functions) {
			this.functions = functions;
			this.applier = LootFunctionTypes.join(functions);
		}

		public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
			return (ItemStack)this.applier.apply(itemStack, lootContext);
		}

		@Override
		public void validate(LootTableReporter reporter) {
			LootFunction.super.validate(reporter);

			for (int i = 0; i < this.functions.length; i++) {
				this.functions[i].validate(reporter.makeChild(".function[" + i + "]"));
			}
		}

		@Override
		public LootFunctionType getType() {
			throw new UnsupportedOperationException();
		}
	}
}
