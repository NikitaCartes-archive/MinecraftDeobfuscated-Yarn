package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class HeightmapDecorator<DC extends DecoratorConfig> extends AbstractHeightmapDecorator<DC> {
	public HeightmapDecorator(Codec<DC> codec) {
		super(codec);
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, DC decoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = decoratorContext.getTopY(this.getHeightmapType(decoratorConfig), i, j);
		return k > 0 ? Stream.of(new BlockPos(i, k, j)) : Stream.of();
	}
}
