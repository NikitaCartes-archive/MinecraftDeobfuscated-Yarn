package net.minecraft.entity.attribute;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EntityAttributes {
	public static final RegistryEntry<EntityAttribute> GENERIC_ARMOR = register(
		"generic.armor", new ClampedEntityAttribute("attribute.name.generic.armor", 0.0, 0.0, 30.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_ARMOR_TOUGHNESS = register(
		"generic.armor_toughness", new ClampedEntityAttribute("attribute.name.generic.armor_toughness", 0.0, 0.0, 20.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_ATTACK_DAMAGE = register(
		"generic.attack_damage", new ClampedEntityAttribute("attribute.name.generic.attack_damage", 2.0, 0.0, 2048.0)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_ATTACK_KNOCKBACK = register(
		"generic.attack_knockback", new ClampedEntityAttribute("attribute.name.generic.attack_knockback", 0.0, 0.0, 5.0)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_ATTACK_SPEED = register(
		"generic.attack_speed", new ClampedEntityAttribute("attribute.name.generic.attack_speed", 4.0, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_BLOCK_BREAK_SPEED = register(
		"player.block_break_speed", new ClampedEntityAttribute("attribute.name.player.block_break_speed", 1.0, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_BLOCK_INTERACTION_RANGE = register(
		"player.block_interaction_range", new ClampedEntityAttribute("attribute.name.player.block_interaction_range", 4.5, 0.0, 64.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_BURNING_TIME = register(
		"generic.burning_time",
		new ClampedEntityAttribute("attribute.name.generic.burning_time", 1.0, 0.0, 1024.0).setTracked(true).setCategory(EntityAttribute.Category.NEGATIVE)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE = register(
		"generic.explosion_knockback_resistance", new ClampedEntityAttribute("attribute.name.generic.explosion_knockback_resistance", 0.0, 0.0, 1.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_ENTITY_INTERACTION_RANGE = register(
		"player.entity_interaction_range", new ClampedEntityAttribute("attribute.name.player.entity_interaction_range", 3.0, 0.0, 64.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_FALL_DAMAGE_MULTIPLIER = register(
		"generic.fall_damage_multiplier",
		new ClampedEntityAttribute("attribute.name.generic.fall_damage_multiplier", 1.0, 0.0, 100.0).setTracked(true).setCategory(EntityAttribute.Category.NEGATIVE)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_FLYING_SPEED = register(
		"generic.flying_speed", new ClampedEntityAttribute("attribute.name.generic.flying_speed", 0.4, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_FOLLOW_RANGE = register(
		"generic.follow_range", new ClampedEntityAttribute("attribute.name.generic.follow_range", 32.0, 0.0, 2048.0)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_GRAVITY = register(
		"generic.gravity",
		new ClampedEntityAttribute("attribute.name.generic.gravity", 0.08, -1.0, 1.0).setTracked(true).setCategory(EntityAttribute.Category.NEUTRAL)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_JUMP_STRENGTH = register(
		"generic.jump_strength", new ClampedEntityAttribute("attribute.name.generic.jump_strength", 0.42F, 0.0, 32.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_KNOCKBACK_RESISTANCE = register(
		"generic.knockback_resistance", new ClampedEntityAttribute("attribute.name.generic.knockback_resistance", 0.0, 0.0, 1.0)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_LUCK = register(
		"generic.luck", new ClampedEntityAttribute("attribute.name.generic.luck", 0.0, -1024.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_MAX_ABSORPTION = register(
		"generic.max_absorption", new ClampedEntityAttribute("attribute.name.generic.max_absorption", 0.0, 0.0, 2048.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_MAX_HEALTH = register(
		"generic.max_health", new ClampedEntityAttribute("attribute.name.generic.max_health", 20.0, 1.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_MINING_EFFICIENCY = register(
		"player.mining_efficiency", new ClampedEntityAttribute("attribute.name.player.mining_efficiency", 0.0, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_MOVEMENT_EFFICIENCY = register(
		"generic.movement_efficiency", new ClampedEntityAttribute("attribute.name.generic.movement_efficiency", 0.0, 0.0, 1.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_MOVEMENT_SPEED = register(
		"generic.movement_speed", new ClampedEntityAttribute("attribute.name.generic.movement_speed", 0.7, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_OXYGEN_BONUS = register(
		"generic.oxygen_bonus", new ClampedEntityAttribute("attribute.name.generic.oxygen_bonus", 0.0, 0.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_SAFE_FALL_DISTANCE = register(
		"generic.safe_fall_distance", new ClampedEntityAttribute("attribute.name.generic.safe_fall_distance", 3.0, -1024.0, 1024.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_SCALE = register(
		"generic.scale", new ClampedEntityAttribute("attribute.name.generic.scale", 1.0, 0.0625, 16.0).setTracked(true).setCategory(EntityAttribute.Category.NEUTRAL)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_SNEAKING_SPEED = register(
		"player.sneaking_speed", new ClampedEntityAttribute("attribute.name.player.sneaking_speed", 0.3, 0.0, 1.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> ZOMBIE_SPAWN_REINFORCEMENTS = register(
		"zombie.spawn_reinforcements", new ClampedEntityAttribute("attribute.name.zombie.spawn_reinforcements", 0.0, 0.0, 1.0)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_STEP_HEIGHT = register(
		"generic.step_height", new ClampedEntityAttribute("attribute.name.generic.step_height", 0.6, 0.0, 10.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_SUBMERGED_MINING_SPEED = register(
		"player.submerged_mining_speed", new ClampedEntityAttribute("attribute.name.player.submerged_mining_speed", 0.2, 0.0, 20.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> PLAYER_SWEEPING_DAMAGE_RATIO = register(
		"player.sweeping_damage_ratio", new ClampedEntityAttribute("attribute.name.player.sweeping_damage_ratio", 0.0, 0.0, 1.0).setTracked(true)
	);
	public static final RegistryEntry<EntityAttribute> GENERIC_WATER_MOVEMENT_EFFICIENCY = register(
		"generic.water_movement_efficiency", new ClampedEntityAttribute("attribute.name.generic.water_movement_efficiency", 0.0, 0.0, 1.0).setTracked(true)
	);

	private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
		return Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), attribute);
	}

	public static RegistryEntry<EntityAttribute> registerAndGetDefault(Registry<EntityAttribute> registry) {
		return GENERIC_MAX_HEALTH;
	}
}
