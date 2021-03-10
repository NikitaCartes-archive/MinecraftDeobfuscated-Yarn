package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class WaterThresholdDecorator extends Decorator<WaterThresholdDecoratorConfig> {
	public WaterThresholdDecorator(Codec<WaterThresholdDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		DecoratorContext decoratorContext, Random random, WaterThresholdDecoratorConfig waterThresholdDecoratorConfig, BlockPos blockPos
	) {
		int i = decoratorContext.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
		int j = decoratorContext.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
		return j - i > waterThresholdDecoratorConfig.maxWaterDepth ? Stream.of() : Stream.of(blockPos);
	}
}
