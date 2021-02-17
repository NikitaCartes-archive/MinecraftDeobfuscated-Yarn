package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DarkOakTreeDecorator extends Decorator<NopeDecoratorConfig> {
	public DarkOakTreeDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		return IntStream.range(0, 16).mapToObj(i -> {
			int j = i / 4;
			int k = i % 4;
			int l = j * 4 + 1 + random.nextInt(3) + blockPos.getX();
			int m = k * 4 + 1 + random.nextInt(3) + blockPos.getZ();
			return new BlockPos(l, blockPos.getY(), m);
		});
	}
}
