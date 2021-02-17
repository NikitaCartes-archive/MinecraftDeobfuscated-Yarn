/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.BiasedRangedDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractBiasedRangeDecorator
extends AbstractRangeDecorator<BiasedRangedDecoratorConfig> {
    private static final Logger LOGGER = LogManager.getLogger();

    public AbstractBiasedRangeDecorator(Codec<BiasedRangedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, BiasedRangedDecoratorConfig biasedRangedDecoratorConfig, int i) {
        int k;
        int j = biasedRangedDecoratorConfig.getBottom().getY(decoratorContext);
        if (j >= (k = biasedRangedDecoratorConfig.getTop().getY(decoratorContext))) {
            LOGGER.warn("Empty range decorator: {} [{}-{}]", (Object)this, (Object)j, (Object)k);
            return j;
        }
        return this.getY(random, j, k, biasedRangedDecoratorConfig.getCutoff());
    }

    protected abstract int getY(Random var1, int var2, int var3, int var4);
}

