/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class Spread32AboveDecorator
extends AbstractRangeDecorator<NopeDecoratorConfig> {
    public Spread32AboveDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, int i) {
        return random.nextInt(Math.max(i, 0) + 32);
    }
}

