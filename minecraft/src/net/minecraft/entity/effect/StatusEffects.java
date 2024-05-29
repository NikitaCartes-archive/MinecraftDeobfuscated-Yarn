package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class StatusEffects {
	private static final int DARKNESS_PADDING_DURATION = 22;
	public static final RegistryEntry<StatusEffect> SPEED = register(
		"speed",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.ofVanilla("effect.speed"), 0.2F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			)
	);
	public static final RegistryEntry<StatusEffect> SLOWNESS = register(
		"slowness",
		new StatusEffect(StatusEffectCategory.HARMFUL, 9154528)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.ofVanilla("effect.slowness"), -0.15F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			)
	);
	public static final RegistryEntry<StatusEffect> HASTE = register(
		"haste",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 14270531)
			.addAttributeModifier(
				EntityAttributes.GENERIC_ATTACK_SPEED, Identifier.ofVanilla("effect.haste"), 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			)
	);
	public static final RegistryEntry<StatusEffect> MINING_FATIGUE = register(
		"mining_fatigue",
		new StatusEffect(StatusEffectCategory.HARMFUL, 4866583)
			.addAttributeModifier(
				EntityAttributes.GENERIC_ATTACK_SPEED, Identifier.ofVanilla("effect.mining_fatigue"), -0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			)
	);
	public static final RegistryEntry<StatusEffect> STRENGTH = register(
		"strength",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 16762624)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, Identifier.ofVanilla("effect.strength"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> INSTANT_HEALTH = register(
		"instant_health", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179, false)
	);
	public static final RegistryEntry<StatusEffect> INSTANT_DAMAGE = register(
		"instant_damage", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.HARMFUL, 11101546, true)
	);
	public static final RegistryEntry<StatusEffect> JUMP_BOOST = register(
		"jump_boost",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 16646020)
			.addAttributeModifier(
				EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, Identifier.ofVanilla("effect.jump_boost"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE
			)
	);
	public static final RegistryEntry<StatusEffect> NAUSEA = register("nausea", new StatusEffect(StatusEffectCategory.HARMFUL, 5578058));
	public static final RegistryEntry<StatusEffect> REGENERATION = register(
		"regeneration", new RegenerationStatusEffect(StatusEffectCategory.BENEFICIAL, 13458603)
	);
	public static final RegistryEntry<StatusEffect> RESISTANCE = register("resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 9520880));
	public static final RegistryEntry<StatusEffect> FIRE_RESISTANCE = register("fire_resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16750848));
	public static final RegistryEntry<StatusEffect> WATER_BREATHING = register("water_breathing", new StatusEffect(StatusEffectCategory.BENEFICIAL, 10017472));
	public static final RegistryEntry<StatusEffect> INVISIBILITY = register("invisibility", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16185078));
	public static final RegistryEntry<StatusEffect> BLINDNESS = register("blindness", new StatusEffect(StatusEffectCategory.HARMFUL, 2039587));
	public static final RegistryEntry<StatusEffect> NIGHT_VISION = register("night_vision", new StatusEffect(StatusEffectCategory.BENEFICIAL, 12779366));
	public static final RegistryEntry<StatusEffect> HUNGER = register("hunger", new HungerStatusEffect(StatusEffectCategory.HARMFUL, 5797459));
	public static final RegistryEntry<StatusEffect> WEAKNESS = register(
		"weakness",
		new StatusEffect(StatusEffectCategory.HARMFUL, 4738376)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, Identifier.ofVanilla("effect.weakness"), -4.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> POISON = register("poison", new PoisonStatusEffect(StatusEffectCategory.HARMFUL, 8889187));
	public static final RegistryEntry<StatusEffect> WITHER = register("wither", new WitherStatusEffect(StatusEffectCategory.HARMFUL, 7561558));
	public static final RegistryEntry<StatusEffect> HEALTH_BOOST = register(
		"health_boost",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 16284963)
			.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, Identifier.ofVanilla("effect.health_boost"), 4.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> ABSORPTION = register(
		"absorption",
		new AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 2445989)
			.addAttributeModifier(EntityAttributes.GENERIC_MAX_ABSORPTION, Identifier.ofVanilla("effect.absorption"), 4.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> SATURATION = register("saturation", new SaturationStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179));
	public static final RegistryEntry<StatusEffect> GLOWING = register("glowing", new StatusEffect(StatusEffectCategory.NEUTRAL, 9740385));
	public static final RegistryEntry<StatusEffect> LEVITATION = register("levitation", new StatusEffect(StatusEffectCategory.HARMFUL, 13565951));
	public static final RegistryEntry<StatusEffect> LUCK = register(
		"luck",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 5882118)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, Identifier.ofVanilla("effect.luck"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> UNLUCK = register(
		"unluck",
		new StatusEffect(StatusEffectCategory.HARMFUL, 12624973)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, Identifier.ofVanilla("effect.unluck"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE)
	);
	public static final RegistryEntry<StatusEffect> SLOW_FALLING = register("slow_falling", new StatusEffect(StatusEffectCategory.BENEFICIAL, 15978425));
	public static final RegistryEntry<StatusEffect> CONDUIT_POWER = register("conduit_power", new StatusEffect(StatusEffectCategory.BENEFICIAL, 1950417));
	public static final RegistryEntry<StatusEffect> DOLPHINS_GRACE = register("dolphins_grace", new StatusEffect(StatusEffectCategory.BENEFICIAL, 8954814));
	public static final RegistryEntry<StatusEffect> BAD_OMEN = register(
		"bad_omen", new BadOmenStatusEffect(StatusEffectCategory.NEUTRAL, 745784).applySound(SoundEvents.EVENT_MOB_EFFECT_BAD_OMEN)
	);
	public static final RegistryEntry<StatusEffect> HERO_OF_THE_VILLAGE = register(
		"hero_of_the_village", new StatusEffect(StatusEffectCategory.BENEFICIAL, 4521796)
	);
	public static final RegistryEntry<StatusEffect> DARKNESS = register("darkness", new StatusEffect(StatusEffectCategory.HARMFUL, 2696993).fadeTicks(22));
	public static final RegistryEntry<StatusEffect> TRIAL_OMEN = register(
		"trial_omen", new StatusEffect(StatusEffectCategory.NEUTRAL, 1484454, ParticleTypes.TRIAL_OMEN).applySound(SoundEvents.EVENT_MOB_EFFECT_TRIAL_OMEN)
	);
	public static final RegistryEntry<StatusEffect> RAID_OMEN = register(
		"raid_omen", new RaidOmenStatusEffect(StatusEffectCategory.NEUTRAL, 14565464, ParticleTypes.RAID_OMEN).applySound(SoundEvents.EVENT_MOB_EFFECT_RAID_OMEN)
	);
	public static final RegistryEntry<StatusEffect> WIND_CHARGED = register("wind_charged", new WindChargedStatusEffect(StatusEffectCategory.HARMFUL, 12438015));
	public static final RegistryEntry<StatusEffect> WEAVING = register(
		"weaving", new WeavingStatusEffect(StatusEffectCategory.HARMFUL, 7891290, random -> MathHelper.nextBetween(random, 2, 3))
	);
	public static final RegistryEntry<StatusEffect> OOZING = register("oozing", new OozingStatusEffect(StatusEffectCategory.HARMFUL, 10092451, random -> 2));
	public static final RegistryEntry<StatusEffect> INFESTED = register(
		"infested", new InfestedStatusEffect(StatusEffectCategory.HARMFUL, 9214860, 0.1F, random -> MathHelper.nextBetween(random, 1, 2))
	);

	private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.ofVanilla(id), statusEffect);
	}

	public static RegistryEntry<StatusEffect> registerAndGetDefault(Registry<StatusEffect> registry) {
		return SPEED;
	}
}
