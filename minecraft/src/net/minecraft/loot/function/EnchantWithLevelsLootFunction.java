package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.math.random.Random;

public class EnchantWithLevelsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<EnchantWithLevelsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<LootNumberProvider, boolean>and(
					instance.group(
						LootNumberProviderTypes.CODEC.fieldOf("levels").forGetter(function -> function.range),
						Codec.BOOL.fieldOf("treasure").orElse(false).forGetter(function -> function.treasureEnchantmentsAllowed)
					)
				)
				.apply(instance, EnchantWithLevelsLootFunction::new)
	);
	private final LootNumberProvider range;
	private final boolean treasureEnchantmentsAllowed;

	EnchantWithLevelsLootFunction(List<LootCondition> conditions, LootNumberProvider range, boolean treasureEnchantmentsAllowed) {
		super(conditions);
		this.range = range;
		this.treasureEnchantmentsAllowed = treasureEnchantmentsAllowed;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.ENCHANT_WITH_LEVELS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.range.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Random random = context.getRandom();
		return EnchantmentHelper.enchant(context.getWorld().getEnabledFeatures(), random, stack, this.range.nextInt(context), this.treasureEnchantmentsAllowed);
	}

	public static EnchantWithLevelsLootFunction.Builder builder(LootNumberProvider range) {
		return new EnchantWithLevelsLootFunction.Builder(range);
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantWithLevelsLootFunction.Builder> {
		private final LootNumberProvider range;
		private boolean treasureEnchantmentsAllowed;

		public Builder(LootNumberProvider range) {
			this.range = range;
		}

		protected EnchantWithLevelsLootFunction.Builder getThisBuilder() {
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
}
