package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;

public class HellFireDecorator extends class_3667<CountDecoratorConfig> {
	public HellFireDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15947(Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();

		for (int i = 0; i < random.nextInt(random.nextInt(countDecoratorConfig.count) + 1) + 1; i++) {
			int j = random.nextInt(16);
			int k = random.nextInt(120) + 4;
			int l = random.nextInt(16);
			list.add(blockPos.add(j, k, l));
		}

		return list.stream();
	}
}
