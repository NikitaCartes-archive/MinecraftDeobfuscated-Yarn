/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public interface Decoratable<R> {
    public R decorate(ConfiguredDecorator<?> var1);

    /**
     * Applies the {@code minecraft:chance} decorators, which only
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
    default public R repeat(UniformIntDistribution count) {
        return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
    }

    /**
     * Applies the {@code minecraft:count} decorator, which repeats
     * the input positions {@code count} times.
     * 
     * @param count the repetition count
     */
    default public R repeat(int count) {
        return this.repeat(UniformIntDistribution.of(count));
    }

    /**
     * Applies the {@code minecraft:count} decorator, which repeats
     * the input positions by a random number between 0 and {@code maxCount}.
     * 
     * @param maxCount the maximum repetition count
     */
    default public R repeatRandomly(int maxCount) {
        return this.repeat(UniformIntDistribution.of(0, maxCount));
    }

    default public R method_30377(int i) {
        return this.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 0, i)));
    }

    /**
     * Applies the {@code minecraft:square} decorator, which spreads positions
     * horizontally a random amount between 0 and 15 blocks on both horizontal axes.
     */
    default public R spreadHorizontally() {
        return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
    }
}

