package net.minecraft.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.registry.Registry;

public class Potions {
	public static final Potion field_8984 = register("empty", new Potion());
	public static final Potion field_8991 = register("water", new Potion());
	public static final Potion field_8967 = register("mundane", new Potion());
	public static final Potion field_8985 = register("thick", new Potion());
	public static final Potion field_8999 = register("awkward", new Potion());
	public static final Potion field_8968 = register("night_vision", new Potion(new StatusEffectInstance(StatusEffects.field_5925, 3600)));
	public static final Potion field_8981 = register("long_night_vision", new Potion("night_vision", new StatusEffectInstance(StatusEffects.field_5925, 9600)));
	public static final Potion field_8997 = register("invisibility", new Potion(new StatusEffectInstance(StatusEffects.field_5905, 3600)));
	public static final Potion field_9000 = register("long_invisibility", new Potion("invisibility", new StatusEffectInstance(StatusEffects.field_5905, 9600)));
	public static final Potion field_8979 = register("leaping", new Potion(new StatusEffectInstance(StatusEffects.field_5913, 3600)));
	public static final Potion field_8971 = register("long_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.field_5913, 9600)));
	public static final Potion field_8998 = register("strong_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.field_5913, 1800, 1)));
	public static final Potion field_8987 = register("fire_resistance", new Potion(new StatusEffectInstance(StatusEffects.field_5918, 3600)));
	public static final Potion field_8969 = register(
		"long_fire_resistance", new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.field_5918, 9600))
	);
	public static final Potion field_9005 = register("swiftness", new Potion(new StatusEffectInstance(StatusEffects.field_5904, 3600)));
	public static final Potion field_8983 = register("long_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.field_5904, 9600)));
	public static final Potion field_8966 = register("strong_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.field_5904, 1800, 1)));
	public static final Potion field_8996 = register("slowness", new Potion(new StatusEffectInstance(StatusEffects.field_5909, 1800)));
	public static final Potion field_8989 = register("long_slowness", new Potion("slowness", new StatusEffectInstance(StatusEffects.field_5909, 4800)));
	public static final Potion field_8976 = register("strong_slowness", new Potion("slowness", new StatusEffectInstance(StatusEffects.field_5909, 400, 3)));
	public static final Potion field_8990 = register(
		"turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.field_5909, 400, 3), new StatusEffectInstance(StatusEffects.field_5907, 400, 2))
	);
	public static final Potion field_8988 = register(
		"long_turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.field_5909, 800, 3), new StatusEffectInstance(StatusEffects.field_5907, 800, 2))
	);
	public static final Potion field_8977 = register(
		"strong_turtle_master",
		new Potion("turtle_master", new StatusEffectInstance(StatusEffects.field_5909, 400, 5), new StatusEffectInstance(StatusEffects.field_5907, 400, 3))
	);
	public static final Potion field_8994 = register("water_breathing", new Potion(new StatusEffectInstance(StatusEffects.field_5923, 3600)));
	public static final Potion field_9001 = register(
		"long_water_breathing", new Potion("water_breathing", new StatusEffectInstance(StatusEffects.field_5923, 9600))
	);
	public static final Potion field_8963 = register("healing", new Potion(new StatusEffectInstance(StatusEffects.field_5915, 1)));
	public static final Potion field_8980 = register("strong_healing", new Potion("healing", new StatusEffectInstance(StatusEffects.field_5915, 1, 1)));
	public static final Potion field_9004 = register("harming", new Potion(new StatusEffectInstance(StatusEffects.field_5921, 1)));
	public static final Potion field_8973 = register("strong_harming", new Potion("harming", new StatusEffectInstance(StatusEffects.field_5921, 1, 1)));
	public static final Potion field_8982 = register("poison", new Potion(new StatusEffectInstance(StatusEffects.field_5899, 900)));
	public static final Potion field_9002 = register("long_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.field_5899, 1800)));
	public static final Potion field_8972 = register("strong_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.field_5899, 432, 1)));
	public static final Potion field_8986 = register("regeneration", new Potion(new StatusEffectInstance(StatusEffects.field_5924, 900)));
	public static final Potion field_9003 = register("long_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.field_5924, 1800)));
	public static final Potion field_8992 = register("strong_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.field_5924, 450, 1)));
	public static final Potion field_8978 = register("strength", new Potion(new StatusEffectInstance(StatusEffects.field_5910, 3600)));
	public static final Potion field_8965 = register("long_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.field_5910, 9600)));
	public static final Potion field_8993 = register("strong_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.field_5910, 1800, 1)));
	public static final Potion field_8975 = register("weakness", new Potion(new StatusEffectInstance(StatusEffects.field_5911, 1800)));
	public static final Potion field_8970 = register("long_weakness", new Potion("weakness", new StatusEffectInstance(StatusEffects.field_5911, 4800)));
	public static final Potion field_8995 = register("luck", new Potion("luck", new StatusEffectInstance(StatusEffects.field_5926, 6000)));
	public static final Potion field_8974 = register("slow_falling", new Potion(new StatusEffectInstance(StatusEffects.field_5906, 1800)));
	public static final Potion field_8964 = register("long_slow_falling", new Potion("slow_falling", new StatusEffectInstance(StatusEffects.field_5906, 4800)));

	private static Potion register(String string, Potion potion) {
		return Registry.register(Registry.POTION, string, potion);
	}
}
