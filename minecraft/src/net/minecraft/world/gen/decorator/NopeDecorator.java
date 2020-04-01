package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class NopeDecorator extends SimpleDecorator<NopeDecoratorConfig> {
	public NopeDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function, Function<Random, ? extends NopeDecoratorConfig> function2) {
		super(function, function2);
	}

	public Stream<BlockPos> getPositions(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		return Stream.of(blockPos);
	}
}
