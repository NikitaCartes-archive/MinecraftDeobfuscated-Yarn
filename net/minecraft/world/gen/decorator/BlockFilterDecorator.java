/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.BlockFilterDecoratorConfig;
import net.minecraft.world.gen.decorator.ConditionalDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class BlockFilterDecorator
extends ConditionalDecorator<BlockFilterDecoratorConfig> {
    public BlockFilterDecorator(Codec<BlockFilterDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, BlockFilterDecoratorConfig blockFilterDecoratorConfig, BlockPos blockPos) {
        return blockFilterDecoratorConfig.getPredicate().test(decoratorContext.getWorld(), blockPos);
    }
}

