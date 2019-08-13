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

public class ChanceTopSolidHeightmapDecorator extends Decorator<ChanceDecoratorConfig> {
	public ChanceTopSolidHeightmapDecorator(Function<Dynamic<?>, ? extends ChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_14346(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			int k = iWorld.getTop(Heightmap.Type.field_13195, blockPos.getX() + i, blockPos.getZ() + j);
			return Stream.of(new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j));
		} else {
			return Stream.empty();
		}
	}
}
