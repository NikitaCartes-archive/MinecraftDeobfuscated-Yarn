package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.decorator.LakeDecoratorConfig;

public class WaterLakeDecorator extends Decorator<LakeDecoratorConfig> {
	public WaterLakeDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15930(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextInt(lakeDecoratorConfig.chance) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(chunkGenerator.method_12104());
			int k = random.nextInt(16);
			return Stream.of(blockPos.add(i, j, k));
		} else {
			return Stream.empty();
		}
	}
}
