package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;

public class FireDecorator extends SimpleDecorator<CountConfig> {
	public FireDecorator(Codec<CountConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> method_15947(Random random, CountConfig countConfig, BlockPos blockPos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();

		for (int i = 0; i < random.nextInt(random.nextInt(countConfig.getCount().getValue(random)) + 1) + 1; i++) {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(120) + 4;
			list.add(new BlockPos(j, l, k));
		}

		return list.stream();
	}
}
