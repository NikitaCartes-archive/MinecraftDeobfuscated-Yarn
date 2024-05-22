package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record SetEnchantmentEffect(EnchantmentLevelBasedValue value) implements EnchantmentValueEffect {
	public static final MapCodec<SetEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter(SetEnchantmentEffect::value))
				.apply(instance, SetEnchantmentEffect::new)
	);

	@Override
	public float apply(int level, Random random, float inputValue) {
		return this.value.getValue(level);
	}

	@Override
	public MapCodec<SetEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
