package net.minecraft.enchantment.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentValueEffectType;
import net.minecraft.util.math.random.Random;

public record SetEnchantmentEffectType(EnchantmentLevelBasedValueType value) implements EnchantmentValueEffectType {
	public static final MapCodec<SetEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(EnchantmentLevelBasedValueType.CODEC.fieldOf("value").forGetter(SetEnchantmentEffectType::value))
				.apply(instance, SetEnchantmentEffectType::new)
	);

	@Override
	public float apply(int i, Random random, float f) {
		return this.value.getValue(i);
	}

	@Override
	public MapCodec<SetEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
