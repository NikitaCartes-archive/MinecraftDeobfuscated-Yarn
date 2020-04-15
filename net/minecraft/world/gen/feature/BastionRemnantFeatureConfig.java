/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantFeatureConfig
implements FeatureConfig {
    private final List<StructurePoolFeatureConfig> possibleConfigs;

    public BastionRemnantFeatureConfig(Map<String, Integer> startPoolToSize) {
        this.possibleConfigs = startPoolToSize.entrySet().stream().map(entry -> new StructurePoolFeatureConfig((String)entry.getKey(), (Integer)entry.getValue())).collect(Collectors.toList());
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<Object>(ops, ops.createList(this.possibleConfigs.stream().map(structurePoolFeatureConfig -> structurePoolFeatureConfig.serialize(ops).getValue())));
    }

    public static <T> BastionRemnantFeatureConfig deserialize(Dynamic<T> dynamic) {
        List<StructurePoolFeatureConfig> list = dynamic.asList(StructurePoolFeatureConfig::deserialize);
        return new BastionRemnantFeatureConfig(list.stream().collect(Collectors.toMap(StructurePoolFeatureConfig::getStartPool, StructurePoolFeatureConfig::getSize)));
    }

    public StructurePoolFeatureConfig getRandom(Random random) {
        return this.possibleConfigs.get(random.nextInt(this.possibleConfigs.size()));
    }
}

