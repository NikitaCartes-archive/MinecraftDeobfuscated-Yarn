package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.decorator.NopeDecoratorConfig;

public class NopeDecorator extends class_3667<NopeDecoratorConfig> {
	public NopeDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15939(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		return Stream.of(blockPos);
	}
}
