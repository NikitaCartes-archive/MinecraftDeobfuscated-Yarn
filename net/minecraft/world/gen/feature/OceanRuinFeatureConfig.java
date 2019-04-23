/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;

public class OceanRuinFeatureConfig
implements FeatureConfig {
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType biomeType, float f, float g) {
        this.biomeType = biomeType;
        this.largeProbability = f;
        this.clusterProbability = g;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("biome_temp"), dynamicOps.createString(this.biomeType.getName()), dynamicOps.createString("large_probability"), dynamicOps.createFloat(this.largeProbability), dynamicOps.createString("cluster_probability"), dynamicOps.createFloat(this.clusterProbability))));
    }

    public static <T> OceanRuinFeatureConfig deserialize(Dynamic<T> dynamic) {
        OceanRuinFeature.BiomeType biomeType = OceanRuinFeature.BiomeType.byName(dynamic.get("biome_temp").asString(""));
        float f = dynamic.get("large_probability").asFloat(0.0f);
        float g = dynamic.get("cluster_probability").asFloat(0.0f);
        return new OceanRuinFeatureConfig(biomeType, f, g);
    }
}

