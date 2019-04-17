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

public class ChanceHeightmapDoubleDecorator extends Decorator<ChanceDecoratorConfig> {
	public ChanceHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_14342(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			int k = iWorld.getTopPosition(Heightmap.Type.field_13197, blockPos.add(i, 0, j)).getY() * 2;
			if (k <= 0) {
				return Stream.empty();
			} else {
				int l = random.nextInt(k);
				return Stream.of(blockPos.add(i, l, j));
			}
		} else {
			return Stream.empty();
		}
	}
}
