/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.AbstractHeightmapDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public abstract class HeightmapDecorator<DC extends DecoratorConfig>
extends AbstractHeightmapDecorator<DC> {
    public HeightmapDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getZ();
        int k = context.getTopY(this.getHeightmapType(config), i, j);
        if (k > 0) {
            return Stream.of(new BlockPos(i, k, j));
        }
        return Stream.of(new BlockPos[0]);
    }
}

