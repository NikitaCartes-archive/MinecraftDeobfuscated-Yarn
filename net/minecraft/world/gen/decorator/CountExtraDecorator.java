/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.AbstractCountDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;

public class CountExtraDecorator
extends AbstractCountDecorator<CountExtraDecoratorConfig> {
    public CountExtraDecorator(Codec<CountExtraDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getCount(Random random, CountExtraDecoratorConfig countExtraDecoratorConfig, BlockPos blockPos) {
        return countExtraDecoratorConfig.count + (random.nextFloat() < countExtraDecoratorConfig.extraChance ? countExtraDecoratorConfig.extraCount : 0);
    }
}

