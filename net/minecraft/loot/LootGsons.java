/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public class LootGsons {
    public static GsonBuilder getConditionGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter((Type)((Object)UniformLootTableRange.class), new UniformLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)BinomialLootTableRange.class), new BinomialLootTableRange.Serializer()).registerTypeAdapter((Type)((Object)ConstantLootTableRange.class), new ConstantLootTableRange.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
    }

    public static GsonBuilder getFunctionGsonBuilder() {
        return LootGsons.getConditionGsonBuilder().registerTypeAdapter((Type)((Object)BoundedIntUnaryOperator.class), new BoundedIntUnaryOperator.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory());
    }

    public static GsonBuilder getTableGsonBuilder() {
        return LootGsons.getFunctionGsonBuilder().registerTypeAdapter((Type)((Object)LootPool.class), new LootPool.Serializer()).registerTypeAdapter((Type)((Object)LootTable.class), new LootTable.Serializer());
    }
}

