package net.minecraft.entity.attribute;

import net.minecraft.util.registry.Registry;

public class EntityAttributes {
	public static final EntityAttribute field_23716 = register(
		"generic.max_health", new ClampedEntityAttribute("attribute.name.generic.max_health", 20.0, 1.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute field_23717 = register(
		"generic.follow_range", new ClampedEntityAttribute("attribute.name.generic.follow_range", 32.0, 0.0, 2048.0)
	);
	public static final EntityAttribute field_23718 = register(
		"generic.knockback_resistance", new ClampedEntityAttribute("attribute.name.generic.knockback_resistance", 0.0, 0.0, 1.0)
	);
	public static final EntityAttribute field_23719 = register(
		"generic.movement_speed", new ClampedEntityAttribute("attribute.name.generic.movement_speed", 0.7F, 0.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute field_23720 = register(
		"generic.flying_speed", new ClampedEntityAttribute("attribute.name.generic.flying_speed", 0.4F, 0.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute field_23721 = register(
		"generic.attack_damage", new ClampedEntityAttribute("attribute.name.generic.attack_damage", 2.0, 0.0, 2048.0)
	);
	public static final EntityAttribute field_23722 = register(
		"generic.attack_knockback", new ClampedEntityAttribute("attribute.name.generic.attack_knockback", 0.0, 0.0, 5.0)
	);
	public static final EntityAttribute field_23723 = register(
		"generic.attack_speed", new ClampedEntityAttribute("attribute.name.generic.attack_speed", 4.0, 0.1F, 1024.0).setTracked(true)
	);
	public static final EntityAttribute field_26761 = register(
		"generic.attack_reach", new ClampedEntityAttribute("attribute.name.generic.attack_reach", 3.0, 0.0, 6.0).setTracked(true)
	);
	public static final EntityAttribute field_23724 = register(
		"generic.armor", new ClampedEntityAttribute("attribute.name.generic.armor", 0.0, 0.0, 30.0).setTracked(true)
	);
	public static final EntityAttribute field_23725 = register(
		"generic.armor_toughness", new ClampedEntityAttribute("attribute.name.generic.armor_toughness", 0.0, 0.0, 20.0).setTracked(true)
	);
	public static final EntityAttribute field_23726 = register(
		"generic.luck", new ClampedEntityAttribute("attribute.name.generic.luck", 0.0, -1024.0, 1024.0).setTracked(true)
	);
	public static final EntityAttribute field_23727 = register(
		"zombie.spawn_reinforcements", new ClampedEntityAttribute("attribute.name.zombie.spawn_reinforcements", 0.0, 0.0, 1.0)
	);
	public static final EntityAttribute field_23728 = register(
		"horse.jump_strength", new ClampedEntityAttribute("attribute.name.horse.jump_strength", 0.7, 0.0, 2.0).setTracked(true)
	);

	private static EntityAttribute register(String id, EntityAttribute attribute) {
		return Registry.register(Registry.ATTRIBUTE, id, attribute);
	}
}
