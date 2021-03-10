/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;

public class SpreadDoubleHeightmapDecorator
extends Decorator<HeightmapDecoratorConfig> {
    public SpreadDoubleHeightmapDecorator(Codec<HeightmapDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, HeightmapDecoratorConfig heightmapDecoratorConfig, BlockPos blockPos) {
        int j;
        int i = blockPos.getX();
        int k = decoratorContext.getTopY(heightmapDecoratorConfig.heightmap, i, j = blockPos.getZ());
        if (k == decoratorContext.getBottomY()) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(new BlockPos(i, decoratorContext.getBottomY() + random.nextInt((k - decoratorContext.getBottomY()) * 2), j));
    }
}

