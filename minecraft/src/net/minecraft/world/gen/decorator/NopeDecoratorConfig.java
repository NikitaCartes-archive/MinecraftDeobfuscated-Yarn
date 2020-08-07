package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class NopeDecoratorConfig implements DecoratorConfig {
	public static final Codec<NopeDecoratorConfig> CODEC = Codec.unit((Supplier<NopeDecoratorConfig>)(() -> NopeDecoratorConfig.INSTANCE));
	public static final NopeDecoratorConfig INSTANCE = new NopeDecoratorConfig();
}
