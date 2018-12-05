package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.decorator.CountDecoratorConfig;

public class LightGemChanceDecorator extends class_3667<CountDecoratorConfig> {
	public LightGemChanceDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15953(Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
		return IntStream.range(0, random.nextInt(random.nextInt(countDecoratorConfig.count) + 1)).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(120) + 4;
			int l = random.nextInt(16);
			return blockPos.add(j, k, l);
		});
	}
}
