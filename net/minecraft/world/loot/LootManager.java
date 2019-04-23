/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)UniformLootTableRange.class), new UniformLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BinomialLootTableRange.class), new BinomialLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)ConstantLootTableRange.class), new ConstantLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BoundedIntUnaryOperator.class), new BoundedIntUnaryOperator.Serializer()).registerTypeAdapter((Type)((Object)LootPool.class), new LootPool.Serializer()).registerTypeAdapter((Type)((Object)LootSupplier.class), new LootSupplier.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    private final Map<Identifier, LootSupplier> suppliers = Maps.newHashMap();
    private final Set<Identifier> supplierNames = Collections.unmodifiableSet(this.suppliers.keySet());
    public static final int lootTablesLength = "loot_tables/".length();
    public static final int jsonLength = ".json".length();

    public LootSupplier getSupplier(Identifier identifier) {
        return this.suppliers.getOrDefault(identifier, LootSupplier.EMPTY);
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.suppliers.clear();
        for (Identifier identifier2 : resourceManager.findResources("loot_tables", string -> string.endsWith(".json"))) {
            String string3 = identifier2.getPath();
            Identifier identifier22 = new Identifier(identifier2.getNamespace(), string3.substring(lootTablesLength, string3.length() - jsonLength));
            try {
                Resource resource = resourceManager.getResource(identifier2);
                Throwable throwable = null;
                try {
                    LootSupplier lootSupplier2 = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), LootSupplier.class);
                    if (lootSupplier2 == null) continue;
                    this.suppliers.put(identifier22, lootSupplier2);
                } catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                } finally {
                    if (resource == null) continue;
                    if (throwable != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    resource.close();
                }
            } catch (Throwable throwable) {
                LOGGER.error("Couldn't read loot table {} from {}", (Object)identifier22, (Object)identifier2, (Object)throwable);
            }
        }
        this.suppliers.put(LootTables.EMPTY, LootSupplier.EMPTY);
        LootTableReporter lootTableReporter = new LootTableReporter();
        this.suppliers.forEach((identifier, lootSupplier) -> LootManager.check(lootTableReporter, identifier, lootSupplier, this.suppliers::get));
        lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
    }

    public static void check(LootTableReporter lootTableReporter, Identifier identifier, LootSupplier lootSupplier, Function<Identifier, LootSupplier> function) {
        ImmutableSet<Identifier> set = ImmutableSet.of(identifier);
        lootSupplier.check(lootTableReporter.makeChild("{" + identifier.toString() + "}"), function, set, lootSupplier.getType());
    }

    public static JsonElement toJson(LootSupplier lootSupplier) {
        return gson.toJsonTree(lootSupplier);
    }

    public Set<Identifier> getSupplierNames() {
        return this.supplierNames;
    }
}

