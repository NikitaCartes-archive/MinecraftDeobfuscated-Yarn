package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class HeightmapSpreadDoubleDecorator<DC extends DecoratorConfig> extends AbstractHeightmapDecorator<DC> {
	public HeightmapSpreadDoubleDecorator(Codec<DC> codec) {
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
		return k == context.getSectionCount()
			? Stream.of()
			: Stream.of(new BlockPos(i, context.getSectionCount() + random.nextInt((k - context.getSectionCount()) * 2), j));
	}
}
