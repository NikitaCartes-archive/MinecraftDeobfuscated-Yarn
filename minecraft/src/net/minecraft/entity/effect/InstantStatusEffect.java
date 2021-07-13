package net.minecraft.entity.effect;

public class InstantStatusEffect extends StatusEffect {
	public InstantStatusEffect(StatusEffectType statusEffectType, int i) {
		super(statusEffectType, i);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return duration >= 1;
	}
}
