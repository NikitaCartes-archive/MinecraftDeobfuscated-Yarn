package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class SpreadDoubleHeightmapDecorator extends Decorator<HeightmapDecoratorConfig> {
	public SpreadDoubleHeightmapDecorator(Codec<HeightmapDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, HeightmapDecoratorConfig heightmapDecoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = decoratorContext.getTopY(heightmapDecoratorConfig.heightmap, i, j);
		return k == decoratorContext.getBottomY()
			? Stream.of()
			: Stream.of(new BlockPos(i, decoratorContext.getBottomY() + random.nextInt((k - decoratorContext.getBottomY()) * 2), j));
	}
}
