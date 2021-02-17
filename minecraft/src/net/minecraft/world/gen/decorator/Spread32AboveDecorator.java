package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;

public class Spread32AboveDecorator extends AbstractRangeDecorator<NopeDecoratorConfig> {
	public Spread32AboveDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, int i) {
		return random.nextInt(Math.max(i, 0) + 32);
	}
}
