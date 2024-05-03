package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record DamageItemEnchantmentEffectType(EnchantmentLevelBasedValueType amount) implements EnchantmentEntityEffectType {
	public static final MapCodec<DamageItemEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					EnchantmentLevelBasedValueType.CODEC.fieldOf("amount").forGetter(damageItemEnchantmentEffectType -> damageItemEnchantmentEffectType.amount)
				)
				.apply(instance, DamageItemEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		ServerPlayerEntity serverPlayerEntity2 = context.owner() instanceof ServerPlayerEntity serverPlayerEntity ? serverPlayerEntity : null;
		context.stack().damage((int)this.amount.getValue(level), world, serverPlayerEntity2, context::onBreak);
	}

	@Override
	public MapCodec<DamageItemEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
