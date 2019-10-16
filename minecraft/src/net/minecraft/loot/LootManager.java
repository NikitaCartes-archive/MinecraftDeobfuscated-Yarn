package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
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
	private final LootConditionManager conditionManager;

	public LootManager(LootConditionManager lootConditionManager) {
		super(GSON, "loot_tables");
		this.conditionManager = lootConditionManager;
	}

	public LootTable getSupplier(Identifier identifier) {
		return (LootTable)this.suppliers.getOrDefault(identifier, LootTable.EMPTY);
	}

	protected void method_20712(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
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
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, immutableMap::get);
		immutableMap.forEach((identifier, lootTable) -> check(lootTableReporter, identifier, lootTable));
		lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
		this.suppliers = immutableMap;
	}

	public static void check(LootTableReporter lootTableReporter, Identifier identifier, LootTable lootTable) {
		lootTable.check(lootTableReporter.withContextType(lootTable.getType()).withSupplier("{" + identifier + "}", identifier));
	}

	public static JsonElement toJson(LootTable lootTable) {
		return GSON.toJsonTree(lootTable);
	}

	public Set<Identifier> getSupplierNames() {
		return this.suppliers.keySet();
	}
}
