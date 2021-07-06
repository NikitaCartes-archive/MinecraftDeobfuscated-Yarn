package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class DamageModifierStatusEffect extends StatusEffect {
	protected final double modifier;

	protected DamageModifierStatusEffect(StatusEffectCategory category, int color, double modifier) {
		super(category, color);
		this.modifier = modifier;
	}

	@Override
	public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
		return this.modifier * (double)(amplifier + 1);
	}
}
