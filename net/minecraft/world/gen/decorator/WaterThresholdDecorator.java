/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.WaterThresholdDecoratorConfig;

public class WaterThresholdDecorator
extends Decorator<WaterThresholdDecoratorConfig> {
    public WaterThresholdDecorator(Codec<WaterThresholdDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, WaterThresholdDecoratorConfig waterThresholdDecoratorConfig, BlockPos blockPos) {
        int i = decoratorContext.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        int j = decoratorContext.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
        if (j - i > waterThresholdDecoratorConfig.maxWaterDepth) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(blockPos);
    }
}

