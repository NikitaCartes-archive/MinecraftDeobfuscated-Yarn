/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConditionalDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class ChanceDecorator
extends ConditionalDecorator<ChanceDecoratorConfig> {
    public ChanceDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext decoratorContext, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        return random.nextFloat() < 1.0f / (float)chanceDecoratorConfig.chance;
    }
}

