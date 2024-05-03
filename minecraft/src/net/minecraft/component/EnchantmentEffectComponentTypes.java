package net.minecraft.component;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffectType;
import net.minecraft.enchantment.effect.DamageImmunityEnchantmentEffectType;
import net.minecraft.enchantment.effect.EnchantmentEffectConditions;
import net.minecraft.enchantment.effect.EnchantmentEntityEffectType;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffectType;
import net.minecraft.enchantment.effect.EnchantmentValueEffectType;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffectType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Unit;

public interface EnchantmentEffectComponentTypes {
	Codec<ComponentType<?>> COMPONENT_TYPE_CODEC = Codec.lazyInitialized(() -> Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getCodec());
	Codec<ComponentMap> COMPONENT_MAP_CODEC = ComponentMap.createCodec(COMPONENT_TYPE_CODEC);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> DAMAGE_PROTECTION = register(
		"damage_protection",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<DamageImmunityEnchantmentEffectType>>> DAMAGE_IMMUNITY = register(
		"damage_immunity",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(DamageImmunityEnchantmentEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> DAMAGE = register(
		"damage", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> SMASH_DAMAGE_PER_FALLEN_BLOCK = register(
		"smash_damage_per_fallen_block",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> KNOCKBACK = register(
		"knockback", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> ARMOR_EFFECTIVENESS = register(
		"armor_effectiveness",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<TargetedEnchantmentEffectType<EnchantmentEntityEffectType>>> POST_ATTACK = register(
		"post_attack",
		builder -> builder.codec(TargetedEnchantmentEffectType.createPostAttackCodec(EnchantmentEntityEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentEntityEffectType>>> HIT_BLOCK = register(
		"hit_block", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentEntityEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> ITEM_DAMAGE = register(
		"item_damage", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<AttributeEnchantmentEffectType>> ATTRIBUTES = register(
		"attributes", builder -> builder.codec(AttributeEnchantmentEffectType.CODEC.codec().listOf())
	);
	ComponentType<List<TargetedEnchantmentEffectType<EnchantmentValueEffectType>>> EQUIPMENT_DROPS = register(
		"equipment_drops",
		builder -> builder.codec(
				TargetedEnchantmentEffectType.createEquipmentDropsCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf()
			)
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentLocationBasedEffectType>>> LOCATION_CHANGED = register(
		"location_changed",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentLocationBasedEffectType.CODEC, LootContextTypes.ENCHANTED_LOCATION).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentEntityEffectType>>> TICK = register(
		"tick", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentEntityEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> AMMO_USE = register(
		"ammo_use", builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> PROJECTILE_PIERCING = register(
		"projectile_piercing",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentEntityEffectType>>> PROJECTILE_SPAWNED = register(
		"projectile_spawned",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentEntityEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> PROJECTILE_SPREAD = register(
		"projectile_spread",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> PROJECTILE_COUNT = register(
		"projectile_count",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> CROSSBOW_CHARGE_TIME = register(
		"crossbow_charge_time",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> TRIDENT_RETURN_ACCELERATION = register(
		"trident_return_acceleration",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> FISHING_TIME_REDUCTION = register(
		"fishing_time_reduction",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> FISHING_LUCK_BONUS = register(
		"fishing_luck_bonus",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> BLOCK_EXPERIENCE = register(
		"block_experience",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> MOB_EXPERIENCE = register(
		"mob_experience",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> TRIDENT_SPIN_ATTACK_STRENGTH = register(
		"trident_spin_attack_strength",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ENTITY).listOf())
	);
	ComponentType<List<EnchantmentEffectConditions<EnchantmentValueEffectType>>> REPAIR_WITH_XP = register(
		"repair_with_xp",
		builder -> builder.codec(EnchantmentEffectConditions.createCodec(EnchantmentValueEffectType.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf())
	);
	ComponentType<List<CrossbowItem.LoadingSounds>> CROSSBOW_CHARGING_SOUNDS = register(
		"crossbow_charging_sounds", builder -> builder.codec(CrossbowItem.LoadingSounds.CODEC.listOf())
	);
	ComponentType<List<RegistryEntry<SoundEvent>>> TRIDENT_SOUND = register("trident_sound", builder -> builder.codec(SoundEvent.ENTRY_CODEC.listOf()));
	ComponentType<Unit> PREVENT_EQUIPMENT_DROP = register("prevent_equipment_drop", builder -> builder.codec(Unit.CODEC));
	ComponentType<Unit> PREVENT_ARMOR_CHANGE = register("prevent_armor_change", builder -> builder.codec(Unit.CODEC));

	static ComponentType<?> getDefault(Registry<ComponentType<?>> registry) {
		return DAMAGE_PROTECTION;
	}

	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
	}
}
