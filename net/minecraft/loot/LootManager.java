/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
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

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)UniformLootTableRange.class), new UniformLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BinomialLootTableRange.class), new BinomialLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)ConstantLootTableRange.class), new ConstantLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BoundedIntUnaryOperator.class), new BoundedIntUnaryOperator.Serializer()).registerTypeAdapter((Type)((Object)LootPool.class), new LootPool.Serializer()).registerTypeAdapter((Type)((Object)LootTable.class), new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    private Map<Identifier, LootTable> suppliers = ImmutableMap.of();
    private final LootConditionManager conditionManager;

    public LootManager(LootConditionManager conditionManager) {
        super(GSON, "loot_tables");
        this.conditionManager = conditionManager;
    }

    public LootTable getSupplier(Identifier id) {
        return this.suppliers.getOrDefault(id, LootTable.EMPTY);
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, LootTable> builder = ImmutableMap.builder();
        JsonObject jsonObject2 = map.remove(LootTables.EMPTY);
        if (jsonObject2 != null) {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }
        map.forEach((identifier, jsonObject) -> {
            try {
                LootTable lootTable = GSON.fromJson((JsonElement)jsonObject, LootTable.class);
                builder.put((Identifier)identifier, lootTable);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", identifier, (Object)exception);
            }
        });
        builder.put(LootTables.EMPTY, LootTable.EMPTY);
        ImmutableMap<Identifier, LootTable> immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.conditionManager::get, immutableMap::get);
        immutableMap.forEach((identifier, lootTable) -> LootManager.check(lootTableReporter, identifier, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> LOGGER.warn("Found validation problem in " + key + ": " + value));
        this.suppliers = immutableMap;
    }

    public static void check(LootTableReporter reporter, Identifier id, LootTable table) {
        table.check(reporter.withContextType(table.getType()).withSupplier("{" + id + "}", id));
    }

    public static JsonElement toJson(LootTable supplier) {
        return GSON.toJsonTree(supplier);
    }

    public Set<Identifier> getSupplierNames() {
        return this.suppliers.keySet();
    }
}

