/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.ConditionalDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.WaterDepthThresholdDecoratorConfig;

public class WaterDepthThresholdDecorator
extends ConditionalDecorator<WaterDepthThresholdDecoratorConfig> {
    public WaterDepthThresholdDecorator(Codec<WaterDepthThresholdDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, WaterDepthThresholdDecoratorConfig waterDepthThresholdDecoratorConfig, BlockPos blockPos) {
        int i = decoratorContext.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        int j = decoratorContext.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
        return j - i <= waterDepthThresholdDecoratorConfig.maxWaterDepth;
    }
}

