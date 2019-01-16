package net.minecraft.entity.effect;

public class InstantStatusEffect extends StatusEffect {
	public InstantStatusEffect(boolean bl, int i) {
		super(bl, i);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean canApplyUpdateEffect(int i, int j) {
		return i >= 1;
	}
}
