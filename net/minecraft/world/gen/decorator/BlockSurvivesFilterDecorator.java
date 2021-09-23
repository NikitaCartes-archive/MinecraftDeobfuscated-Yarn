/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.BlockSurvivesFilterDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class BlockSurvivesFilterDecorator
extends Decorator<BlockSurvivesFilterDecoratorConfig> {
    public BlockSurvivesFilterDecorator(Codec<BlockSurvivesFilterDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, BlockSurvivesFilterDecoratorConfig blockSurvivesFilterDecoratorConfig, BlockPos blockPos) {
        if (!blockSurvivesFilterDecoratorConfig.state().canPlaceAt(decoratorContext.getWorld(), blockPos)) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(blockPos);
    }
}

