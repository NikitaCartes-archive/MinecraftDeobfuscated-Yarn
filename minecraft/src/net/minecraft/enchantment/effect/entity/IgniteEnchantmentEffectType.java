package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record IgniteEnchantmentEffectType(EnchantmentLevelBasedValueType duration) implements EnchantmentEntityEffectType {
	public static final MapCodec<IgniteEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					EnchantmentLevelBasedValueType.CODEC.fieldOf("duration").forGetter(igniteEnchantmentEffectType -> igniteEnchantmentEffectType.duration)
				)
				.apply(instance, IgniteEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		user.setOnFireFor(this.duration.getValue(level));
	}

	@Override
	public MapCodec<IgniteEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
