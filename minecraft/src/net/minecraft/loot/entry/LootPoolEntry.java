package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Util;

public abstract class LootPoolEntry implements EntryCombiner {
	protected final List<LootCondition> conditions;
	private final Predicate<LootContext> conditionPredicate;

	protected LootPoolEntry(List<LootCondition> conditions) {
		this.conditions = conditions;
		this.conditionPredicate = Util.allOf(conditions);
	}

	protected static <T extends LootPoolEntry> P1<Mu<T>, List<LootCondition>> addConditionsField(Instance<T> instance) {
		return instance.group(LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(entry -> entry.conditions));
	}

	public void validate(LootTableReporter reporter) {
		for (int i = 0; i < this.conditions.size(); i++) {
			((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(".condition[" + i + "]"));
		}
	}

	protected final boolean test(LootContext context) {
		return this.conditionPredicate.test(context);
	}

	public abstract LootPoolEntryType getType();

	public abstract static class Builder<T extends LootPoolEntry.Builder<T>> implements LootConditionConsumingBuilder<T> {
		private final ImmutableList.Builder<LootCondition> conditions = ImmutableList.builder();

		protected abstract T getThisBuilder();

		public T conditionally(LootCondition.Builder builder) {
			this.conditions.add(builder.build());
			return this.getThisBuilder();
		}

		public final T getThisConditionConsumingBuilder() {
			return this.getThisBuilder();
		}

		protected List<LootCondition> getConditions() {
			return this.conditions.build();
		}

		public AlternativeEntry.Builder alternatively(LootPoolEntry.Builder<?> builder) {
			return new AlternativeEntry.Builder(this, builder);
		}

		public GroupEntry.Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
			return new GroupEntry.Builder(this, entry);
		}

		public SequenceEntry.Builder groupEntry(LootPoolEntry.Builder<?> entry) {
			return new SequenceEntry.Builder(this, entry);
		}

		public abstract LootPoolEntry build();
	}
}
