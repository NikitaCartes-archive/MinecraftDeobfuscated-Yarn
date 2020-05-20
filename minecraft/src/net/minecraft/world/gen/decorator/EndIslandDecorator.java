package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class EndIslandDecorator extends SimpleDecorator<NopeDecoratorConfig> {
	public EndIslandDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		Stream<BlockPos> stream = Stream.empty();
		if (random.nextInt(14) == 0) {
			stream = Stream.concat(stream, Stream.of(blockPos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
			if (random.nextInt(4) == 0) {
				stream = Stream.concat(stream, Stream.of(blockPos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
			}

			return stream;
		} else {
			return Stream.empty();
		}
	}
}
