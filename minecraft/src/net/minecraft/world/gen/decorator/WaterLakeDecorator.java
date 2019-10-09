package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class WaterLakeDecorator extends Decorator<LakeDecoratorConfig> {
	public WaterLakeDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15930(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextInt(lakeDecoratorConfig.chance) == 0) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = random.nextInt(chunkGenerator.getMaxY());
			return Stream.of(new BlockPos(i, k, j));
		} else {
			return Stream.empty();
		}
	}
}
