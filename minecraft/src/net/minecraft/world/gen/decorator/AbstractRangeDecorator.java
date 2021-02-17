package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractRangeDecorator<DC extends DecoratorConfig> extends Decorator<DC> {
	public AbstractRangeDecorator(Codec<DC> codec) {
		super(codec);
	}

	protected abstract int getY(DecoratorContext context, Random random, DC config, int y);

	@Override
	public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
		return Stream.of(new BlockPos(pos.getX(), this.getY(context, random, config, pos.getY()), pos.getZ()));
	}
}
