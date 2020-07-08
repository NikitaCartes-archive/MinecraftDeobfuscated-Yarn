/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.AbstractHeightmapDecorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class DarkOakTreeDecorator
extends AbstractHeightmapDecorator<NopeDecoratorConfig> {
    public DarkOakTreeDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type getHeightmapType(NopeDecoratorConfig nopeDecoratorConfig) {
        return Heightmap.Type.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, 16).mapToObj(i -> {
            int j = i / 4;
            int k = i % 4;
            int l = j * 4 + 1 + random.nextInt(3) + blockPos.getX();
            int m = k * 4 + 1 + random.nextInt(3) + blockPos.getZ();
            int n = decoratorContext.getTopY(this.getHeightmapType(nopeDecoratorConfig), l, m);
            return new BlockPos(l, n, m);
        });
    }
}

