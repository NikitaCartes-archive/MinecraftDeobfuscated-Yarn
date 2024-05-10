package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentValueEffectType;
import net.minecraft.util.math.random.Random;

public record MultiplyEnchantmentEffectType(EnchantmentLevelBasedValueType factor) implements EnchantmentValueEffectType {
	public static final MapCodec<MultiplyEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValueType.CODEC.fieldOf("factor").forGetter(MultiplyEnchantmentEffectType::factor))
				.apply(instance, MultiplyEnchantmentEffectType::new)
	);

	@Override
	public float apply(int level, Random random, float inputValue) {
		return inputValue * this.factor.getValue(level);
	}

	@Override
	public MapCodec<MultiplyEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
