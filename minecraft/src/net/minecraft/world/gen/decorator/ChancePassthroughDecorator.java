package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChancePassthroughDecorator extends SimpleDecorator<LakeDecoratorConfig> {
	public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends LakeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(Random random, LakeDecoratorConfig lakeDecoratorConfig, BlockPos blockPos) {
		return random.nextFloat() < 1.0F / (float)lakeDecoratorConfig.chance ? Stream.of(blockPos) : Stream.empty();
	}
}
