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
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;

public abstract class LootEntry implements EntryCombiner {
	protected final LootCondition[] conditions;
	private final Predicate<LootContext> conditionPredicate;

	protected LootEntry(LootCondition[] conditions) {
		this.conditions = conditions;
		this.conditionPredicate = LootConditions.joinAnd(conditions);
	}

	public void check(LootTableReporter reporter) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(reporter.makeChild(".condition[" + i + "]"));
		}
	}

	protected final boolean test(LootContext context) {
		return this.conditionPredicate.test(context);
	}

	public abstract static class Builder<T extends LootEntry.Builder<T>> implements LootConditionConsumingBuilder<T> {
		private final List<LootCondition> conditions = Lists.<LootCondition>newArrayList();

		protected abstract T getThisBuilder();

		public T withCondition(LootCondition.Builder builder) {
			this.conditions.add(builder.build());
			return this.getThisBuilder();
		}

		public final T getThis() {
			return this.getThisBuilder();
		}

		protected LootCondition[] getConditions() {
			return (LootCondition[])this.conditions.toArray(new LootCondition[0]);
		}

		public AlternativeEntry.Builder withChild(LootEntry.Builder<?> builder) {
			return new AlternativeEntry.Builder(this, builder);
		}

		public abstract LootEntry build();
	}

	public abstract static class Serializer<T extends LootEntry> {
		private final Identifier id;
		private final Class<T> type;

		protected Serializer(Identifier id, Class<T> type) {
			this.id = id;
			this.type = type;
		}

		public Identifier getIdentifier() {
			return this.id;
		}

		public Class<T> getType() {
			return this.type;
		}

		public abstract void toJson(JsonObject json, T entry, JsonSerializationContext context);

		public abstract T fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions);
	}
}
