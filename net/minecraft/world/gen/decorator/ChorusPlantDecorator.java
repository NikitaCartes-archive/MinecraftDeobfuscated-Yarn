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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class ChorusPlantDecorator
extends Decorator<NopeDecoratorConfig> {
    public ChorusPlantDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_14373(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        int i2 = random.nextInt(5);
        return IntStream.range(0, i2).mapToObj(i -> {
            int k;
            int j = random.nextInt(16) + blockPos.getX();
            int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k = random.nextInt(16) + blockPos.getZ());
            if (l > 0) {
                int m = l - 1;
                return new BlockPos(j, m, k);
            }
            return null;
        }).filter(Objects::nonNull);
    }
}

