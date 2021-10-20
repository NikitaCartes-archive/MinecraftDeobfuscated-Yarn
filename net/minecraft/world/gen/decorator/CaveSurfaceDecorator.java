/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.decorator.CaveSurfaceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.feature.util.CaveSurface;

public class CaveSurfaceDecorator
extends Decorator<CaveSurfaceDecoratorConfig> {
    public CaveSurfaceDecorator(Codec<CaveSurfaceDecoratorConfig> codec) {
        super(codec);
    }

    public static boolean method_39183(BlockState blockState) {
        return blockState.isAir() || blockState.isOf(Blocks.WATER);
    }

    private static Predicate<BlockState> method_39184(boolean bl) {
        if (bl) {
            return CaveSurfaceDecorator::method_39183;
        }
        return AbstractBlock.AbstractBlockState::isAir;
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, CaveSurfaceDecoratorConfig caveSurfaceDecoratorConfig, BlockPos blockPos) {
        OptionalInt optionalInt;
        Optional<CaveSurface> optional = CaveSurface.create(decoratorContext.getWorld(), blockPos, caveSurfaceDecoratorConfig.searchRange, CaveSurfaceDecorator.method_39184(caveSurfaceDecoratorConfig.field_35422), blockState -> blockState.getMaterial().isSolid());
        if (optional.isEmpty()) {
            return Stream.of(new BlockPos[0]);
        }
        OptionalInt optionalInt2 = optionalInt = caveSurfaceDecoratorConfig.surface == VerticalSurfaceType.CEILING ? optional.get().getCeilingHeight() : optional.get().getFloorHeight();
        if (optionalInt.isEmpty()) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(blockPos.withY(optionalInt.getAsInt() - caveSurfaceDecoratorConfig.surface.getOffset()));
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (CaveSurfaceDecoratorConfig)config, pos);
    }
}

