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

public class LootGsons {
	public static GsonBuilder getConditionGsonBuilder() {
		return new GsonBuilder()
			.registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
			.registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer())
			.registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer())
			.registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
	}

	public static GsonBuilder getFunctionGsonBuilder() {
		return getConditionGsonBuilder()
			.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer())
			.registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer())
			.registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer());
	}

	public static GsonBuilder getTableGsonBuilder() {
		return getFunctionGsonBuilder()
			.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
			.registerTypeAdapter(LootTable.class, new LootTable.Serializer());
	}
}
