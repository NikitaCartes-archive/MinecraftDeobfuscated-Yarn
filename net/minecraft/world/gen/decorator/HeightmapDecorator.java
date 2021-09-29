/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;

public class HeightmapDecorator
extends Decorator<HeightmapDecoratorConfig> {
    public HeightmapDecorator(Codec<HeightmapDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, HeightmapDecoratorConfig heightmapDecoratorConfig, BlockPos blockPos) {
        int j;
        int i = blockPos.getX();
        int k = decoratorContext.getTopY(heightmapDecoratorConfig.heightmap, i, j = blockPos.getZ());
        if (k > decoratorContext.getBottomY()) {
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.of(new BlockPos[0]);
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (HeightmapDecoratorConfig)config, pos);
    }
}

