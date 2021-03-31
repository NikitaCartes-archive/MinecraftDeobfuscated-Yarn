package net.minecraft.loot.condition;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class ValueCheckLootCondition implements LootCondition {
	private final LootNumberProvider value;
	private final BoundedIntUnaryOperator range;

	private ValueCheckLootCondition(LootNumberProvider value, BoundedIntUnaryOperator range) {
		this.value = value;
		this.range = range;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.VALUE_CHECK;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(this.value.getRequiredParameters(), this.range.getRequiredParameters());
	}

	public boolean test(LootContext lootContext) {
		return this.range.test(lootContext, this.value.nextInt(lootContext));
	}

	public static LootCondition.Builder builder(LootNumberProvider value, BoundedIntUnaryOperator range) {
		return () -> new ValueCheckLootCondition(value, range);
	}

	public static class Serializer implements JsonSerializer<ValueCheckLootCondition> {
		public void toJson(JsonObject jsonObject, ValueCheckLootCondition valueCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("value", jsonSerializationContext.serialize(valueCheckLootCondition.value));
			jsonObject.add("range", jsonSerializationContext.serialize(valueCheckLootCondition.range));
		}

		public ValueCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, LootNumberProvider.class);
			BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "range", jsonDeserializationContext, BoundedIntUnaryOperator.class);
			return new ValueCheckLootCondition(lootNumberProvider, boundedIntUnaryOperator);
		}
	}
}
