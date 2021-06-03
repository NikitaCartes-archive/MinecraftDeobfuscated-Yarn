package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class InvertedLootCondition implements LootCondition {
	final LootCondition term;

	InvertedLootCondition(LootCondition term) {
		this.term = term;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.INVERTED;
	}

	public final boolean test(LootContext lootContext) {
		return !this.term.test(lootContext);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.term.getRequiredParameters();
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootCondition.super.validate(reporter);
		this.term.validate(reporter);
	}

	public static LootCondition.Builder builder(LootCondition.Builder term) {
		InvertedLootCondition invertedLootCondition = new InvertedLootCondition(term.build());
		return () -> invertedLootCondition;
	}

	public static class Serializer implements JsonSerializer<InvertedLootCondition> {
		public void toJson(JsonObject jsonObject, InvertedLootCondition invertedLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(invertedLootCondition.term));
		}

		public InvertedLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition lootCondition = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, LootCondition.class);
			return new InvertedLootCondition(lootCondition);
		}
	}
}
