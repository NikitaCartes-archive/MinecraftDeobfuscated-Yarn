package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentValueEffectType;
import net.minecraft.util.math.random.Random;

public record AddEnchantmentEffectType(EnchantmentLevelBasedValueType value) implements EnchantmentValueEffectType {
	public static final MapCodec<AddEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValueType.CODEC.fieldOf("value").forGetter(AddEnchantmentEffectType::value))
				.apply(instance, AddEnchantmentEffectType::new)
	);

	@Override
	public float apply(int level, Random random, float inputValue) {
		return inputValue + this.value.getValue(level);
	}

	@Override
	public MapCodec<AddEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
