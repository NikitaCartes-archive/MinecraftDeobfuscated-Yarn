package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record AddEnchantmentEffect(EnchantmentLevelBasedValue value) implements EnchantmentValueEffect {
	public static final MapCodec<AddEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter(AddEnchantmentEffect::value))
				.apply(instance, AddEnchantmentEffect::new)
	);

	@Override
	public float apply(int level, Random random, float inputValue) {
		return inputValue + this.value.getValue(level);
	}

	@Override
	public MapCodec<AddEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
