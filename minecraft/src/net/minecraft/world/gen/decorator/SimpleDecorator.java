package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class SimpleDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public SimpleDecorator(Codec<DC> codec) {
		super(codec);
	}

	@Override
	public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
		return this.getPositions(random, config, pos);
	}

	protected abstract Stream<BlockPos> getPositions(Random random, DC config, BlockPos pos);
}
