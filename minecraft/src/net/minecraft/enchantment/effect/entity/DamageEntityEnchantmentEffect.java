package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record DamageEntityEnchantmentEffect(EnchantmentLevelBasedValue minDamage, EnchantmentLevelBasedValue maxDamage, RegistryEntry<DamageType> damageType)
	implements EnchantmentEntityEffect {
	public static final MapCodec<DamageEntityEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					EnchantmentLevelBasedValue.CODEC.fieldOf("min_damage").forGetter(DamageEntityEnchantmentEffect::minDamage),
					EnchantmentLevelBasedValue.CODEC.fieldOf("max_damage").forGetter(DamageEntityEnchantmentEffect::maxDamage),
					DamageType.ENTRY_CODEC.fieldOf("damage_type").forGetter(DamageEntityEnchantmentEffect::damageType)
				)
				.apply(instance, DamageEntityEnchantmentEffect::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		float f = MathHelper.nextBetween(user.getRandom(), this.minDamage.getValue(level), this.maxDamage.getValue(level));
		user.damage(new DamageSource(this.damageType, context.owner()), f);
	}

	@Override
	public MapCodec<DamageEntityEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
