/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public interface Decoratable<R> {
    public R decorate(ConfiguredDecorator<?> var1);

    /**
     * Applies the {@code minecraft:chance} decorator, which only
     * allows positions with a {@code 1 / count} chance,
     * e.g. a count of 2 would give approximately half of the input positions.
     */
    default public R applyChance(int chance) {
        return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
    }

    /**
     * Applies the {@code minecraft:count} decorator, which repeats
     * the input positions by the value of the {@code count} distribution.
     * 
     * @param count the distribution of the repetition count
     */
    default public R repeat(IntProvider count) {
        return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
    }

    /**
     * Applies the {@code minecraft:count} decorator, which repeats
     * the input positions {@code count} times.
     * 
     * @param count the repetition count
     */
    default public R repeat(int count) {
        return this.repeat(ConstantIntProvider.create(count));
    }

    /**
     * Applies the {@code minecraft:count} decorator, which repeats
     * the input positions by a random number between 0 and {@code maxCount}.
     * 
     * @param maxCount the maximum repetition count
     */
    default public R repeatRandomly(int maxCount) {
        return this.repeat(UniformIntProvider.create(0, maxCount));
    }

    /**
     * @param min the minimum offset, inclusive
     * @param max the maximum offset, inclusive
     */
    default public R uniformRange(YOffset min, YOffset max) {
        return this.range(new RangeDecoratorConfig(UniformHeightProvider.create(min, max)));
    }

    /**
     * @param min the minimum offset, inclusive
     * @param max the maximum offset, inclusive
     */
    default public R triangleRange(YOffset min, YOffset max) {
        return this.range(new RangeDecoratorConfig(TrapezoidHeightProvider.create(min, max)));
    }

    default public R range(RangeDecoratorConfig config) {
        return this.decorate(Decorator.RANGE.configure(config));
    }

    /**
     * Applies the {@code minecraft:square} decorator, which spreads positions
     * horizontally a random amount between 0 and 15 blocks on both horizontal axes.
     */
    default public R spreadHorizontally() {
        return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
    }
}

