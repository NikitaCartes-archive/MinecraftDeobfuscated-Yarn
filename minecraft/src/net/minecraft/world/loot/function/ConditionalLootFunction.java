package net.minecraft.world.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction implements LootFunction {
	protected final LootCondition[] field_1047;
	private final Predicate<LootContext> predicate;

	protected ConditionalLootFunction(LootCondition[] lootConditions) {
		this.field_1047 = lootConditions;
		this.predicate = LootConditions.joinAnd(lootConditions);
	}

	public final ItemStack method_521(ItemStack itemStack, LootContext lootContext) {
		return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
	}

	protected abstract ItemStack process(ItemStack itemStack, LootContext lootContext);

	@Override
	public void method_292(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		LootFunction.super.method_292(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.field_1047.length; i++) {
			this.field_1047[i].method_292(lootTableReporter.makeChild(".conditions[" + i + "]"), function, set, lootContextType);
		}
	}

	protected static ConditionalLootFunction.Builder<?> builder(Function<LootCondition[], LootFunction> function) {
		return new ConditionalLootFunction.Joiner(function);
	}

	public abstract static class Builder<T extends ConditionalLootFunction.Builder<T>> implements LootFunction.Builder, ConditionConsumerBuilder<T> {
		private final List<LootCondition> conditionList = Lists.<LootCondition>newArrayList();

		public T method_524(LootCondition.Builder builder) {
			this.conditionList.add(builder.build());
			return this.getThisBuilder();
		}

		public final T method_525() {
			return this.getThisBuilder();
		}

		protected abstract T getThisBuilder();

		protected LootCondition[] method_526() {
			return (LootCondition[])this.conditionList.toArray(new LootCondition[0]);
		}
	}

	public abstract static class Factory<T extends ConditionalLootFunction> extends LootFunction.Factory<T> {
		public Factory(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void method_529(JsonObject jsonObject, T conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
			if (!ArrayUtils.isEmpty((Object[])conditionalLootFunction.field_1047)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(conditionalLootFunction.field_1047));
			}
		}

		public final T method_528(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			return this.method_530(jsonObject, jsonDeserializationContext, lootConditions);
		}

		public abstract T method_530(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions);
	}

	static final class Joiner extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
		private final Function<LootCondition[], LootFunction> joiner;

		public Joiner(Function<LootCondition[], LootFunction> function) {
			this.joiner = function;
		}

		protected ConditionalLootFunction.Joiner method_527() {
			return this;
		}

		@Override
		public LootFunction build() {
			return (LootFunction)this.joiner.apply(this.method_526());
		}
	}
}
