/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseRangeDecorator
extends AbstractRangeDecorator<RangeDecoratorConfig> {
    private static final Logger LOGGER = LogManager.getLogger();

    public BaseRangeDecorator(Codec<RangeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, RangeDecoratorConfig rangeDecoratorConfig, int i) {
        int k;
        int j = rangeDecoratorConfig.getBottom().getY(decoratorContext);
        if (j >= (k = rangeDecoratorConfig.getTop().getY(decoratorContext))) {
            LOGGER.warn("Empty range decorator: {} [{}-{}]", (Object)this, (Object)j, (Object)k);
            return j;
        }
        return this.getY(random, j, k);
    }

    protected abstract int getY(Random var1, int var2, int var3);
}

