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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DungeonDecoratorConfig;

public class DungeonsDecorator
extends Decorator<DungeonDecoratorConfig> {
    public DungeonsDecorator(Function<Dynamic<?>, ? extends DungeonDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15933(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, DungeonDecoratorConfig dungeonDecoratorConfig, BlockPos blockPos) {
        int i2 = dungeonDecoratorConfig.chance;
        return IntStream.range(0, i2).mapToObj(i -> {
            int j = random.nextInt(16);
            int k = random.nextInt(chunkGenerator.getMaxY());
            int l = random.nextInt(16);
            return blockPos.add(j, k, l);
        });
    }
}

