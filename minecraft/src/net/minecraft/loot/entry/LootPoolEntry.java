package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializable;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntry implements EntryCombiner {
	protected final LootCondition[] conditions;
	private final Predicate<LootContext> conditionPredicate;

	protected LootPoolEntry(LootCondition[] conditions) {
		this.conditions = conditions;
		this.conditionPredicate = LootConditionTypes.joinAnd(conditions);
	}

	public void validate(LootTableReporter reporter) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].validate(reporter.makeChild(".condition[" + i + "]"));
		}
	}

	protected final boolean test(LootContext context) {
		return this.conditionPredicate.test(context);
	}

	public abstract LootPoolEntryType method_29318();

	public abstract static class Builder<T extends LootPoolEntry.Builder<T>> implements LootConditionConsumingBuilder<T> {
		private final List<LootCondition> conditions = Lists.<LootCondition>newArrayList();

		protected abstract T getThisBuilder();

		public T conditionally(LootCondition.Builder builder) {
			this.conditions.add(builder.build());
			return this.getThisBuilder();
		}

		public final T getThis() {
			return this.getThisBuilder();
		}

		protected LootCondition[] getConditions() {
			return (LootCondition[])this.conditions.toArray(new LootCondition[0]);
		}

		public AlternativeEntry.Builder alternatively(LootPoolEntry.Builder<?> builder) {
			return new AlternativeEntry.Builder(this, builder);
		}

		public abstract LootPoolEntry build();
	}

	public abstract static class class_5337<T extends LootPoolEntry> implements JsonSerializable<T> {
		public final void toJson(JsonObject jsonObject, T lootPoolEntry, JsonSerializationContext jsonSerializationContext) {
			if (!ArrayUtils.isEmpty((Object[])lootPoolEntry.conditions)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(lootPoolEntry.conditions));
			}

			this.method_422(jsonObject, lootPoolEntry, jsonSerializationContext);
		}

		public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
		}

		public abstract void method_422(JsonObject jsonObject, T lootPoolEntry, JsonSerializationContext jsonSerializationContext);

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions);
	}
}
