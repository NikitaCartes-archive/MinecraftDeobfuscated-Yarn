package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameter;
import net.minecraft.world.loot.context.Parameters;

public class LootingEnchantLootFunction extends ConditionalLootFunction {
	private final UniformLootTableRange range;
	private final int amount;

	private LootingEnchantLootFunction(LootCondition[] lootConditions, UniformLootTableRange uniformLootTableRange, int i) {
		super(lootConditions);
		this.range = uniformLootTableRange;
		this.amount = i;
	}

	@Override
	public Set<Parameter<?>> getRequiredParameters() {
		return ImmutableSet.of(Parameters.field_1230);
	}

	private boolean method_549() {
		return this.amount > 0;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Entity entity = lootContext.get(Parameters.field_1230);
		if (entity instanceof LivingEntity) {
			int i = EnchantmentHelper.getLooting((LivingEntity)entity);
			if (i == 0) {
				return itemStack;
			}

			float f = (float)i * this.range.nextFloat(lootContext.getRandom());
			itemStack.addAmount(Math.round(f));
			if (this.method_549() && itemStack.getAmount() > this.amount) {
				itemStack.setAmount(this.amount);
			}
		}

		return itemStack;
	}

	public static LootingEnchantLootFunction.class_126 method_547(UniformLootTableRange uniformLootTableRange) {
		return new LootingEnchantLootFunction.class_126(uniformLootTableRange);
	}

	public static class Factory extends ConditionalLootFunction.Factory<LootingEnchantLootFunction> {
		protected Factory() {
			super(new Identifier("looting_enchant"), LootingEnchantLootFunction.class);
		}

		public void method_553(JsonObject jsonObject, LootingEnchantLootFunction lootingEnchantLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, lootingEnchantLootFunction, jsonSerializationContext);
			jsonObject.add("count", jsonSerializationContext.serialize(lootingEnchantLootFunction.range));
			if (lootingEnchantLootFunction.method_549()) {
				jsonObject.add("limit", jsonSerializationContext.serialize(lootingEnchantLootFunction.amount));
			}
		}

		public LootingEnchantLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			int i = JsonHelper.getInt(jsonObject, "limit", 0);
			return new LootingEnchantLootFunction(
				lootConditions, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, UniformLootTableRange.class), i
			);
		}
	}

	public static class class_126 extends ConditionalLootFunction.Builder<LootingEnchantLootFunction.class_126> {
		private final UniformLootTableRange field_1084;
		private int field_1085 = 0;

		public class_126(UniformLootTableRange uniformLootTableRange) {
			this.field_1084 = uniformLootTableRange;
		}

		protected LootingEnchantLootFunction.class_126 method_552() {
			return this;
		}

		public LootingEnchantLootFunction.class_126 method_551(int i) {
			this.field_1085 = i;
			return this;
		}

		@Override
		public LootFunction build() {
			return new LootingEnchantLootFunction(this.getConditions(), this.field_1084, this.field_1085);
		}
	}
}
