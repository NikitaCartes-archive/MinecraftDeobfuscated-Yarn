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
		long l = (long)decoratorContext.getTopY(surfaceRelativeThresholdDecoratorConfig.field_34722, blockPos.getX(), blockPos.getZ());
		long m = l + (long)surfaceRelativeThresholdDecoratorConfig.field_34723;
		long n = l + (long)surfaceRelativeThresholdDecoratorConfig.field_34724;
		return (long)blockPos.getY() >= m && (long)blockPos.getY() <= n ? Stream.of(blockPos) : Stream.of();
	}
}
