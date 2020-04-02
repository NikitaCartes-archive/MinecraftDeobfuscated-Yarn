/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class OakTreeFeature
extends BranchedTreeFeature<BranchedTreeFeatureConfig> {
    public OakTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos2, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
        int j;
        int k;
        int l;
        int i = branchedTreeFeatureConfig.trunkPlacer.getHeight(random, branchedTreeFeatureConfig);
        Optional<BlockPos> optional = this.findPositionToGenerate(modifiableTestableWorld, i, l = branchedTreeFeatureConfig.foliagePlacer.getRadius(random, k = i - (j = branchedTreeFeatureConfig.foliagePlacer.getHeight(random, i)), branchedTreeFeatureConfig), blockPos2, branchedTreeFeatureConfig);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos22 = optional.get();
        this.setToDirt(modifiableTestableWorld, blockPos22.down());
        Map<BlockPos, Integer> map = branchedTreeFeatureConfig.trunkPlacer.generate(modifiableTestableWorld, random, i, blockPos22, l, set, blockBox, branchedTreeFeatureConfig);
        map.forEach((blockPos, integer) -> branchedTreeFeatureConfig.foliagePlacer.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, (BlockPos)blockPos, j, (int)integer, set2));
        return true;
    }
}

