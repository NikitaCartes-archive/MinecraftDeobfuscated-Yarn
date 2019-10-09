/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;

public class NoiseHeightmapDoubleDecorator
extends Decorator<NoiseHeightmapDecoratorConfig> {
    public NoiseHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends NoiseHeightmapDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15934(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NoiseHeightmapDecoratorConfig noiseHeightmapDecoratorConfig, BlockPos blockPos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
        int i2 = d < noiseHeightmapDecoratorConfig.noiseLevel ? noiseHeightmapDecoratorConfig.belowNoise : noiseHeightmapDecoratorConfig.aboveNoise;
        return IntStream.range(0, i2).mapToObj(i -> {
            int k;
            int j = random.nextInt(16) + blockPos.getX();
            int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k = random.nextInt(16) + blockPos.getZ()) * 2;
            if (l <= 0) {
                return null;
            }
            return new BlockPos(j, random.nextInt(l), k);
        }).filter(Objects::nonNull);
    }
}

