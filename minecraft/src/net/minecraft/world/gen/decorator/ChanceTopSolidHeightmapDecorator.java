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

public class ChanceTopSolidHeightmapDecorator extends Decorator<LakeDecoratorConfig> {
	public ChanceTopSolidHeightmapDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)lakeDecoratorConfig.chance) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
			return Stream.of(new BlockPos(i, k, j));
		} else {
			return Stream.empty();
		}
	}
}
