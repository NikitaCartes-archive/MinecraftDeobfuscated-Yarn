package net.minecraft.entity.attribute;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityAttributes {
	public static final EntityAttribute GENERIC_MAX_HEALTH = register(
		"generic.max_health", new ClampedEntityAttribute("attribute.name.generic.max_health", 20.0, 1.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_FOLLOW_RANGE = register(
		"generic.follow_range", new ClampedEntityAttribute("attribute.name.generic.follow_range", 32.0, 0.0, 2048.0)
	);
	public static final EntityAttribute GENERIC_KNOCKBACK_RESISTANCE = register(
		"generic.knockback_resistance", new ClampedEntityAttribute("attribute.name.generic.knockback_resistance", 0.0, 0.0, 1.0)
	);
	public static final EntityAttribute GENERIC_MOVEMENT_SPEED = register(
		"generic.movement_speed", new ClampedEntityAttribute("attribute.name.generic.movement_speed", 0.7F, 0.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_FLYING_SPEED = register(
		"generic.flying_speed", new ClampedEntityAttribute("attribute.name.generic.flying_speed", 0.4F, 0.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_ATTACK_DAMAGE = register(
		"generic.attack_damage", new ClampedEntityAttribute("attribute.name.generic.attack_damage", 2.0, 0.0, 2048.0)
	);
	public static final EntityAttribute GENERIC_ATTACK_KNOCKBACK = register(
		"generic.attack_knockback", new ClampedEntityAttribute("attribute.name.generic.attack_knockback", 0.0, 0.0, 5.0)
	);
	public static final EntityAttribute GENERIC_ATTACK_SPEED = register(
		"generic.attack_speed", new ClampedEntityAttribute("attribute.name.generic.attack_speed", 4.0, 0.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_ARMOR = register(
		"generic.armor", new ClampedEntityAttribute("attribute.name.generic.armor", 0.0, 0.0, 30.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_ARMOR_TOUGHNESS = register(
		"generic.armor_toughness", new ClampedEntityAttribute("attribute.name.generic.armor_toughness", 0.0, 0.0, 20.0).setTracked(true)
	);
	public static final EntityAttribute GENERIC_LUCK = register(
		"generic.luck", new ClampedEntityAttribute("attribute.name.generic.luck", 0.0, -1024.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute ZOMBIE_SPAWN_REINFORCEMENTS = register(
		"zombie.spawn_reinforcements", new ClampedEntityAttribute("attribute.name.zombie.spawn_reinforcements", 0.0, 0.0, 1.0)
	);
	public static final EntityAttribute HORSE_JUMP_STRENGTH = register(
		"horse.jump_strength", new ClampedEntityAttribute("attribute.name.horse.jump_strength", 0.7, 0.0, 2.0).setTracked(true)
	);

	private static EntityAttribute register(String id, EntityAttribute attribute) {
		return Registry.register(Registries.ATTRIBUTE, id, attribute);
	}
}
