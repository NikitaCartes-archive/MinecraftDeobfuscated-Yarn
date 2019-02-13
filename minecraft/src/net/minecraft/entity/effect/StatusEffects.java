package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.registry.Registry;

public class StatusEffects {
	public static final StatusEffect field_5904 = register(
		1,
		"speed",
		new StatusEffect(false, 8171462)
			.setIcon(0, 0)
			.method_5566(EntityAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, EntityAttributeModifier.Operation.field_6331)
			.method_5557()
	);
	public static final StatusEffect field_5909 = register(
		2,
		"slowness",
		new StatusEffect(true, 5926017)
			.setIcon(1, 0)
			.method_5566(EntityAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5917 = register(
		3,
		"haste",
		new StatusEffect(false, 14270531)
			.setIcon(2, 0)
			.method_5571(1.5)
			.method_5557()
			.method_5566(EntityAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5901 = register(
		4,
		"mining_fatigue",
		new StatusEffect(true, 4866583)
			.setIcon(3, 0)
			.method_5566(EntityAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, EntityAttributeModifier.Operation.field_6331)
	);
	public static final StatusEffect field_5910 = register(
		5,
		"strength",
		new DamageModifierStatusEffect(false, 9643043, 3.0)
			.setIcon(4, 0)
			.method_5566(EntityAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, EntityAttributeModifier.Operation.field_6328)
			.method_5557()
	);
	public static final StatusEffect field_5915 = register(6, "instant_health", new InstantStatusEffect(false, 16262179).method_5557());
	public static final StatusEffect field_5921 = register(7, "instant_damage", new InstantStatusEffect(true, 4393481).method_5557());
	public static final StatusEffect field_5913 = register(8, "jump_boost", new StatusEffect(false, 2293580).setIcon(2, 1).method_5557());
	public static final StatusEffect field_5916 = register(9, "nausea", new StatusEffect(true, 5578058).setIcon(3, 1).method_5571(0.25));
	public static final StatusEffect field_5924 = register(10, "regeneration", new StatusEffect(false, 13458603).setIcon(7, 0).method_5571(0.25).method_5557());
	public static final StatusEffect field_5907 = register(11, "resistance", new StatusEffect(false, 10044730).setIcon(6, 1).method_5557());
	public static final StatusEffect field_5918 = register(12, "fire_resistance", new StatusEffect(false, 14981690).setIcon(7, 1).method_5557());
	public static final StatusEffect field_5923 = register(13, "water_breathing", new StatusEffect(false, 3035801).setIcon(0, 2).method_5557());
	public static final StatusEffect field_5905 = register(14, "invisibility", new StatusEffect(false, 8356754).setIcon(0, 1).method_5557());
	public static final StatusEffect field_5919 = register(15, "blindness", new StatusEffect(true, 2039587).setIcon(5, 1).method_5571(0.25));
	public static final StatusEffect field_5925 = register(16, "night_vision", new StatusEffect(false, 2039713).setIcon(4, 1).method_5557());
	public static final StatusEffect field_5903 = register(17, "hunger", new StatusEffect(true, 5797459).setIcon(1, 1));
	public static final StatusEffect field_5911 = register(
		18,
		"weakness",
		new DamageModifierStatusEffect(true, 4738376, -4.0)
			.setIcon(5, 0)
			.method_5566(EntityAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5899 = register(19, "poison", new StatusEffect(true, 5149489).setIcon(6, 0).method_5571(0.25));
	public static final StatusEffect field_5920 = register(20, "wither", new StatusEffect(true, 3484199).setIcon(1, 2).method_5571(0.25));
	public static final StatusEffect field_5914 = register(
		21,
		"health_boost",
		new HealthBoostStatusEffect(false, 16284963)
			.setIcon(7, 2)
			.method_5566(EntityAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, EntityAttributeModifier.Operation.field_6328)
			.method_5557()
	);
	public static final StatusEffect field_5898 = register(22, "absorption", new AbsorptionStatusEffect(false, 2445989).setIcon(2, 2).method_5557());
	public static final StatusEffect field_5922 = register(23, "saturation", new InstantStatusEffect(false, 16262179).method_5557());
	public static final StatusEffect field_5912 = register(24, "glowing", new StatusEffect(false, 9740385).setIcon(4, 2));
	public static final StatusEffect field_5902 = register(25, "levitation", new StatusEffect(true, 13565951).setIcon(3, 2));
	public static final StatusEffect field_5926 = register(
		26,
		"luck",
		new StatusEffect(false, 3381504)
			.setIcon(5, 2)
			.method_5557()
			.method_5566(EntityAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5908 = register(
		27,
		"unluck",
		new StatusEffect(true, 12624973)
			.setIcon(6, 2)
			.method_5566(EntityAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, EntityAttributeModifier.Operation.field_6328)
	);
	public static final StatusEffect field_5906 = register(28, "slow_falling", new StatusEffect(false, 16773073).setIcon(8, 0).method_5557());
	public static final StatusEffect field_5927 = register(29, "conduit_power", new StatusEffect(false, 1950417).setIcon(9, 0).method_5557());
	public static final StatusEffect field_5900 = register(30, "dolphins_grace", new StatusEffect(false, 8954814).setIcon(10, 0).method_5557());
	public static final StatusEffect field_16595 = register(31, "bad_omen", new StatusEffect(false, 745784).setIcon(8, 1));

	private static StatusEffect register(int i, String string, StatusEffect statusEffect) {
		return Registry.register(Registry.STATUS_EFFECT, i, string, statusEffect);
	}
}
