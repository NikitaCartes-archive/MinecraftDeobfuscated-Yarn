package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;

public class CountDecorator extends SimpleDecorator<CountConfig> {
	public CountDecorator(Codec<CountConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, CountConfig countConfig, BlockPos blockPos) {
		return IntStream.range(0, countConfig.getCount().getValue(random)).mapToObj(i -> blockPos);
	}
}
