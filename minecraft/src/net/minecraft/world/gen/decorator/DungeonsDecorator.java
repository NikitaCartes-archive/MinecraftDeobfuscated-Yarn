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

public class DungeonsDecorator extends Decorator<DungeonDecoratorConfig> {
	public DungeonsDecorator(Function<Dynamic<?>, ? extends DungeonDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15933(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, DungeonDecoratorConfig dungeonDecoratorConfig, BlockPos blockPos
	) {
		int i = dungeonDecoratorConfig.chance;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(chunkGenerator.getMaxY());
			int l = random.nextInt(16);
			return blockPos.add(j, k, l);
		});
	}
}
