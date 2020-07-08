package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class SquareDecorator extends SimpleDecorator<NopeDecoratorConfig> {
	public SquareDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		int i = random.nextInt(16) + blockPos.getX();
		int j = random.nextInt(16) + blockPos.getZ();
		int k = blockPos.getY();
		return Stream.of(new BlockPos(i, k, j));
	}
}
