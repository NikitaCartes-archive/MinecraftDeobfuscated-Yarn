package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.entity.ApplyMobEffectEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.DamageEntityEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.DamageItemEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.ExplodeEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.PlaySoundEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.ReplaceBlockEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.ReplaceDiscEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.RunFunctionEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.SetBlockPropertiesEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffectType;
import net.minecraft.enchantment.effect.entity.SummonEntityEnchantmentEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface EnchantmentLocationBasedEffectType {
	Codec<EnchantmentLocationBasedEffectType> CODEC = Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE
		.getCodec()
		.dispatch(EnchantmentLocationBasedEffectType::getCodec, Function.identity());

	static MapCodec<? extends EnchantmentLocationBasedEffectType> registerAndGetDefault(Registry<MapCodec<? extends EnchantmentLocationBasedEffectType>> registry) {
		Registry.register(registry, "all_of", AllOfEnchantmentEffectTypes.LocationBasedEffects.CODEC);
		Registry.register(registry, "apply_mob_effect", ApplyMobEffectEnchantmentEffectType.CODEC);
		Registry.register(registry, "attribute", AttributeEnchantmentEffectType.CODEC);
		Registry.register(registry, "damage_entity", DamageEntityEnchantmentEffectType.CODEC);
		Registry.register(registry, "damage_item", DamageItemEnchantmentEffectType.CODEC);
		Registry.register(registry, "explode", ExplodeEnchantmentEffectType.CODEC);
		Registry.register(registry, "ignite", IgniteEnchantmentEffectType.CODEC);
		Registry.register(registry, "play_sound", PlaySoundEnchantmentEffectType.CODEC);
		Registry.register(registry, "replace_block", ReplaceBlockEnchantmentEffectType.CODEC);
		Registry.register(registry, "replace_disc", ReplaceDiscEnchantmentEffectType.CODEC);
		Registry.register(registry, "run_function", RunFunctionEnchantmentEffectType.CODEC);
		Registry.register(registry, "set_block_properties", SetBlockPropertiesEnchantmentEffectType.CODEC);
		Registry.register(registry, "spawn_particles", SpawnParticlesEnchantmentEffectType.CODEC);
		return Registry.register(registry, "summon_entity", SummonEntityEnchantmentEffectType.CODEC);
	}

	void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean bl);

	default void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
	}

	MapCodec<? extends EnchantmentLocationBasedEffectType> getCodec();
}
