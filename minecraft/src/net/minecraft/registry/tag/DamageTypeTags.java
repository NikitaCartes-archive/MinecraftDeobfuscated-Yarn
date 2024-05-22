package net.minecraft.registry.tag;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypeTags {
	TagKey<DamageType> DAMAGES_HELMET = of("damages_helmet");
	TagKey<DamageType> BYPASSES_ARMOR = of("bypasses_armor");
	TagKey<DamageType> BYPASSES_SHIELD = of("bypasses_shield");
	TagKey<DamageType> BYPASSES_INVULNERABILITY = of("bypasses_invulnerability");
	TagKey<DamageType> BYPASSES_COOLDOWN = of("bypasses_cooldown");
	TagKey<DamageType> BYPASSES_EFFECTS = of("bypasses_effects");
	TagKey<DamageType> BYPASSES_RESISTANCE = of("bypasses_resistance");
	TagKey<DamageType> BYPASSES_ENCHANTMENTS = of("bypasses_enchantments");
	TagKey<DamageType> IS_FIRE = of("is_fire");
	TagKey<DamageType> IS_PROJECTILE = of("is_projectile");
	TagKey<DamageType> WITCH_RESISTANT_TO = of("witch_resistant_to");
	TagKey<DamageType> IS_EXPLOSION = of("is_explosion");
	TagKey<DamageType> IS_FALL = of("is_fall");
	TagKey<DamageType> IS_DROWNING = of("is_drowning");
	TagKey<DamageType> IS_FREEZING = of("is_freezing");
	TagKey<DamageType> IS_LIGHTNING = of("is_lightning");
	TagKey<DamageType> NO_ANGER = of("no_anger");
	TagKey<DamageType> NO_IMPACT = of("no_impact");
	TagKey<DamageType> ALWAYS_MOST_SIGNIFICANT_FALL = of("always_most_significant_fall");
	TagKey<DamageType> WITHER_IMMUNE_TO = of("wither_immune_to");
	TagKey<DamageType> IGNITES_ARMOR_STANDS = of("ignites_armor_stands");
	TagKey<DamageType> BURNS_ARMOR_STANDS = of("burns_armor_stands");
	TagKey<DamageType> AVOIDS_GUARDIAN_THORNS = of("avoids_guardian_thorns");
	TagKey<DamageType> ALWAYS_TRIGGERS_SILVERFISH = of("always_triggers_silverfish");
	TagKey<DamageType> ALWAYS_HURTS_ENDER_DRAGONS = of("always_hurts_ender_dragons");
	TagKey<DamageType> NO_KNOCKBACK = of("no_knockback");
	TagKey<DamageType> ALWAYS_KILLS_ARMOR_STANDS = of("always_kills_armor_stands");
	TagKey<DamageType> CAN_BREAK_ARMOR_STAND = of("can_break_armor_stand");
	TagKey<DamageType> BYPASSES_WOLF_ARMOR = of("bypasses_wolf_armor");
	TagKey<DamageType> IS_PLAYER_ATTACK = of("is_player_attack");
	TagKey<DamageType> BURN_FROM_STEPPING = of("burn_from_stepping");
	TagKey<DamageType> PANIC_CAUSES = of("panic_causes");
	TagKey<DamageType> PANIC_ENVIRONMENTAL_CAUSES = of("panic_environmental_causes");

	private static TagKey<DamageType> of(String id) {
		return TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla(id));
	}
}
