package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentValueEffectType;
import net.minecraft.util.math.random.Random;

public record RemoveBinomialEnchantmentEffectType(EnchantmentLevelBasedValueType chance) implements EnchantmentValueEffectType {
	public static final MapCodec<RemoveBinomialEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValueType.CODEC.fieldOf("chance").forGetter(RemoveBinomialEnchantmentEffectType::chance))
				.apply(instance, RemoveBinomialEnchantmentEffectType::new)
	);

	@Override
	public float apply(int level, Random random, float inputValue) {
		float f = this.chance.getValue(level);
		int i = 0;

		for (int j = 0; (float)j < inputValue; j++) {
			if (random.nextFloat() < f) {
				i++;
			}
		}

		return inputValue - (float)i;
	}

	@Override
	public MapCodec<RemoveBinomialEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}