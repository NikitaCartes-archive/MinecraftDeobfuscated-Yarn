package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class DamageModifierStatusEffect extends StatusEffect {
	protected final double modifier;

	protected DamageModifierStatusEffect(StatusEffectType statusEffectType, int color, double d) {
		super(statusEffectType, color);
		this.modifier = d;
	}

	@Override
	public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
		return this.modifier * (double)(amplifier + 1);
	}
}
