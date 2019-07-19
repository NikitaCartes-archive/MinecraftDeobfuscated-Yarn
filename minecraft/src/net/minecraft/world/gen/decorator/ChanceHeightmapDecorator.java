package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3267;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class ChanceHeightmapDecorator extends Decorator<class_3267> {
	public ChanceHeightmapDecorator(Function<Dynamic<?>, ? extends class_3267> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, class_3267 arg, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)arg.field_14192) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			BlockPos blockPos2 = iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(i, 0, j));
			return Stream.of(blockPos2);
		} else {
			return Stream.empty();
		}
	}
}
