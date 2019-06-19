package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.LootTableRanges;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class EnchantWithLevelsLootFunction extends ConditionalLootFunction {
	private final LootTableRange range;
	private final boolean treasureEnchantmentsAllowed;

	private EnchantWithLevelsLootFunction(LootCondition[] lootConditions, LootTableRange lootTableRange, boolean bl) {
		super(lootConditions);
		this.range = lootTableRange;
		this.treasureEnchantmentsAllowed = bl;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Random random = lootContext.getRandom();
		return EnchantmentHelper.enchant(random, itemStack, this.range.next(random), this.treasureEnchantmentsAllowed);
	}

	public static EnchantWithLevelsLootFunction.Builder builder(LootTableRange lootTableRange) {
		return new EnchantWithLevelsLootFunction.Builder(lootTableRange);
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantWithLevelsLootFunction.Builder> {
		private final LootTableRange range;
		private boolean treasureEnchantmentsAllowed;

		public Builder(LootTableRange lootTableRange) {
			this.range = lootTableRange;
		}

		protected EnchantWithLevelsLootFunction.Builder method_483() {
			return this;
		}

		public EnchantWithLevelsLootFunction.Builder allowTreasureEnchantments() {
			this.treasureEnchantmentsAllowed = true;
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantWithLevelsLootFunction(this.getConditions(), this.range, this.treasureEnchantmentsAllowed);
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<EnchantWithLevelsLootFunction> {
		public Factory() {
			super(new Identifier("enchant_with_levels"), EnchantWithLevelsLootFunction.class);
		}

		public void method_485(JsonObject jsonObject, EnchantWithLevelsLootFunction enchantWithLevelsLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, enchantWithLevelsLootFunction, jsonSerializationContext);
			jsonObject.add("levels", LootTableRanges.serialize(enchantWithLevelsLootFunction.range, jsonSerializationContext));
			jsonObject.addProperty("treasure", enchantWithLevelsLootFunction.treasureEnchantmentsAllowed);
		}

		public EnchantWithLevelsLootFunction method_486(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootTableRange lootTableRange = LootTableRanges.deserialize(jsonObject.get("levels"), jsonDeserializationContext);
			boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
			return new EnchantWithLevelsLootFunction(lootConditions, lootTableRange, bl);
		}
	}
}
