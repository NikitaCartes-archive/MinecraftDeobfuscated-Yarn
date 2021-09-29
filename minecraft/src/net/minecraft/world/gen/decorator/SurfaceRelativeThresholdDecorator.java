package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class SurfaceRelativeThresholdDecorator extends ConditionalDecorator<SurfaceRelativeThresholdDecoratorConfig> {
	public SurfaceRelativeThresholdDecorator(Codec<SurfaceRelativeThresholdDecoratorConfig> codec) {
		super(codec);
	}

	protected boolean shouldPlace(
		DecoratorContext decoratorContext, Random random, SurfaceRelativeThresholdDecoratorConfig surfaceRelativeThresholdDecoratorConfig, BlockPos blockPos
	) {
		long l = (long)decoratorContext.getTopY(surfaceRelativeThresholdDecoratorConfig.heightmap, blockPos.getX(), blockPos.getZ());
		long m = l + (long)surfaceRelativeThresholdDecoratorConfig.min;
		long n = l + (long)surfaceRelativeThresholdDecoratorConfig.max;
		return m <= (long)blockPos.getY() && (long)blockPos.getY() <= n;
	}
}
