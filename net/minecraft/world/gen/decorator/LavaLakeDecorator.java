/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class LavaLakeDecorator
extends Decorator<ChanceDecoratorConfig> {
    public LavaLakeDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        if (random.nextInt(chanceDecoratorConfig.chance / 10) == 0) {
            int i = random.nextInt(16) + blockPos.getX();
            int j = random.nextInt(16) + blockPos.getZ();
            int k = random.nextInt(random.nextInt(decoratorContext.getMaxY() - 8) + 8);
            if (k < decoratorContext.getSeaLevel() || random.nextInt(chanceDecoratorConfig.chance / 8) == 0) {
                return Stream.of(new BlockPos(i, k, j));
            }
        }
        return Stream.empty();
    }
}

