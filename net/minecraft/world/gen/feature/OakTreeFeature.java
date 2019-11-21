/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
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
    public boolean generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
        int k;
        int j;
        int i = branchedTreeFeatureConfig.baseHeight + random.nextInt(branchedTreeFeatureConfig.heightRandA + 1) + random.nextInt(branchedTreeFeatureConfig.heightRandB + 1);
        Optional<BlockPos> optional = this.findPositionToGenerate(modifiableTestableWorld, i, j = branchedTreeFeatureConfig.trunkHeight >= 0 ? branchedTreeFeatureConfig.trunkHeight + random.nextInt(branchedTreeFeatureConfig.trunkHeightRandom + 1) : i - (branchedTreeFeatureConfig.foliageHeight + random.nextInt(branchedTreeFeatureConfig.foliageHeightRandom + 1)), k = branchedTreeFeatureConfig.foliagePlacer.method_23452(random, j, i, branchedTreeFeatureConfig), blockPos, branchedTreeFeatureConfig);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos2 = optional.get();
        this.setToDirt(modifiableTestableWorld, blockPos2.down());
        branchedTreeFeatureConfig.foliagePlacer.method_23448(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, j, k, blockPos2, set2);
        this.generate(modifiableTestableWorld, random, i, blockPos2, branchedTreeFeatureConfig.trunkTopOffset + random.nextInt(branchedTreeFeatureConfig.trunkTopOffsetRandom + 1), set, blockBox, branchedTreeFeatureConfig);
        return true;
    }
}

