package net.minecraft.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction implements LootFunction {
	protected final LootCondition[] conditions;
	private final Predicate<LootContext> predicate;

	protected ConditionalLootFunction(LootCondition[] conditions) {
		this.conditions = conditions;
		this.predicate = LootConditions.joinAnd(conditions);
	}

	public final ItemStack method_521(ItemStack itemStack, LootContext lootContext) {
		return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
	}

	protected abstract ItemStack process(ItemStack stack, LootContext context);

	@Override
	public void check(LootTableReporter reporter) {
		LootFunction.super.check(reporter);

		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(reporter.makeChild(".conditions[" + i + "]"));
		}
	}

	protected static ConditionalLootFunction.Builder<?> builder(Function<LootCondition[], LootFunction> joiner) {
		return new ConditionalLootFunction.Joiner(joiner);
	}

	public abstract static class Builder<T extends ConditionalLootFunction.Builder<T>> implements LootFunction.Builder, LootConditionConsumingBuilder<T> {
		private final List<LootCondition> conditionList = Lists.<LootCondition>newArrayList();

		public T method_524(LootCondition.Builder builder) {
			this.conditionList.add(builder.build());
			return this.getThisBuilder();
		}

		public final T method_525() {
			return this.getThisBuilder();
		}

		protected abstract T getThisBuilder();

		protected LootCondition[] getConditions() {
			return (LootCondition[])this.conditionList.toArray(new LootCondition[0]);
		}
	}

	public abstract static class Factory<T extends ConditionalLootFunction> extends LootFunction.Factory<T> {
		public Factory(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void method_529(JsonObject jsonObject, T conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
			if (!ArrayUtils.isEmpty((Object[])conditionalLootFunction.conditions)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(conditionalLootFunction.conditions));
			}
		}

		public final T method_528(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
		}

		public abstract T fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions);
	}

	static final class Joiner extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
		private final Function<LootCondition[], LootFunction> joiner;

		public Joiner(Function<LootCondition[], LootFunction> joiner) {
			this.joiner = joiner;
		}

		protected ConditionalLootFunction.Joiner method_527() {
			return this;
		}

		@Override
		public LootFunction build() {
			return (LootFunction)this.joiner.apply(this.getConditions());
		}
	}
}