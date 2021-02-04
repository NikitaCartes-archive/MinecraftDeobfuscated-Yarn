package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class SpreadDoubleHeightmapDecorator<DC extends DecoratorConfig> extends AbstractHeightmapDecorator<DC> {
	public SpreadDoubleHeightmapDecorator(Codec<DC> codec) {
		super(codec);
	}

	@Override
	protected Heightmap.Type getHeightmapType(DC config) {
		return Heightmap.Type.MOTION_BLOCKING;
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getZ();
		int k = context.getTopY(this.getHeightmapType(config), i, j);
		return k == context.getBottomY() ? Stream.of() : Stream.of(new BlockPos(i, context.getBottomY() + random.nextInt((k - context.getBottomY()) * 2), j));
	}
}
