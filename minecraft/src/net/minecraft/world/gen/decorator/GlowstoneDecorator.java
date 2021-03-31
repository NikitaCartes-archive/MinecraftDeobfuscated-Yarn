package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;

public class GlowstoneDecorator extends AbstractCountDecorator<CountConfig> {
	public GlowstoneDecorator(Codec<CountConfig> codec) {
		super(codec);
	}

	protected int getCount(Random random, CountConfig countConfig, BlockPos blockPos) {
		return random.nextInt(random.nextInt(countConfig.getCount().get(random)) + 1);
	}
}
