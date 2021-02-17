/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.gen.decorator.AbstractRangeDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class EndGatewayDecorator
extends AbstractRangeDecorator<NopeDecoratorConfig> {
    public EndGatewayDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected int getY(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, int i) {
        return i + 3 + random.nextInt(7);
    }
}

