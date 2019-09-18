package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.class_4570;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class LootingEnchantLootFunction extends ConditionalLootFunction {
	private final UniformLootTableRange countRange;
	private final int limit;

	private LootingEnchantLootFunction(class_4570[] args, UniformLootTableRange uniformLootTableRange, int i) {
		super(args);
		this.countRange = uniformLootTableRange;
		this.limit = i;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
	}

	private boolean hasLimit() {
		return this.limit > 0;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Entity entity = lootContext.get(LootContextParameters.KILLER_ENTITY);
		if (entity instanceof LivingEntity) {
			int i = EnchantmentHelper.getLooting((LivingEntity)entity);
			if (i == 0) {
				return itemStack;
			}

			float f = (float)i * this.countRange.nextFloat(lootContext.getRandom());
			itemStack.increment(Math.round(f));
			if (this.hasLimit() && itemStack.getCount() > this.limit) {
				itemStack.setCount(this.limit);
			}
		}

		return itemStack;
	}

	public static LootingEnchantLootFunction.Builder builder(UniformLootTableRange uniformLootTableRange) {
		return new LootingEnchantLootFunction.Builder(uniformLootTableRange);
	}

	public static class Builder extends ConditionalLootFunction.Builder<LootingEnchantLootFunction.Builder> {
		private final UniformLootTableRange countRange;
		private int limit = 0;

		public Builder(UniformLootTableRange uniformLootTableRange) {
			this.countRange = uniformLootTableRange;
		}

		protected LootingEnchantLootFunction.Builder method_552() {
			return this;
		}

		public LootingEnchantLootFunction.Builder withLimit(int i) {
			this.limit = i;
			return this;
		}

		@Override
		public LootFunction build() {
			return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit);
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<LootingEnchantLootFunction> {
		protected Factory() {
			super(new Identifier("looting_enchant"), LootingEnchantLootFunction.class);
		}

		public void method_553(JsonObject jsonObject, LootingEnchantLootFunction lootingEnchantLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, lootingEnchantLootFunction, jsonSerializationContext);
			jsonObject.add("count", jsonSerializationContext.serialize(lootingEnchantLootFunction.countRange));
			if (lootingEnchantLootFunction.hasLimit()) {
				jsonObject.add("limit", jsonSerializationContext.serialize(lootingEnchantLootFunction.limit));
			}
		}

		public LootingEnchantLootFunction method_554(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
			int i = JsonHelper.getInt(jsonObject, "limit", 0);
			return new LootingEnchantLootFunction(args, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, UniformLootTableRange.class), i);
		}
	}
}
