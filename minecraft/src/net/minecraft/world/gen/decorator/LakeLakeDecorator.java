package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class LakeLakeDecorator extends Decorator<LakeDecoratorConfig> {
	public LakeLakeDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15931(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextInt(lakeDecoratorConfig.chance / 10) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(random.nextInt(chunkGenerator.getMaxY() - 8) + 8);
			int k = random.nextInt(16);
			if (j < iWorld.getSeaLevel() || random.nextInt(lakeDecoratorConfig.chance / 8) == 0) {
				return Stream.of(blockPos.add(i, j, k));
			}
		}

		return Stream.empty();
	}
}
