/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.BlockSurvivesFilterDecoratorConfig;
import net.minecraft.world.gen.decorator.ConditionalDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class BlockSurvivesFilterDecorator
extends ConditionalDecorator<BlockSurvivesFilterDecoratorConfig> {
    public BlockSurvivesFilterDecorator(Codec<BlockSurvivesFilterDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, BlockSurvivesFilterDecoratorConfig blockSurvivesFilterDecoratorConfig, BlockPos blockPos) {
        return blockSurvivesFilterDecoratorConfig.state().canPlaceAt(decoratorContext.getWorld(), blockPos);
    }
}

