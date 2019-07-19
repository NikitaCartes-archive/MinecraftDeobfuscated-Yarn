/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DoublePlantFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DoublePlantFeature
extends Feature<DoublePlantFeatureConfig> {
    public DoublePlantFeature(Function<Dynamic<?>, ? extends DoublePlantFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DoublePlantFeatureConfig doublePlantFeatureConfig) {
        boolean bl = false;
        for (int i = 0; i < 64; ++i) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (!iWorld.isAir(blockPos2) || blockPos2.getY() >= 254 || !doublePlantFeatureConfig.state.canPlaceAt(iWorld, blockPos2)) continue;
            ((TallPlantBlock)doublePlantFeatureConfig.state.getBlock()).placeAt(iWorld, blockPos2, 2);
            bl = true;
        }
        return bl;
    }
}

