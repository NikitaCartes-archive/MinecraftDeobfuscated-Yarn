/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChancePassthroughDecorator
extends SimpleDecorator<LakeDecoratorConfig> {
    public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_14347(Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)lakeDecoratorConfig.chance) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }
}

