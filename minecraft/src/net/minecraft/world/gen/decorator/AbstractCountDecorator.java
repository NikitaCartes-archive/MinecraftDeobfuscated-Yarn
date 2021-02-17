package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractCountDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public AbstractCountDecorator(Codec<DC> codec) {
		super(codec);
	}

	protected abstract int getCount(Random random, DC config, BlockPos pos);

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
		return IntStream.range(0, this.getCount(random, config, pos)).mapToObj(i -> pos);
	}
}
