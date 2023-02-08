package net.minecraft.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class Potions {
	public static RegistryKey<Potion> EMPTY_KEY = RegistryKey.of(RegistryKeys.POTION, new Identifier("empty"));
	public static final Potion EMPTY = register(EMPTY_KEY, new Potion());
	public static final Potion WATER = register("water", new Potion());
	public static final Potion MUNDANE = register("mundane", new Potion());
	public static final Potion THICK = register("thick", new Potion());
	public static final Potion AWKWARD = register("awkward", new Potion());
	public static final Potion NIGHT_VISION = register("night_vision", new Potion(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600)));
	public static final Potion LONG_NIGHT_VISION = register(
		"long_night_vision", new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600))
	);
	public static final Potion INVISIBILITY = register("invisibility", new Potion(new StatusEffectInstance(StatusEffects.INVISIBILITY, 3600)));
	public static final Potion LONG_INVISIBILITY = register(
		"long_invisibility", new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, 9600))
	);
	public static final Potion LEAPING = register("leaping", new Potion(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600)));
	public static final Potion LONG_LEAPING = register("long_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, 9600)));
	public static final Potion STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800, 1)));
	public static final Potion FIRE_RESISTANCE = register("fire_resistance", new Potion(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600)));
	public static final Potion LONG_FIRE_RESISTANCE = register(
		"long_fire_resistance", new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600))
	);
	public static final Potion SWIFTNESS = register("swiftness", new Potion(new StatusEffectInstance(StatusEffects.SPEED, 3600)));
	public static final Potion LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, 9600)));
	public static final Potion STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, 1800, 1)));
	public static final Potion SLOWNESS = register("slowness", new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 1800)));
	public static final Potion LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, 4800)));
	public static final Potion STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3)));
	public static final Potion TURTLE_MASTER = register(
		"turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3), new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 2))
	);
	public static final Potion LONG_TURTLE_MASTER = register(
		"long_turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.SLOWNESS, 800, 3), new StatusEffectInstance(StatusEffects.RESISTANCE, 800, 2))
	);
	public static final Potion STRONG_TURTLE_MASTER = register(
		"strong_turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 5), new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 3))
	);
	public static final Potion WATER_BREATHING = register("water_breathing", new Potion(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 3600)));
	public static final Potion LONG_WATER_BREATHING = register(
		"long_water_breathing", new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600))
	);
	public static final Potion HEALING = register("healing", new Potion(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1)));
	public static final Potion STRONG_HEALING = register("strong_healing", new Potion("healing", new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1)));
	public static final Potion HARMING = register("harming", new Potion(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1)));
	public static final Potion STRONG_HARMING = register("strong_harming", new Potion("harming", new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1)));
	public static final Potion POISON = register("poison", new Potion(new StatusEffectInstance(StatusEffects.POISON, 900)));
	public static final Potion LONG_POISON = register("long_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, 1800)));
	public static final Potion STRONG_POISON = register("strong_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, 432, 1)));
	public static final Potion REGENERATION = register("regeneration", new Potion(new StatusEffectInstance(StatusEffects.REGENERATION, 900)));
	public static final Potion LONG_REGENERATION = register(
		"long_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, 1800))
	);
	public static final Potion STRONG_REGENERATION = register(
		"strong_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, 450, 1))
	);
	public static final Potion STRENGTH = register("strength", new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 3600)));
	public static final Potion LONG_STRENGTH = register("long_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, 9600)));
	public static final Potion STRONG_STRENGTH = register("strong_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, 1800, 1)));
	public static final Potion WEAKNESS = register("weakness", new Potion(new StatusEffectInstance(StatusEffects.WEAKNESS, 1800)));
	public static final Potion LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, 4800)));
	public static final Potion LUCK = register("luck", new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, 6000)));
	public static final Potion SLOW_FALLING = register("slow_falling", new Potion(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800)));
	public static final Potion LONG_SLOW_FALLING = register(
		"long_slow_falling", new Potion("slow_falling", new StatusEffectInstance(StatusEffects.SLOW_FALLING, 4800))
	);

	private static Potion register(String name, Potion potion) {
		return Registry.register(Registries.POTION, name, potion);
	}

	private static Potion register(RegistryKey<Potion> key, Potion potion) {
		return Registry.register(Registries.POTION, key, potion);
	}
}
