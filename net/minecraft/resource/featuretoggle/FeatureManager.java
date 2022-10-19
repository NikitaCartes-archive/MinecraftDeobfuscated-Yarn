/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.FeatureUniverse;
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
        return this.featureSetOf(features, (Identifier feature) -> LOGGER.warn("Unknown feature flag: {}", feature));
    }

    public FeatureSet featureSetOf(FeatureFlag ... features) {
        return FeatureSet.of(this.universe, Arrays.asList(features));
    }

    public FeatureSet featureSetOf(Iterable<Identifier> features, Consumer<Identifier> unknownFlagConsumer) {
        Set<FeatureFlag> set = Sets.newIdentityHashSet();
        for (Identifier identifier : features) {
            FeatureFlag featureFlag = this.featureFlags.get(identifier);
            if (featureFlag == null) {
                unknownFlagConsumer.accept(identifier);
                continue;
            }
            set.add(featureFlag);
        }
        return FeatureSet.of(this.universe, set);
    }

    public Set<Identifier> toId(FeatureSet features) {
        HashSet<Identifier> set = new HashSet<Identifier>();
        this.featureFlags.forEach((identifier, featureFlag) -> {
            if (features.contains((FeatureFlag)featureFlag)) {
                set.add((Identifier)identifier);
            }
        });
        return set;
    }

    public Codec<FeatureSet> getCodec() {
        return Identifier.CODEC.listOf().comapFlatMap(featureIds -> {
            HashSet set = new HashSet();
            FeatureSet featureSet = this.featureSetOf((Iterable<Identifier>)featureIds, set::add);
            if (!set.isEmpty()) {
                return DataResult.error("Unknown feature ids: " + set, featureSet);
            }
            return DataResult.success(featureSet);
        }, features -> List.copyOf(this.toId((FeatureSet)features)));
    }

    public static class Builder {
        private final FeatureUniverse universe;
        private int id;
        private final Map<Identifier, FeatureFlag> featureFlags = new LinkedHashMap<Identifier, FeatureFlag>();

        public Builder(String universe) {
            this.universe = new FeatureUniverse(universe);
        }

        public FeatureFlag addVanillaFlag(String feature) {
            return this.addFlag(new Identifier("minecraft", feature));
        }

        public FeatureFlag addFlag(Identifier feature) {
            FeatureFlag featureFlag;
            FeatureFlag featureFlag2;
            if (this.id >= 64) {
                throw new IllegalStateException("Too many feature flags");
            }
            if ((featureFlag2 = this.featureFlags.put(feature, featureFlag = new FeatureFlag(this.universe, this.id++))) != null) {
                throw new IllegalStateException("Duplicate feature flag " + feature);
            }
            return featureFlag;
        }

        public FeatureManager build() {
            FeatureSet featureSet = FeatureSet.of(this.universe, this.featureFlags.values());
            return new FeatureManager(this.universe, featureSet, Map.copyOf(this.featureFlags));
        }
    }
}

