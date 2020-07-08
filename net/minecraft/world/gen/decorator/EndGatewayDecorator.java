/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class EndGatewayDecorator
extends Decorator<NopeDecoratorConfig> {
    public EndGatewayDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        int j;
        int i;
        int k;
        if (random.nextInt(700) == 0 && (k = decoratorContext.getTopY(Heightmap.Type.MOTION_BLOCKING, i = random.nextInt(16) + blockPos.getX(), j = random.nextInt(16) + blockPos.getZ())) > 0) {
            int l = k + 3 + random.nextInt(7);
            return Stream.of(new BlockPos(i, l, j));
        }
        return Stream.empty();
    }
}

