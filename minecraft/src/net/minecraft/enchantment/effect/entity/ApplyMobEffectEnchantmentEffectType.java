package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record ApplyMobEffectEnchantmentEffectType(
	RegistryEntryList<StatusEffect> toApply,
	EnchantmentLevelBasedValueType minDuration,
	EnchantmentLevelBasedValueType maxDuration,
	EnchantmentLevelBasedValueType minAmplifier,
	EnchantmentLevelBasedValueType maxAmplifier
) implements EnchantmentEntityEffectType {
	public static final MapCodec<ApplyMobEffectEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.STATUS_EFFECT).fieldOf("to_apply").forGetter(ApplyMobEffectEnchantmentEffectType::toApply),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("min_duration").forGetter(ApplyMobEffectEnchantmentEffectType::minDuration),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("max_duration").forGetter(ApplyMobEffectEnchantmentEffectType::maxDuration),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("min_amplifier").forGetter(ApplyMobEffectEnchantmentEffectType::minAmplifier),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("max_amplifier").forGetter(ApplyMobEffectEnchantmentEffectType::maxAmplifier)
				)
				.apply(instance, ApplyMobEffectEnchantmentEffectType::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		if (user instanceof LivingEntity livingEntity) {
			Random random = livingEntity.getRandom();
			Optional<RegistryEntry<StatusEffect>> optional = this.toApply.getRandom(random);
			if (optional.isPresent()) {
				int i = Math.round(MathHelper.nextBetween(random, this.minDuration.getValue(level), this.maxDuration.getValue(level)) * 20.0F);
				int j = Math.max(0, Math.round(MathHelper.nextBetween(random, this.minAmplifier.getValue(level), this.maxAmplifier.getValue(level))));
				livingEntity.addStatusEffect(new StatusEffectInstance((RegistryEntry<StatusEffect>)optional.get(), i, j));
			}
		}
	}

	@Override
	public MapCodec<ApplyMobEffectEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
