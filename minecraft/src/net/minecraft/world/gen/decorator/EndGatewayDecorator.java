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

public class EndGatewayDecorator extends Decorator<NopeDecoratorConfig> {
	public EndGatewayDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextInt(700) == 0) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, i, j);
			if (k > 0) {
				int l = k + 3 + random.nextInt(7);
				return Stream.of(new BlockPos(i, l, j));
			}
		}

		return Stream.empty();
	}
}
