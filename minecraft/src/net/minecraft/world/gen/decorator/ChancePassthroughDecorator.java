package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3267;
import net.minecraft.util.math.BlockPos;

public class ChancePassthroughDecorator extends SimpleDecorator<class_3267> {
	public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends class_3267> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(Random random, class_3267 arg, BlockPos blockPos) {
		return random.nextFloat() < 1.0F / (float)arg.field_14192 ? Stream.of(blockPos) : Stream.empty();
	}
}
