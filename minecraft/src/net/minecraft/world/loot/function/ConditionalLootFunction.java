package net.minecraft.world.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction implements LootFunction {
	protected final class_4570[] conditions;
	private final Predicate<LootContext> predicate;

	protected ConditionalLootFunction(class_4570[] args) {
		this.conditions = args;
		this.predicate = LootConditions.joinAnd(args);
	}

	public final ItemStack method_521(ItemStack itemStack, LootContext lootContext) {
		return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
	}

	protected abstract ItemStack process(ItemStack itemStack, LootContext lootContext);

	@Override
	public void check(LootTableReporter lootTableReporter) {
		LootFunction.super.check(lootTableReporter);

		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(lootTableReporter.makeChild(".conditions[" + i + "]"));
		}
	}

	protected static ConditionalLootFunction.Builder<?> builder(Function<class_4570[], LootFunction> function) {
		return new ConditionalLootFunction.Joiner(function);
	}

	public abstract static class Builder<T extends ConditionalLootFunction.Builder<T>> implements LootFunction.Builder, ConditionConsumerBuilder<T> {
		private final List<class_4570> conditionList = Lists.<class_4570>newArrayList();

		public T method_524(class_4570.Builder builder) {
			this.conditionList.add(builder.build());
			return this.getThisBuilder();
		}

		public final T method_525() {
			return this.getThisBuilder();
		}

		protected abstract T getThisBuilder();

		protected class_4570[] getConditions() {
			return (class_4570[])this.conditionList.toArray(new class_4570[0]);
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
			class_4570[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new class_4570[0], jsonDeserializationContext, class_4570[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
		}

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args);
	}

	static final class Joiner extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
		private final Function<class_4570[], LootFunction> joiner;

		public Joiner(Function<class_4570[], LootFunction> function) {
			this.joiner = function;
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
