/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class CountExtraHeightmapDecorator
extends Decorator<CountExtraChanceDecoratorConfig> {
    public CountExtraHeightmapDecorator(Codec<CountExtraChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, CountExtraChanceDecoratorConfig countExtraChanceDecoratorConfig, BlockPos blockPos) {
        int i2 = countExtraChanceDecoratorConfig.count;
        if (random.nextFloat() < countExtraChanceDecoratorConfig.extraChance) {
            i2 += countExtraChanceDecoratorConfig.extraCount;
        }
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16) + blockPos.getX();
            int k = random.nextInt(16) + blockPos.getZ();
            int l = worldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k);
            return new BlockPos(j, l, k);
        });
    }
}

