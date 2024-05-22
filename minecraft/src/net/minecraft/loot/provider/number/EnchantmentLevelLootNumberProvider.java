package net.minecraft.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public record EnchantmentLevelLootNumberProvider(EnchantmentLevelBasedValue amount) implements LootNumberProvider {
	public static final MapCodec<EnchantmentLevelLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentLevelLootNumberProvider::amount))
				.apply(instance, EnchantmentLevelLootNumberProvider::new)
	);

	@Override
	public float nextFloat(LootContext context) {
		int i = context.requireParameter(LootContextParameters.ENCHANTMENT_LEVEL);
		return this.amount.getValue(i);
	}

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.ENCHANTMENT_LEVEL;
	}

	public static EnchantmentLevelLootNumberProvider create(EnchantmentLevelBasedValue amount) {
		return new EnchantmentLevelLootNumberProvider(amount);
	}
}
