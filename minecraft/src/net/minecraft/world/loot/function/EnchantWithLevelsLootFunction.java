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
	private final LootTableRange field_1026;
	private final boolean treasureEnchantmentsAllowed;

	private EnchantWithLevelsLootFunction(LootCondition[] lootConditions, LootTableRange lootTableRange, boolean bl) {
		super(lootConditions);
		this.field_1026 = lootTableRange;
		this.treasureEnchantmentsAllowed = bl;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Random random = lootContext.getRandom();
		return EnchantmentHelper.enchant(random, itemStack, this.field_1026.next(random), this.treasureEnchantmentsAllowed);
	}

	public static EnchantWithLevelsLootFunction.class_107 method_481(LootTableRange lootTableRange) {
		return new EnchantWithLevelsLootFunction.class_107(lootTableRange);
	}

	public static class Factory extends ConditionalLootFunction.Factory<EnchantWithLevelsLootFunction> {
		public Factory() {
			super(new Identifier("enchant_with_levels"), EnchantWithLevelsLootFunction.class);
		}

		public void serialize(JsonObject jsonObject, EnchantWithLevelsLootFunction enchantWithLevelsLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, enchantWithLevelsLootFunction, jsonSerializationContext);
			jsonObject.add("levels", LootTableRanges.serialize(enchantWithLevelsLootFunction.field_1026, jsonSerializationContext));
			jsonObject.addProperty("treasure", enchantWithLevelsLootFunction.treasureEnchantmentsAllowed);
		}

		public EnchantWithLevelsLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootTableRange lootTableRange = LootTableRanges.deserialize(jsonObject.get("levels"), jsonDeserializationContext);
			boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
			return new EnchantWithLevelsLootFunction(lootConditions, lootTableRange, bl);
		}
	}

	public static class class_107 extends ConditionalLootFunction.Builder<EnchantWithLevelsLootFunction.class_107> {
		private final LootTableRange field_1028;
		private boolean field_1029;

		public class_107(LootTableRange lootTableRange) {
			this.field_1028 = lootTableRange;
		}

		protected EnchantWithLevelsLootFunction.class_107 method_483() {
			return this;
		}

		public EnchantWithLevelsLootFunction.class_107 method_484() {
			this.field_1029 = true;
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantWithLevelsLootFunction(this.getConditions(), this.field_1028, this.field_1029);
		}
	}
}
