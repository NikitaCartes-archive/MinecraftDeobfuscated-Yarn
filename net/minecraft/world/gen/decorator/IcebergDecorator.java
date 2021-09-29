/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class IcebergDecorator
extends Decorator<NopeDecoratorConfig> {
    public IcebergDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        int i = random.nextInt(8) + 4 + blockPos.getX();
        int j = random.nextInt(8) + 4 + blockPos.getZ();
        return Stream.of(new BlockPos(i, blockPos.getY(), j));
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (NopeDecoratorConfig)config, pos);
    }
}

