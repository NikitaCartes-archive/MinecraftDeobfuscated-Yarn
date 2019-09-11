/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;

public class HeightmapNoiseBiasedDecorator
extends Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
    public HeightmapNoiseBiasedDecorator(Function<Dynamic<?>, ? extends TopSolidHeightmapNoiseBiasedDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15943(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, TopSolidHeightmapNoiseBiasedDecoratorConfig topSolidHeightmapNoiseBiasedDecoratorConfig, BlockPos blockPos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor, (double)blockPos.getZ() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor, false);
        int i2 = (int)Math.ceil((d + topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset) * (double)topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio);
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16);
            int k = random.nextInt(16);
            int l = iWorld.getTopY(topSolidHeightmapNoiseBiasedDecoratorConfig.heightmap, blockPos.getX() + j, blockPos.getZ() + k);
            return new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
        });
    }
}

