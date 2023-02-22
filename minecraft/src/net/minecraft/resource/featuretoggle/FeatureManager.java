package net.minecraft.resource.featuretoggle;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class FeatureManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final FeatureUniverse universe;
	private final Map<Identifier, FeatureFlag> featureFlags;
	private final FeatureSet featureSet;

	FeatureManager(FeatureUniverse universe, FeatureSet featureSet, Map<Identifier, FeatureFlag> featureFlags) {
		this.universe = universe;
		this.featureFlags = featureFlags;
		this.featureSet = featureSet;
	}

	public boolean contains(FeatureSet features) {
		return features.isSubsetOf(this.featureSet);
	}

	public FeatureSet getFeatureSet() {
		return this.featureSet;
	}

	public FeatureSet featureSetOf(Iterable<Identifier> features) {
		return this.featureSetOf(features, feature -> LOGGER.warn("Unknown feature flag: {}", feature));
	}

	public FeatureSet featureSetOf(FeatureFlag... features) {
		return FeatureSet.of(this.universe, Arrays.asList(features));
	}

	public FeatureSet featureSetOf(Iterable<Identifier> features, Consumer<Identifier> unknownFlagConsumer) {
		Set<FeatureFlag> set = Sets.newIdentityHashSet();

		for (Identifier identifier : features) {
			FeatureFlag featureFlag = (FeatureFlag)this.featureFlags.get(identifier);
			if (featureFlag == null) {
				unknownFlagConsumer.accept(identifier);
			} else {
				set.add(featureFlag);
			}
		}

		return FeatureSet.of(this.universe, set);
	}

	public Set<Identifier> toId(FeatureSet features) {
		Set<Identifier> set = new HashSet();
		this.featureFlags.forEach((identifier, featureFlag) -> {
			if (features.contains(featureFlag)) {
				set.add(identifier);
			}
		});
		return set;
	}

	public Codec<FeatureSet> getCodec() {
		return Identifier.CODEC.listOf().comapFlatMap(featureIds -> {
			Set<Identifier> set = new HashSet();
			FeatureSet featureSet = this.featureSetOf(featureIds, set::add);
			return !set.isEmpty() ? DataResult.error(() -> "Unknown feature ids: " + set, featureSet) : DataResult.success(featureSet);
		}, features -> List.copyOf(this.toId(features)));
	}

	public static class Builder {
		private final FeatureUniverse universe;
		private int id;
		private final Map<Identifier, FeatureFlag> featureFlags = new LinkedHashMap();

		public Builder(String universe) {
			this.universe = new FeatureUniverse(universe);
		}

		public FeatureFlag addVanillaFlag(String feature) {
			return this.addFlag(new Identifier("minecraft", feature));
		}

		public FeatureFlag addFlag(Identifier feature) {
			if (this.id >= 64) {
				throw new IllegalStateException("Too many feature flags");
			} else {
				FeatureFlag featureFlag = new FeatureFlag(this.universe, this.id++);
				FeatureFlag featureFlag2 = (FeatureFlag)this.featureFlags.put(feature, featureFlag);
				if (featureFlag2 != null) {
					throw new IllegalStateException("Duplicate feature flag " + feature);
				} else {
					return featureFlag;
				}
			}
		}

		public FeatureManager build() {
			FeatureSet featureSet = FeatureSet.of(this.universe, this.featureFlags.values());
			return new FeatureManager(this.universe, featureSet, Map.copyOf(this.featureFlags));
		}
	}
}
