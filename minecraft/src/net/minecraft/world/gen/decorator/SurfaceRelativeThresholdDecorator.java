package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class SurfaceRelativeThresholdDecorator extends Decorator<SurfaceRelativeThresholdDecoratorConfig> {
	public SurfaceRelativeThresholdDecorator(Codec<SurfaceRelativeThresholdDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, SurfaceRelativeThresholdDecoratorConfig surfaceRelativeThresholdDecoratorConfig, BlockPos blockPos
	) {
		long l = (long)decoratorContext.getTopY(surfaceRelativeThresholdDecoratorConfig.heightmap, blockPos.getX(), blockPos.getZ());
		long m = l + (long)surfaceRelativeThresholdDecoratorConfig.min;
		long n = l + (long)surfaceRelativeThresholdDecoratorConfig.max;
		return (long)blockPos.getY() >= m && (long)blockPos.getY() <= n ? Stream.of(blockPos) : Stream.of();
	}
}
