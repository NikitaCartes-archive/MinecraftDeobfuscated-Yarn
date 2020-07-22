/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decoratable;
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class ConfiguredDecorator<DC extends DecoratorConfig>
implements Decoratable<ConfiguredDecorator<?>> {
    public static final Codec<ConfiguredDecorator<?>> CODEC = Registry.DECORATOR.dispatch("type", configuredDecorator -> configuredDecorator.decorator, Decorator::getCodec);
    private final Decorator<DC> decorator;
    private final DC config;

    public ConfiguredDecorator(Decorator<DC> decorator, DC config) {
        this.decorator = decorator;
        this.config = config;
    }

    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
        return this.decorator.getPositions(context, random, this.config, pos);
    }

    public String toString() {
        return String.format("[%s %s]", Registry.DECORATOR.getId(this.decorator), this.config);
    }

    @Override
    public ConfiguredDecorator<?> decorate(ConfiguredDecorator<?> configuredDecorator) {
        return new ConfiguredDecorator<DecoratedDecoratorConfig>(Decorator.DECORATED, new DecoratedDecoratorConfig(configuredDecorator, this));
    }

    public DC getConfig() {
        return this.config;
    }

    @Override
    public /* synthetic */ Object decorate(ConfiguredDecorator decorator) {
        return this.decorate(decorator);
    }
}

