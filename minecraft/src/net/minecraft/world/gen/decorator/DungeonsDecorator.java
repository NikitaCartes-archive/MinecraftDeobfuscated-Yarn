package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DungeonsDecorator extends Decorator<LakeDecoratorConfig> {
	public DungeonsDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15933(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos
	) {
		int i = lakeDecoratorConfig.chance;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(chunkGenerator.getMaxY());
			return new BlockPos(j, l, k);
		});
	}
}
