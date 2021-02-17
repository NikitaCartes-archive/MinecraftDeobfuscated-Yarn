package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;

public class EndGatewayDecorator extends AbstractRangeDecorator<NopeDecoratorConfig> {
	public EndGatewayDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected int getY(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, int i) {
		return i + 3 + random.nextInt(7);
	}
}
