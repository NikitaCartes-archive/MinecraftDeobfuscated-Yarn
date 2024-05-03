package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Util;

public abstract class ConditionalLootFunction implements LootFunction {
	protected final List<LootCondition> conditions;
	private final Predicate<LootContext> predicate;

	protected ConditionalLootFunction(List<LootCondition> conditions) {
		this.conditions = conditions;
		this.predicate = Util.allOf(conditions);
	}

	@Override
	public abstract LootFunctionType<? extends ConditionalLootFunction> getType();

	protected static <T extends ConditionalLootFunction> P1<Mu<T>, List<LootCondition>> addConditionsField(Instance<T> instance) {
		return instance.group(LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(function -> function.conditions));
	}

	public final ItemStack apply(ItemStack itemStack, LootContext lootContext) {
		return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
	}

	protected abstract ItemStack process(ItemStack stack, LootContext context);

	@Override
	public void validate(LootTableReporter reporter) {
		LootFunction.super.validate(reporter);

		for (int i = 0; i < this.conditions.size(); i++) {
			((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(".conditions[" + i + "]"));
		}
	}

	protected static ConditionalLootFunction.Builder<?> builder(Function<List<LootCondition>, LootFunction> joiner) {
		return new ConditionalLootFunction.Joiner(joiner);
	}

	public abstract static class Builder<T extends ConditionalLootFunction.Builder<T>> implements LootFunction.Builder, LootConditionConsumingBuilder<T> {
		private final ImmutableList.Builder<LootCondition> conditionList = ImmutableList.builder();

		public T conditionally(LootCondition.Builder builder) {
			this.conditionList.add(builder.build());
			return this.getThisBuilder();
		}

		public final T getThisConditionConsumingBuilder() {
			return this.getThisBuilder();
		}

		protected abstract T getThisBuilder();

		protected List<LootCondition> getConditions() {
			return this.conditionList.build();
		}
	}

	static final class Joiner extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
		private final Function<List<LootCondition>, LootFunction> joiner;

		public Joiner(Function<List<LootCondition>, LootFunction> joiner) {
			this.joiner = joiner;
		}

		protected ConditionalLootFunction.Joiner getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return (LootFunction)this.joiner.apply(this.getConditions());
		}
	}
}
