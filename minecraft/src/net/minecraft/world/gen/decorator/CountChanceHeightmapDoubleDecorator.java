package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CountChanceHeightmapDoubleDecorator extends Decorator<CountChanceDecoratorConfig> {
	public CountChanceHeightmapDoubleDecorator(Function<Dynamic<?>, ? extends CountChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15899(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		CountChanceDecoratorConfig countChanceDecoratorConfig,
		BlockPos blockPos
	) {
		return IntStream.range(0, countChanceDecoratorConfig.count).filter(i -> random.nextFloat() < countChanceDecoratorConfig.chance).mapToObj(i -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k) * 2;
			return l <= 0 ? null : new BlockPos(j, random.nextInt(l), k);
		}).filter(Objects::nonNull);
	}
}
