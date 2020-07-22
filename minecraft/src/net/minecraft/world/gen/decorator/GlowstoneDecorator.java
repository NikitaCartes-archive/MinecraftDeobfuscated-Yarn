package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;

public class GlowstoneDecorator extends SimpleDecorator<CountConfig> {
	public GlowstoneDecorator(Codec<CountConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, CountConfig countConfig, BlockPos blockPos) {
		return IntStream.range(0, random.nextInt(random.nextInt(countConfig.getCount().getValue(random)) + 1)).mapToObj(i -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(120) + 4;
			return new BlockPos(j, l, k);
		});
	}
}
