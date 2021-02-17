/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.AbstractCountDecorator;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;

public class LavaLakeDecorator
extends AbstractCountDecorator<ChanceDecoratorConfig> {
    public LavaLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getCount(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        if (blockPos.getY() < 63 || random.nextInt(10) == 0) {
            return 1;
        }
        return 0;
    }
}

