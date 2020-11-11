/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;

public class LootGsons {
    public static GsonBuilder getConditionGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter((Type)((Object)BoundedIntUnaryOperator.class), new BoundedIntUnaryOperator.Serializer()).registerTypeHierarchyAdapter(LootNumberProvider.class, LootNumberProviderTypes.method_32455()).registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootScoreProvider.class, LootScoreProviderTypes.method_32478()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
    }

    public static GsonBuilder getFunctionGsonBuilder() {
        return LootGsons.getConditionGsonBuilder().registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootNbtProvider.class, LootNbtProviderTypes.method_32442());
    }

    public static GsonBuilder getTableGsonBuilder() {
        return LootGsons.getFunctionGsonBuilder().registerTypeAdapter((Type)((Object)LootPool.class), new LootPool.Serializer()).registerTypeAdapter((Type)((Object)LootTable.class), new LootTable.Serializer());
    }
}

