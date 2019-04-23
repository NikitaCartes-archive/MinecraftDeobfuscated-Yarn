/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

public class IcebergDecorator
extends Decorator<ChanceDecoratorConfig> {
    public IcebergDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15929(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)chanceDecoratorConfig.chance) {
            int i = random.nextInt(8) + 4;
            int j = random.nextInt(8) + 4;
            return Stream.of(iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(i, 0, j)));
        }
        return Stream.empty();
    }
}

