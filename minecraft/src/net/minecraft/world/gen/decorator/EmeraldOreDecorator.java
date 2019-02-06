package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class EmeraldOreDecorator extends SimpleDecorator<NopeDecoratorConfig> {
	public EmeraldOreDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15922(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		int i = 3 + random.nextInt(6);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(28) + 4;
			int l = random.nextInt(16);
			return blockPos.add(j, k, l);
		});
	}
}
