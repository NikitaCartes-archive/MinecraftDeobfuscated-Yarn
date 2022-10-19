package net.minecraft.resource.featuretoggle;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;

public final class FeatureSet {
	private static final FeatureSet EMPTY = new FeatureSet(null, 0L);
	public static final int MAX_FEATURE_FLAGS = 64;
	@Nullable
	private final FeatureUniverse universe;
	private final long featuresMask;

	private FeatureSet(@Nullable FeatureUniverse universe, long featuresMask) {
		this.universe = universe;
		this.featuresMask = featuresMask;
	}

	static FeatureSet of(FeatureUniverse universe, Collection<FeatureFlag> features) {
		if (features.isEmpty()) {
			return EMPTY;
		} else {
			long l = combineMask(universe, 0L, features);
			return new FeatureSet(universe, l);
		}
	}

	public static FeatureSet empty() {
		return EMPTY;
	}

	public static FeatureSet of(FeatureFlag feature) {
		return new FeatureSet(feature.universe, feature.mask);
	}

	public static FeatureSet of(FeatureFlag feature1, FeatureFlag... features) {
		long l = features.length == 0 ? feature1.mask : combineMask(feature1.universe, feature1.mask, Arrays.asList(features));
		return new FeatureSet(feature1.universe, l);
	}

	private static long combineMask(FeatureUniverse universe, long featuresMask, Iterable<FeatureFlag> newFeatures) {
		for (FeatureFlag featureFlag : newFeatures) {
			if (universe != featureFlag.universe) {
				throw new IllegalStateException("Mismatched feature universe, expected '" + universe + "', but got '" + featureFlag.universe + "'");
			}

			featuresMask |= featureFlag.mask;
		}

		return featuresMask;
	}

	public boolean contains(FeatureFlag feature) {
		return this.universe != feature.universe ? false : (this.featuresMask & feature.mask) != 0L;
	}

	public boolean isSubsetOf(FeatureSet features) {
		if (this.universe == null) {
			return true;
		} else {
			return this.universe != features.universe ? false : (this.featuresMask & ~features.featuresMask) == 0L;
		}
	}

	public FeatureSet combine(FeatureSet features) {
		if (this.universe == null) {
			return features;
		} else if (features.universe == null) {
			return this;
		} else if (this.universe != features.universe) {
			throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + features.universe + "'");
		} else {
			return new FeatureSet(this.universe, this.featuresMask | features.featuresMask);
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof FeatureSet featureSet && this.universe == featureSet.universe && this.featuresMask == featureSet.featuresMask) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return (int)HashCommon.mix(this.featuresMask);
	}
}
