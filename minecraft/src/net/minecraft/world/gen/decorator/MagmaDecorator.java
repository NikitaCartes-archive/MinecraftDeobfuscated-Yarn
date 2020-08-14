package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class MagmaDecorator extends Decorator<NopeDecoratorConfig> {
	public MagmaDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		int i = decoratorContext.getSeaLevel();
		int j = i - 5 + random.nextInt(10);
		return Stream.of(new BlockPos(blockPos.getX(), j, blockPos.getZ()));
	}
}
