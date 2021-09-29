/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.EnvironmentScanDecoratorConfig;

public class EnvironmentScanDecorator
extends Decorator<EnvironmentScanDecoratorConfig> {
    public EnvironmentScanDecorator(Codec<EnvironmentScanDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, EnvironmentScanDecoratorConfig environmentScanDecoratorConfig, BlockPos blockPos) {
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        StructureWorldAccess structureWorldAccess = decoratorContext.getWorld();
        for (int i = 0; i < environmentScanDecoratorConfig.maxSteps() && !structureWorldAccess.isOutOfHeightLimit(mutable.getY()); ++i) {
            if (environmentScanDecoratorConfig.targetCondition().test(structureWorldAccess, mutable)) {
                return Stream.of(mutable);
            }
            mutable.move(environmentScanDecoratorConfig.directionOfSearch());
        }
        return Stream.of(new BlockPos[0]);
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (EnvironmentScanDecoratorConfig)config, pos);
    }
}

