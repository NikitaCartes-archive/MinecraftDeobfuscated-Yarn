package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class NopeDecoratorConfig implements DecoratorConfig {
	public static final Codec<NopeDecoratorConfig> field_24891 = Codec.unit((Supplier<NopeDecoratorConfig>)(() -> NopeDecoratorConfig.field_24892));
	public static final NopeDecoratorConfig field_24892 = new NopeDecoratorConfig();
}
