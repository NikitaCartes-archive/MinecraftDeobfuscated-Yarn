package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class ScatterDecorator extends Decorator<ScatterDecoratorConfig> {
	public ScatterDecorator(Codec<ScatterDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, ScatterDecoratorConfig scatterDecoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX() + scatterDecoratorConfig.xzSpread.get(random);
		int j = blockPos.getY() + scatterDecoratorConfig.ySpread.get(random);
		int k = blockPos.getZ() + scatterDecoratorConfig.xzSpread.get(random);
		BlockPos blockPos2 = new BlockPos(i, j, k);
		ChunkPos chunkPos = new ChunkPos(blockPos2);
		ChunkPos chunkPos2 = new ChunkPos(blockPos);
		int l = MathHelper.abs(chunkPos.x - chunkPos2.x);
		int m = MathHelper.abs(chunkPos.z - chunkPos2.z);
		return l <= 1 && m <= 1 ? Stream.of(new BlockPos(i, j, k)) : Stream.empty();
	}
}
