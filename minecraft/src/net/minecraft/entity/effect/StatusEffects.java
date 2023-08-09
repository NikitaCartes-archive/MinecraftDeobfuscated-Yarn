package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class StatusEffects {
	private static final int DARKNESS_PADDING_DURATION = 22;
	public static final StatusEffect SPEED = register(
		"speed",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
			)
	);
	public static final StatusEffect SLOWNESS = register(
		"slowness",
		new StatusEffect(StatusEffectCategory.HARMFUL, 9154528)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
			)
	);
	public static final StatusEffect HASTE = register(
		"haste",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 14270531)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
	);
	public static final StatusEffect MINING_FATIGUE = register(
		"mining_fatigue",
		new StatusEffect(StatusEffectCategory.HARMFUL, 4866583)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
	);
	public static final StatusEffect STRENGTH = register(
		"strength",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 16762624)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect INSTANT_HEALTH = register(
		"instant_health", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179, false)
	);
	public static final StatusEffect INSTANT_DAMAGE = register(
		"instant_damage", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.HARMFUL, 11101546, true)
	);
	public static final StatusEffect JUMP_BOOST = register("jump_boost", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16646020));
	public static final StatusEffect NAUSEA = register("nausea", new StatusEffect(StatusEffectCategory.HARMFUL, 5578058));
	public static final StatusEffect REGENERATION = register("regeneration", new RegenerationStatusEffect(StatusEffectCategory.BENEFICIAL, 13458603));
	public static final StatusEffect RESISTANCE = register("resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 9520880));
	public static final StatusEffect FIRE_RESISTANCE = register("fire_resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16750848));
	public static final StatusEffect WATER_BREATHING = register("water_breathing", new StatusEffect(StatusEffectCategory.BENEFICIAL, 10017472));
	public static final StatusEffect INVISIBILITY = register("invisibility", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16185078));
	public static final StatusEffect BLINDNESS = register("blindness", new StatusEffect(StatusEffectCategory.HARMFUL, 2039587));
	public static final StatusEffect NIGHT_VISION = register("night_vision", new StatusEffect(StatusEffectCategory.BENEFICIAL, 12779366));
	public static final StatusEffect HUNGER = register("hunger", new HungerStatusEffect(StatusEffectCategory.HARMFUL, 5797459));
	public static final StatusEffect WEAKNESS = register(
		"weakness",
		new StatusEffect(StatusEffectCategory.HARMFUL, 4738376)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", -4.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect POISON = register("poison", new PoisonStatusEffect(StatusEffectCategory.HARMFUL, 8889187));
	public static final StatusEffect WITHER = register("wither", new WitherStatusEffect(StatusEffectCategory.HARMFUL, 7561558));
	public static final StatusEffect HEALTH_BOOST = register(
		"health_boost",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 16284963)
			.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect ABSORPTION = register(
		"absorption",
		new AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 2445989)
			.addAttributeModifier(EntityAttributes.GENERIC_MAX_ABSORPTION, "EAE29CF0-701E-4ED6-883A-96F798F3DAB5", 4.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect SATURATION = register("saturation", new SaturationStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179));
	public static final StatusEffect GLOWING = register("glowing", new StatusEffect(StatusEffectCategory.NEUTRAL, 9740385));
	public static final StatusEffect LEVITATION = register("levitation", new StatusEffect(StatusEffectCategory.HARMFUL, 13565951));
	public static final StatusEffect LUCK = register(
		"luck",
		new StatusEffect(StatusEffectCategory.BENEFICIAL, 5882118)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect UNLUCK = register(
		"unluck",
		new StatusEffect(StatusEffectCategory.HARMFUL, 12624973)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect SLOW_FALLING = register("slow_falling", new StatusEffect(StatusEffectCategory.BENEFICIAL, 15978425));
	public static final StatusEffect CONDUIT_POWER = register("conduit_power", new StatusEffect(StatusEffectCategory.BENEFICIAL, 1950417));
	public static final StatusEffect DOLPHINS_GRACE = register("dolphins_grace", new StatusEffect(StatusEffectCategory.BENEFICIAL, 8954814));
	public static final StatusEffect BAD_OMEN = register("bad_omen", new BadOmenStatusEffect(StatusEffectCategory.NEUTRAL, 745784));
	public static final StatusEffect HERO_OF_THE_VILLAGE = register("hero_of_the_village", new StatusEffect(StatusEffectCategory.BENEFICIAL, 4521796));
	public static final StatusEffect DARKNESS = register(
		"darkness",
		new StatusEffect(StatusEffectCategory.HARMFUL, 2696993).setFactorCalculationDataSupplier(() -> new StatusEffectInstance.FactorCalculationData(22))
	);

	private static StatusEffect register(String id, StatusEffect statusEffect) {
		return Registry.register(Registries.STATUS_EFFECT, id, statusEffect);
	}
}
