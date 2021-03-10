package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.feature.util.CaveSurface;

public class CaveSurfaceDecorator extends Decorator<CaveSurfaceDecoratorConfig> {
	public CaveSurfaceDecorator(Codec<CaveSurfaceDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, CaveSurfaceDecoratorConfig caveSurfaceDecoratorConfig, BlockPos blockPos
	) {
		Optional<CaveSurface> optional = CaveSurface.create(
			decoratorContext.getWorld(),
			blockPos,
			caveSurfaceDecoratorConfig.searchRange,
			AbstractBlock.AbstractBlockState::isAir,
			blockState -> blockState.getMaterial().isSolid()
		);
		if (!optional.isPresent()) {
			return Stream.of();
		} else {
			OptionalInt optionalInt = caveSurfaceDecoratorConfig.surface == VerticalSurfaceType.CEILING
				? ((CaveSurface)optional.get()).getCeilingHeight()
				: ((CaveSurface)optional.get()).getFloorHeight();
			return !optionalInt.isPresent() ? Stream.of() : Stream.of(blockPos.withY(optionalInt.getAsInt() - caveSurfaceDecoratorConfig.surface.getOffset()));
		}
	}
}
