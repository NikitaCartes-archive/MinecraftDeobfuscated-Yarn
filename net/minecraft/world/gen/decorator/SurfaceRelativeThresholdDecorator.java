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
import net.minecraft.world.gen.decorator.SurfaceRelativeThresholdDecoratorConfig;

public class SurfaceRelativeThresholdDecorator
extends Decorator<SurfaceRelativeThresholdDecoratorConfig> {
    public SurfaceRelativeThresholdDecorator(Codec<SurfaceRelativeThresholdDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, SurfaceRelativeThresholdDecoratorConfig surfaceRelativeThresholdDecoratorConfig, BlockPos blockPos) {
        long l = decoratorContext.getTopY(surfaceRelativeThresholdDecoratorConfig.field_34722, blockPos.getX(), blockPos.getZ());
        long m = l + (long)surfaceRelativeThresholdDecoratorConfig.field_34723;
        long n = l + (long)surfaceRelativeThresholdDecoratorConfig.field_34724;
        if ((long)blockPos.getY() < m || (long)blockPos.getY() > n) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(blockPos);
    }
}

