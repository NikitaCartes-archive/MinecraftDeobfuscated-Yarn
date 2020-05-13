package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IcebergDecorator extends Decorator<ChanceDecoratorConfig> {
	public IcebergDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance) {
			int i = random.nextInt(8) + 4 + blockPos.getX();
			int j = random.nextInt(8) + 4 + blockPos.getZ();
			int k = worldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, i, j);
			return Stream.of(new BlockPos(i, k, j));
		} else {
			return Stream.empty();
		}
	}
}
