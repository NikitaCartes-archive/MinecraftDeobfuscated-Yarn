package net.minecraft.loot;

import com.google.gson.GsonBuilder;
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
		return new GsonBuilder()
			.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer())
			.registerTypeHierarchyAdapter(LootNumberProvider.class, LootNumberProviderTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootScoreProvider.class, LootScoreProviderTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
	}

	public static GsonBuilder getFunctionGsonBuilder() {
		return getConditionGsonBuilder()
			.registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootNbtProvider.class, LootNbtProviderTypes.createGsonSerializer());
	}

	public static GsonBuilder getTableGsonBuilder() {
		return getFunctionGsonBuilder()
			.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
			.registerTypeAdapter(LootTable.class, new LootTable.Serializer());
	}
}
