/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
    private Map<Identifier, LootTable> tables = ImmutableMap.of();
    private final LootConditionManager conditionManager;

    public LootManager(LootConditionManager conditionManager) {
        super(GSON, "loot_tables");
        this.conditionManager = conditionManager;
    }

    public LootTable getTable(Identifier id) {
        return this.tables.getOrDefault(id, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, LootTable> builder = ImmutableMap.builder();
        JsonElement jsonElement = map.remove(LootTables.EMPTY);
        if (jsonElement != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((id, json) -> {
            try {
                LootTable lootTable = GSON.fromJson((JsonElement)json, LootTable.class);
                builder.put((Identifier)id, lootTable);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", id, (Object)exception);
            }
        });
        builder.put(LootTables.EMPTY, LootTable.EMPTY);
        ImmutableMap<Identifier, LootTable> immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, immutableMap::get);
        immutableMap.forEach((id, lootTable) -> LootManager.validate(lootTableReporter, id, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in {}: {}", key, value));
        this.tables = immutableMap;
    }

    public static void validate(LootTableReporter reporter, Identifier id, LootTable table) {
        table.validate(reporter.withContextType(table.getType()).withTable("{" + id + "}", id));
    }

    public static JsonElement toJson(LootTable table) {
        return GSON.toJsonTree(table);
    }

    public Set<Identifier> getTableIds() {
        return this.tables.keySet();
    }
}

