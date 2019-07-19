package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class InvertedLootCondition implements LootCondition {
	private final LootCondition term;

	private InvertedLootCondition(LootCondition term) {
		this.term = term;
	}

	public final boolean test(LootContext lootContext) {
		return !this.term.test(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.term.getRequiredParameters();
	}

	@Override
	public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
		LootCondition.super.check(reporter, supplierGetter, parentLootTables, contextType);
		this.term.check(reporter, supplierGetter, parentLootTables, contextType);
	}

	public static LootCondition.Builder builder(LootCondition.Builder term) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(term.build());
		return () -> invertedLootCondition;
	}

	public static class Factory extends LootCondition.Factory<InvertedLootCondition> {
		public Factory() {
			super(new Identifier("inverted"), InvertedLootCondition.class);
		}

		public void toJson(JsonObject jsonObject, InvertedLootCondition invertedLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(invertedLootCondition.term));
		}

		public InvertedLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition lootCondition = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, LootCondition.class);
			return new InvertedLootCondition(lootCondition);
		}
	}
}
