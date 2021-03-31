package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;

public class LootingEnchantLootFunction extends ConditionalLootFunction {
	public static final int field_31854 = 0;
	private final LootNumberProvider countRange;
	private final int limit;

	private LootingEnchantLootFunction(LootCondition[] conditions, LootNumberProvider countRange, int limit) {
		super(conditions);
		this.countRange = countRange;
		this.limit = limit;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.LOOTING_ENCHANT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(ImmutableSet.of(LootContextParameters.KILLER_ENTITY), this.countRange.getRequiredParameters());
	}

	private boolean hasLimit() {
		return this.limit > 0;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
		if (entity instanceof LivingEntity) {
			int i = EnchantmentHelper.getLooting((LivingEntity)entity);
			if (i == 0) {
				return stack;
			}

			float f = (float)i * this.countRange.nextFloat(context);
			stack.increment(Math.round(f));
			if (this.hasLimit() && stack.getCount() > this.limit) {
				stack.setCount(this.limit);
			}
		}

		return stack;
	}

	public static LootingEnchantLootFunction.Builder builder(LootNumberProvider countRange) {
		return new LootingEnchantLootFunction.Builder(countRange);
	}

	public static class Builder extends ConditionalLootFunction.Builder<LootingEnchantLootFunction.Builder> {
		private final LootNumberProvider countRange;
		private int limit = 0;

		public Builder(LootNumberProvider countRange) {
			this.countRange = countRange;
		}

		protected LootingEnchantLootFunction.Builder getThisBuilder() {
			return this;
		}

		public LootingEnchantLootFunction.Builder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		@Override
		public LootFunction build() {
			return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<LootingEnchantLootFunction> {
		public void toJson(JsonObject jsonObject, LootingEnchantLootFunction lootingEnchantLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, lootingEnchantLootFunction, jsonSerializationContext);
			jsonObject.add("count", jsonSerializationContext.serialize(lootingEnchantLootFunction.countRange));
			if (lootingEnchantLootFunction.hasLimit()) {
				jsonObject.add("limit", jsonSerializationContext.serialize(lootingEnchantLootFunction.limit));
			}
		}

		public LootingEnchantLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			int i = JsonHelper.getInt(jsonObject, "limit", 0);
			return new LootingEnchantLootFunction(lootConditions, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, LootNumberProvider.class), i);
		}
	}
}
