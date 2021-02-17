package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class IcebergDecorator extends Decorator<NopeDecoratorConfig> {
	public IcebergDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		int i = random.nextInt(8) + 4 + blockPos.getX();
		int j = random.nextInt(8) + 4 + blockPos.getZ();
		return Stream.of(new BlockPos(i, blockPos.getY(), j));
	}
}
