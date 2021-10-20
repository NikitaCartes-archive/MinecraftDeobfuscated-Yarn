/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.ScatterDecoratorConfig;

public class ScatterDecorator
extends Decorator<ScatterDecoratorConfig> {
    public ScatterDecorator(Codec<ScatterDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, ScatterDecoratorConfig scatterDecoratorConfig, BlockPos blockPos) {
        int i = blockPos.getX() + scatterDecoratorConfig.xzSpread.get(random);
        int j = blockPos.getY() + scatterDecoratorConfig.ySpread.get(random);
        int k = blockPos.getZ() + scatterDecoratorConfig.xzSpread.get(random);
        BlockPos blockPos2 = new BlockPos(i, j, k);
        ChunkPos chunkPos = new ChunkPos(blockPos2);
        ChunkPos chunkPos2 = new ChunkPos(blockPos);
        int l = MathHelper.abs(chunkPos.x - chunkPos2.x);
        int m = MathHelper.abs(chunkPos.z - chunkPos2.z);
        if (l > 1 || m > 1) {
            return Stream.empty();
        }
        return Stream.of(new BlockPos(i, j, k));
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (ScatterDecoratorConfig)config, pos);
    }
}

