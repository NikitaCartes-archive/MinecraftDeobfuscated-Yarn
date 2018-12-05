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
	protected final LootCondition[] conditions;
	private final Predicate<LootContext> predicate;

	protected ConditionalLootFunction(LootCondition[] lootConditions) {
		this.conditions = lootConditions;
		this.predicate = LootConditions.joinAnd(lootConditions);
	}

	public final ItemStack apply(ItemStack itemStack, LootContext lootContext) {
		return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
	}

	protected abstract ItemStack process(ItemStack itemStack, LootContext lootContext);

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		LootFunction.super.check(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].check(lootTableReporter.makeChild(".conditions[" + i + "]"), function, set, lootContextType);
		}
	}

	protected static ConditionalLootFunction.Builder<?> create(Function<LootCondition[], LootFunction> function) {
		return new ConditionalLootFunction.Joiner(function);
	}

	public abstract static class Builder<T extends ConditionalLootFunction.Builder<T>> implements LootFunction.Builder, ConditionConsumerBuilder<T> {
		private final List<LootCondition> conditionList = Lists.<LootCondition>newArrayList();

		public T withCondition(LootCondition.Builder builder) {
			this.conditionList.add(builder.build());
			return this.build();
		}

		public final T method_525() {
			return this.build();
		}

		protected abstract T build();

		protected LootCondition[] getConditions() {
			return (LootCondition[])this.conditionList.toArray(new LootCondition[0]);
		}
	}

	public abstract static class Factory<T extends ConditionalLootFunction> extends LootFunction.Factory<T> {
		public Factory(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void toJson(JsonObject jsonObject, T conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
			if (!ArrayUtils.isEmpty((Object[])conditionalLootFunction.conditions)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(conditionalLootFunction.conditions));
			}
		}

		public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
		}

		public abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions);
	}

	static final class Joiner extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
		private final Function<LootCondition[], LootFunction> joiner;

		public Joiner(Function<LootCondition[], LootFunction> function) {
			this.joiner = function;
		}

		protected ConditionalLootFunction.Joiner create() {
			return this;
		}

		@Override
		public LootFunction build() {
			return (LootFunction)this.joiner.apply(this.getConditions());
		}
	}
}
