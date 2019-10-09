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

public class HeightmapDecorator extends Decorator<NopeDecoratorConfig> {
	public HeightmapDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15940(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		int i = random.nextInt(16) + blockPos.getX();
		int j = random.nextInt(16) + blockPos.getZ();
		int k = iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
		return Stream.of(new BlockPos(i, k, j));
	}
}
