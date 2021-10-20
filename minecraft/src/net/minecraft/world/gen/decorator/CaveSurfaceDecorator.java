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
import net.minecraft.world.gen.feature.util.CaveSurface;

public class CaveSurfaceDecorator extends Decorator<CaveSurfaceDecoratorConfig> {
	public CaveSurfaceDecorator(Codec<CaveSurfaceDecoratorConfig> codec) {
		super(codec);
	}

	public static boolean method_39183(BlockState blockState) {
		return blockState.isAir() || blockState.isOf(Blocks.WATER);
	}

	private static Predicate<BlockState> method_39184(boolean bl) {
		return bl ? CaveSurfaceDecorator::method_39183 : AbstractBlock.AbstractBlockState::isAir;
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, CaveSurfaceDecoratorConfig caveSurfaceDecoratorConfig, BlockPos blockPos
	) {
		Optional<CaveSurface> optional = CaveSurface.create(
			decoratorContext.getWorld(),
			blockPos,
			caveSurfaceDecoratorConfig.searchRange,
			method_39184(caveSurfaceDecoratorConfig.field_35422),
			blockState -> blockState.getMaterial().isSolid()
		);
		if (optional.isEmpty()) {
			return Stream.of();
		} else {
			OptionalInt optionalInt = caveSurfaceDecoratorConfig.surface == VerticalSurfaceType.CEILING
				? ((CaveSurface)optional.get()).getCeilingHeight()
				: ((CaveSurface)optional.get()).getFloorHeight();
			return optionalInt.isEmpty() ? Stream.of() : Stream.of(blockPos.withY(optionalInt.getAsInt() - caveSurfaceDecoratorConfig.surface.getOffset()));
		}
	}
}
