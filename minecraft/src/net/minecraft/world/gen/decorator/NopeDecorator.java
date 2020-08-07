package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class NopeDecorator extends SimpleDecorator<NopeDecoratorConfig> {
	public NopeDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> method_15939(Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		return Stream.of(blockPos);
	}
}
