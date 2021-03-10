package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class HeightmapDecorator extends Decorator<HeightmapDecoratorConfig> {
	public HeightmapDecorator(Codec<HeightmapDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, HeightmapDecoratorConfig heightmapDecoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = decoratorContext.getTopY(heightmapDecoratorConfig.heightmap, i, j);
		return k > decoratorContext.getBottomY() ? Stream.of(new BlockPos(i, k, j)) : Stream.of();
	}
}
