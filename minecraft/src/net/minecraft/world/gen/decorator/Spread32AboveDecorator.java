package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class Spread32AboveDecorator extends Decorator<NopeDecoratorConfig> {
	public Spread32AboveDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		int i = random.nextInt(blockPos.getY() + 32);
		return Stream.of(new BlockPos(blockPos.getX(), i, blockPos.getZ()));
	}
}
