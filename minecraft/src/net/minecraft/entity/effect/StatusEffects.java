package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;

public class StatusEffects {
	public static final StatusEffect SPEED = register(
		1,
		"speed",
		new StatusEffect(StatusEffectType.BENEFICIAL, 8171462)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
			)
	);
	public static final StatusEffect SLOWNESS = register(
		2,
		"slowness",
		new StatusEffect(StatusEffectType.HARMFUL, 5926017)
			.addAttributeModifier(
				EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
			)
	);
	public static final StatusEffect HASTE = register(
		3,
		"haste",
		new StatusEffect(StatusEffectType.BENEFICIAL, 14270531)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
	);
	public static final StatusEffect MINING_FATIGUE = register(
		4,
		"mining_fatigue",
		new StatusEffect(StatusEffectType.HARMFUL, 4866583)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
	);
	public static final StatusEffect STRENGTH = register(
		5,
		"strength",
		new DamageModifierStatusEffect(StatusEffectType.BENEFICIAL, 9643043, 3.0)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect INSTANT_HEALTH = register(6, "instant_health", new InstantStatusEffect(StatusEffectType.BENEFICIAL, 16262179));
	public static final StatusEffect INSTANT_DAMAGE = register(7, "instant_damage", new InstantStatusEffect(StatusEffectType.HARMFUL, 4393481));
	public static final StatusEffect JUMP_BOOST = register(8, "jump_boost", new StatusEffect(StatusEffectType.BENEFICIAL, 2293580));
	public static final StatusEffect NAUSEA = register(9, "nausea", new StatusEffect(StatusEffectType.HARMFUL, 5578058));
	public static final StatusEffect REGENERATION = register(10, "regeneration", new StatusEffect(StatusEffectType.BENEFICIAL, 13458603));
	public static final StatusEffect RESISTANCE = register(11, "resistance", new StatusEffect(StatusEffectType.BENEFICIAL, 10044730));
	public static final StatusEffect FIRE_RESISTANCE = register(12, "fire_resistance", new StatusEffect(StatusEffectType.BENEFICIAL, 14981690));
	public static final StatusEffect WATER_BREATHING = register(13, "water_breathing", new StatusEffect(StatusEffectType.BENEFICIAL, 3035801));
	public static final StatusEffect INVISIBILITY = register(14, "invisibility", new StatusEffect(StatusEffectType.BENEFICIAL, 8356754));
	public static final StatusEffect BLINDNESS = register(15, "blindness", new StatusEffect(StatusEffectType.HARMFUL, 2039587));
	public static final StatusEffect NIGHT_VISION = register(16, "night_vision", new StatusEffect(StatusEffectType.BENEFICIAL, 2039713));
	public static final StatusEffect HUNGER = register(17, "hunger", new StatusEffect(StatusEffectType.HARMFUL, 5797459));
	public static final StatusEffect WEAKNESS = register(
		18,
		"weakness",
		new DamageModifierStatusEffect(StatusEffectType.HARMFUL, 4738376, -4.0)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect POISON = register(19, "poison", new StatusEffect(StatusEffectType.HARMFUL, 5149489));
	public static final StatusEffect WITHER = register(20, "wither", new StatusEffect(StatusEffectType.HARMFUL, 3484199));
	public static final StatusEffect HEALTH_BOOST = register(
		21,
		"health_boost",
		new HealthBoostStatusEffect(StatusEffectType.BENEFICIAL, 16284963)
			.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect ABSORPTION = register(22, "absorption", new AbsorptionStatusEffect(StatusEffectType.BENEFICIAL, 2445989));
	public static final StatusEffect SATURATION = register(23, "saturation", new InstantStatusEffect(StatusEffectType.BENEFICIAL, 16262179));
	public static final StatusEffect GLOWING = register(24, "glowing", new StatusEffect(StatusEffectType.NEUTRAL, 9740385));
	public static final StatusEffect LEVITATION = register(25, "levitation", new StatusEffect(StatusEffectType.HARMFUL, 13565951));
	public static final StatusEffect LUCK = register(
		26,
		"luck",
		new StatusEffect(StatusEffectType.BENEFICIAL, 3381504)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect UNLUCK = register(
		27,
		"unluck",
		new StatusEffect(StatusEffectType.HARMFUL, 12624973)
			.addAttributeModifier(EntityAttributes.GENERIC_LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, EntityAttributeModifier.Operation.ADDITION)
	);
	public static final StatusEffect SLOW_FALLING = register(28, "slow_falling", new StatusEffect(StatusEffectType.BENEFICIAL, 16773073));
	public static final StatusEffect CONDUIT_POWER = register(29, "conduit_power", new StatusEffect(StatusEffectType.BENEFICIAL, 1950417));
	public static final StatusEffect DOLPHINS_GRACE = register(30, "dolphins_grace", new StatusEffect(StatusEffectType.BENEFICIAL, 8954814));
	public static final StatusEffect BAD_OMEN = register(31, "bad_omen", new StatusEffect(StatusEffectType.NEUTRAL, 745784) {
		@Override
		public boolean canApplyUpdateEffect(int duration, int amplifier) {
			return true;
		}

		@Override
		public void applyUpdateEffect(LivingEntity entity, int amplifier) {
			if (entity instanceof ServerPlayerEntity && !entity.isSpectator()) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
				ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
				if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
					return;
				}

				if (serverWorld.isNearOccupiedPointOfInterest(entity.getBlockPos())) {
					serverWorld.getRaidManager().startRaid(serverPlayerEntity);
				}
			}
		}
	});
	public static final StatusEffect HERO_OF_THE_VILLAGE = register(32, "hero_of_the_village", new StatusEffect(StatusEffectType.BENEFICIAL, 4521796));

	private static StatusEffect register(int rawId, String id, StatusEffect entry) {
		return Registry.register(Registry.STATUS_EFFECT, rawId, id, entry);
	}
}
