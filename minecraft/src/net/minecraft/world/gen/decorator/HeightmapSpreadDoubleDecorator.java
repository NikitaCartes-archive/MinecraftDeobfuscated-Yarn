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
	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, DC decoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = decoratorContext.getTopY(this.getHeightmapType(decoratorConfig), i, j);
		return k == 0 ? Stream.of() : Stream.of(new BlockPos(i, random.nextInt(k * 2), j));
	}
}
