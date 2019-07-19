package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
		.registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer())
		.registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer())
		.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer())
		.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
		.registerTypeAdapter(LootTable.class, new LootTable.Serializer())
		.registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer())
		.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory())
		.registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory())
		.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
		.create();
	private Map<Identifier, LootTable> suppliers = ImmutableMap.of();

	public LootManager() {
		super(GSON, "loot_tables");
	}

	public LootTable getSupplier(Identifier id) {
		return (LootTable)this.suppliers.getOrDefault(id, LootTable.EMPTY);
	}

	protected void apply(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, LootTable> builder = ImmutableMap.builder();
		JsonObject jsonObject = (JsonObject)map.remove(LootTables.EMPTY);
		if (jsonObject != null) {
			LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", LootTables.EMPTY);
		}

		map.forEach((identifier, jsonObjectx) -> {
			try {
				LootTable lootTable = GSON.fromJson(jsonObjectx, LootTable.class);
				builder.put(identifier, lootTable);
			} catch (Exception var4x) {
				LOGGER.error("Couldn't parse loot table {}", identifier, var4x);
			}
		});
		builder.put(LootTables.EMPTY, LootTable.EMPTY);
		ImmutableMap<Identifier, LootTable> immutableMap = builder.build();
		LootTableReporter lootTableReporter = new LootTableReporter();
		immutableMap.forEach((id, supplier) -> check(lootTableReporter, id, supplier, immutableMap::get));
		lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in " + key + ": " + value));
		this.suppliers = immutableMap;
	}

	public static void check(LootTableReporter reporter, Identifier id, LootTable supplier, Function<Identifier, LootTable> supplierGetter) {
		Set<Identifier> set = ImmutableSet.of(id);
		supplier.check(reporter.makeChild("{" + id.toString() + "}"), supplierGetter, set, supplier.getType());
	}

	public static JsonElement toJson(LootTable supplier) {
		return GSON.toJsonTree(supplier);
	}

	public Set<Identifier> getSupplierNames() {
		return this.suppliers.keySet();
	}
}
