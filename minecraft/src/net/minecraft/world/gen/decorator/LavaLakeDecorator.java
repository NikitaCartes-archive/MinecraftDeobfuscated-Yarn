package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class LavaLakeDecorator extends Decorator<LakeDecoratorConfig> {
	public LavaLakeDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextInt(lakeDecoratorConfig.chance / 10) == 0) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = random.nextInt(random.nextInt(chunkGenerator.getMaxY() - 8) + 8);
			if (k < iWorld.getSeaLevel() || random.nextInt(lakeDecoratorConfig.chance / 8) == 0) {
				return Stream.of(new BlockPos(i, k, j));
			}
		}

		return Stream.empty();
	}
}