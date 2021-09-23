/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.BlockFilterDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class BlockFilterDecorator
extends Decorator<BlockFilterDecoratorConfig> {
    public BlockFilterDecorator(Codec<BlockFilterDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, BlockFilterDecoratorConfig blockFilterDecoratorConfig, BlockPos blockPos) {
        BlockState blockState = decoratorContext.getWorld().getBlockState(blockPos.add(blockFilterDecoratorConfig.offset()));
        for (Block block : blockFilterDecoratorConfig.disallowed()) {
            if (!blockState.isOf(block)) continue;
            return Stream.of(new BlockPos[0]);
        }
        for (Block block : blockFilterDecoratorConfig.allowed()) {
            if (!blockState.isOf(block)) continue;
            return Stream.of(blockPos);
        }
        if (blockFilterDecoratorConfig.allowed().isEmpty()) {
            return Stream.of(blockPos);
        }
        return Stream.of(new BlockPos[0]);
    }
}

