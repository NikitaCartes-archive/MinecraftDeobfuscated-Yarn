package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;

public class StatusEffects {
	public static final StatusEffect field_5904 = register(
		1,
		"speed",
		new StatusEffect(StatusEffectType.field_18271, 8171462)
			.addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5909 = register(
		2,
		"slowness",
		new StatusEffect(StatusEffectType.field_18272, 5926017)
			.addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5917 = register(
		3,
		"haste",
		new StatusEffect(StatusEffectType.field_18271, 14270531)
			.addAttributeModifier(EntityAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5901 = register(
		4,
		"mining_fatigue",
		new StatusEffect(StatusEffectType.field_18272, 4866583)
			.addAttributeModifier(EntityAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5910 = register(
		5,
		"strength",
		new DamageModifierStatusEffect(StatusEffectType.field_18271, 9643043, 3.0)
			.addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5915 = register(6, "instant_health", new InstantStatusEffect(StatusEffectType.field_18271, 16262179));
	public static final StatusEffect field_5921 = register(7, "instant_damage", new InstantStatusEffect(StatusEffectType.field_18272, 4393481));
	public static final StatusEffect field_5913 = register(8, "jump_boost", new StatusEffect(StatusEffectType.field_18271, 2293580));
	public static final StatusEffect field_5916 = register(9, "nausea", new StatusEffect(StatusEffectType.field_18272, 5578058));
	public static final StatusEffect field_5924 = register(10, "regeneration", new StatusEffect(StatusEffectType.field_18271, 13458603));
	public static final StatusEffect field_5907 = register(11, "resistance", new StatusEffect(StatusEffectType.field_18271, 10044730));
	public static final StatusEffect field_5918 = register(12, "fire_resistance", new StatusEffect(StatusEffectType.field_18271, 14981690));
	public static final StatusEffect field_5923 = register(13, "water_breathing", new StatusEffect(StatusEffectType.field_18271, 3035801));
	public static final StatusEffect field_5905 = register(14, "invisibility", new StatusEffect(StatusEffectType.field_18271, 8356754));
	public static final StatusEffect field_5919 = register(15, "blindness", new StatusEffect(StatusEffectType.field_18272, 2039587));
	public static final StatusEffect field_5925 = register(16, "night_vision", new StatusEffect(StatusEffectType.field_18271, 2039713));
	public static final StatusEffect field_5903 = register(17, "hunger", new StatusEffect(StatusEffectType.field_18272, 5797459));
	public static final StatusEffect field_5911 = register(
		18,
		"weakness",
		new DamageModifierStatusEffect(StatusEffectType.field_18272, 4738376, -4.0)
			.addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5899 = register(19, "poison", new StatusEffect(StatusEffectType.field_18272, 5149489));
	public static final StatusEffect field_5920 = register(20, "wither", new StatusEffect(StatusEffectType.field_18272, 3484199));
	public static final StatusEffect field_5914 = register(
		21,
		"health_boost",
		new HealthBoostStatusEffect(StatusEffectType.field_18271, 16284963)
			.addAttributeModifier(EntityAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5898 = register(22, "absorption", new AbsorptionStatusEffect(StatusEffectType.field_18271, 2445989));
	public static final StatusEffect field_5922 = register(23, "saturation", new InstantStatusEffect(StatusEffectType.field_18271, 16262179));
	public static final StatusEffect field_5912 = register(24, "glowing", new StatusEffect(StatusEffectType.field_18273, 9740385));
	public static final StatusEffect field_5902 = register(25, "levitation", new StatusEffect(StatusEffectType.field_18272, 13565951));
	public static final StatusEffect field_5926 = register(
		26,
		"luck",
		new StatusEffect(StatusEffectType.field_18271, 3381504)
			.addAttributeModifier(EntityAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5908 = register(
		27,
		"unluck",
		new StatusEffect(StatusEffectType.field_18272, 12624973)
			.addAttributeModifier(EntityAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5906 = register(28, "slow_falling", new StatusEffect(StatusEffectType.field_18271, 16773073));
	public static final StatusEffect field_5927 = register(29, "conduit_power", new StatusEffect(StatusEffectType.field_18271, 1950417));
	public static final StatusEffect field_5900 = register(30, "dolphins_grace", new StatusEffect(StatusEffectType.field_18271, 8954814));
	public static final StatusEffect field_16595 = register(31, "bad_omen", new StatusEffect(StatusEffectType.field_18273, 745784) {
		@Override
		public boolean canApplyUpdateEffect(int i, int j) {
			return true;
		}

		@Override
		public void applyUpdateEffect(LivingEntity livingEntity, int i) {
			if (livingEntity instanceof ServerPlayerEntity && !livingEntity.isSpectator()) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
				if (serverWorld.getDifficulty() == Difficulty.field_5801) {
					return;
				}

				if (serverWorld.isNearOccupiedPointOfInterest(new BlockPos(livingEntity))) {
					serverWorld.getRaidManager().startRaid(serverPlayerEntity);
				}
			}
		}
	});
	public static final StatusEffect field_18980 = register(32, "hero_of_the_village", new StatusEffect(StatusEffectType.field_18271, 4521796));

	private static StatusEffect register(int i, String string, StatusEffect statusEffect) {
		return Registry.register(Registry.STATUS_EFFECT, i, string, statusEffect);
	}
}
