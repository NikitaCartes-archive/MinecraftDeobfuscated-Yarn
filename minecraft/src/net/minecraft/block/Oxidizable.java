package net.minecraft.block;

public interface Oxidizable extends Degradable<Oxidizable.OxidizationLevel> {
	@Override
	default float getDegradationChanceMultiplier() {
		return this.getDegradationLevel() == Oxidizable.OxidizationLevel.UNAFFECTED ? 0.75F : 1.0F;
	}

	public static enum OxidizationLevel {
		UNAFFECTED,
		EXPOSED,
		WEATHERED,
		OXIDIZED;
	}
}
