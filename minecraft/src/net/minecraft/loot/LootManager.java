package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
	private Map<Identifier, LootTable> tables = ImmutableMap.of();
	private final LootConditionManager conditionManager;

	public LootManager(LootConditionManager conditionManager) {
		super(GSON, "loot_tables");
		this.conditionManager = conditionManager;
	}

	public LootTable getTable(Identifier id) {
		return (LootTable)this.tables.getOrDefault(id, LootTable.EMPTY);
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
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, immutableMap::get);
		immutableMap.forEach((identifier, lootTable) -> check(lootTableReporter, identifier, lootTable));
		lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in " + key + ": " + value));
		this.tables = immutableMap;
	}

	public static void check(LootTableReporter reporter, Identifier id, LootTable table) {
		table.check(reporter.withContextType(table.getType()).withTable("{" + id + "}", id));
	}

	public static JsonElement toJson(LootTable table) {
		return GSON.toJsonTree(table);
	}

	public Set<Identifier> getTableIds() {
		return this.tables.keySet();
	}
}
