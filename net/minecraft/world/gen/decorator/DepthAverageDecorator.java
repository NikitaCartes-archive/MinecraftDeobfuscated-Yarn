/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;

public class DepthAverageDecorator
extends AbstractRangeDecorator<DepthAverageDecoratorConfig> {
    public DepthAverageDecorator(Codec<DepthAverageDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, DepthAverageDecoratorConfig depthAverageDecoratorConfig, int i) {
        int j = depthAverageDecoratorConfig.getSpread();
        return random.nextInt(j) + random.nextInt(j) - j + depthAverageDecoratorConfig.getBaseline().getY(decoratorContext);
    }
}

