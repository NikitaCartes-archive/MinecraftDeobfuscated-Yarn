/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ConditionalDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.SurfaceRelativeThresholdDecoratorConfig;

public class SurfaceRelativeThresholdDecorator
extends ConditionalDecorator<SurfaceRelativeThresholdDecoratorConfig> {
    public SurfaceRelativeThresholdDecorator(Codec<SurfaceRelativeThresholdDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, SurfaceRelativeThresholdDecoratorConfig surfaceRelativeThresholdDecoratorConfig, BlockPos blockPos) {
        long l = decoratorContext.getTopY(surfaceRelativeThresholdDecoratorConfig.heightmap, blockPos.getX(), blockPos.getZ());
        long m = l + (long)surfaceRelativeThresholdDecoratorConfig.min;
        long n = l + (long)surfaceRelativeThresholdDecoratorConfig.max;
        return m <= (long)blockPos.getY() && (long)blockPos.getY() <= n;
    }
}

