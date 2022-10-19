package net.minecraft.resource.featuretoggle;

public class FeatureFlag {
	final FeatureUniverse universe;
	final long mask;

	FeatureFlag(FeatureUniverse universe, int id) {
		this.universe = universe;
		this.mask = 1L << id;
	}
}
