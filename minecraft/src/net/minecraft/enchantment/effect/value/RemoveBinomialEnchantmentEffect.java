package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record RemoveBinomialEnchantmentEffect(EnchantmentLevelBasedValue chance) implements EnchantmentValueEffect {
	public static final MapCodec<RemoveBinomialEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomialEnchantmentEffect::chance))
				.apply(instance, RemoveBinomialEnchantmentEffect::new)
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
	public MapCodec<RemoveBinomialEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
