/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.class_5432;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class ConfiguredDecorator<DC extends DecoratorConfig>
implements class_5432<ConfiguredDecorator<?>> {
    public static final Codec<ConfiguredDecorator<?>> CODEC = Registry.DECORATOR.dispatch("name", configuredDecorator -> configuredDecorator.decorator, Decorator::getCodec);
    private final Decorator<DC> decorator;
    private final DC config;

    public ConfiguredDecorator(Decorator<DC> decorator, DC config) {
        this.decorator = decorator;
        this.config = config;
    }

    public Stream<BlockPos> method_30444(DecoratorContext decoratorContext, Random random, BlockPos blockPos) {
        return this.decorator.getPositions(decoratorContext, random, this.config, blockPos);
    }

    public String toString() {
        return String.format("[%s %s]", Registry.DECORATOR.getId(this.decorator), this.config);
    }

    @Override
    public ConfiguredDecorator<?> method_30374(ConfiguredDecorator<?> configuredDecorator) {
        return new ConfiguredDecorator<DecoratedDecoratorConfig>(Decorator.DECORATED, new DecoratedDecoratorConfig(configuredDecorator, this));
    }

    public DC getConfig() {
        return this.config;
    }

    @Override
    public /* synthetic */ Object method_30374(ConfiguredDecorator configuredDecorator) {
        return this.method_30374(configuredDecorator);
    }
}

