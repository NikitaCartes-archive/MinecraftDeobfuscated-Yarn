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
	public float apply(int i, Random random, float f) {
		float g = this.chance.getValue(i);
		int j = 0;

		for (int k = 0; (float)k < f; k++) {
			if (random.nextFloat() < g) {
				j++;
			}
		}

		return f - (float)j;
	}

	@Override
	public MapCodec<RemoveBinomialEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
