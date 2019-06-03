/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.loot.BinomialLootTableRange;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)UniformLootTableRange.class), new UniformLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BinomialLootTableRange.class), new BinomialLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)ConstantLootTableRange.class), new ConstantLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BoundedIntUnaryOperator.class), new BoundedIntUnaryOperator.Serializer()).registerTypeAdapter((Type)((Object)LootPool.class), new LootPool.Serializer()).registerTypeAdapter((Type)((Object)LootSupplier.class), new LootSupplier.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    private Map<Identifier, LootSupplier> suppliers = ImmutableMap.of();

    public LootManager() {
        super(GSON, "loot_tables");
    }

    public LootSupplier getSupplier(Identifier identifier) {
        return this.suppliers.getOrDefault(identifier, LootSupplier.EMPTY);
    }

    protected void method_20712(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, LootSupplier> builder = ImmutableMap.builder();
        map.forEach((identifier, jsonObject) -> {
            try {
                LootSupplier lootSupplier = GSON.fromJson((JsonElement)jsonObject, LootSupplier.class);
                builder.put((Identifier)identifier, lootSupplier);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", identifier, (Object)exception);
            }
        });
        builder.put(LootTables.EMPTY, LootSupplier.EMPTY);
        ImmutableMap<Identifier, LootSupplier> immutableMap = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter();
        immutableMap.forEach((identifier, lootSupplier) -> LootManager.check(lootTableReporter, identifier, lootSupplier, immutableMap::get));
        lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.suppliers = immutableMap;
    }

    public static void check(LootTableReporter lootTableReporter, Identifier identifier, LootSupplier lootSupplier, Function<Identifier, LootSupplier> function) {
        ImmutableSet<Identifier> set = ImmutableSet.of(identifier);
        lootSupplier.check(lootTableReporter.makeChild("{" + identifier.toString() + "}"), function, set, lootSupplier.getType());
    }

    public static JsonElement toJson(LootSupplier lootSupplier) {
        return GSON.toJsonTree(lootSupplier);
    }

    public Set<Identifier> getSupplierNames() {
        return this.suppliers.keySet();
    }
}

