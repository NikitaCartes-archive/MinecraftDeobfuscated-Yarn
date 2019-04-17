package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class ChanceHeightmapDecorator extends Decorator<ChanceDecoratorConfig> {
	public ChanceHeightmapDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_14343(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			BlockPos blockPos2 = iWorld.getTopPosition(Heightmap.Type.field_13197, blockPos.add(i, 0, j));
			return Stream.of(blockPos2);
		} else {
			return Stream.empty();
		}
	}
}
