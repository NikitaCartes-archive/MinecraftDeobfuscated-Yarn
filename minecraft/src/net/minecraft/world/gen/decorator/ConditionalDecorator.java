package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class ConditionalDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public ConditionalDecorator(Codec<DC> codec) {
		super(codec);
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
		return this.shouldPlace(context, random, config, pos) ? Stream.of(pos) : Stream.of();
	}

	protected abstract boolean shouldPlace(DecoratorContext context, Random random, DC config, BlockPos pos);
}
