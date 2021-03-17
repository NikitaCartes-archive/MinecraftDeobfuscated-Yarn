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
import net.minecraft.world.gen.decorator.WaterDepthThresholdDecoratorConfig;

public class WaterDepthThresholdDecorator
extends Decorator<WaterDepthThresholdDecoratorConfig> {
    public WaterDepthThresholdDecorator(Codec<WaterDepthThresholdDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, WaterDepthThresholdDecoratorConfig waterDepthThresholdDecoratorConfig, BlockPos blockPos) {
        int i = decoratorContext.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        int j = decoratorContext.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
        if (j - i > waterDepthThresholdDecoratorConfig.maxWaterDepth) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(blockPos);
    }
}

