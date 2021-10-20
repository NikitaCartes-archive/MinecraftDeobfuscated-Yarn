package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;

public class ScatterDecoratorConfig implements DecoratorConfig {
	public static final Codec<ScatterDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					IntProvider.createValidatingCodec(-16, 16).fieldOf("xz_spread").forGetter(scatterDecoratorConfig -> scatterDecoratorConfig.xzSpread),
					IntProvider.createValidatingCodec(-16, 16).fieldOf("y_spread").forGetter(scatterDecoratorConfig -> scatterDecoratorConfig.ySpread)
				)
				.apply(instance, ScatterDecoratorConfig::new)
	);
	public final IntProvider xzSpread;
	public final IntProvider ySpread;

	public ScatterDecoratorConfig(IntProvider xzSpread, IntProvider ySpread) {
		this.xzSpread = xzSpread;
		this.ySpread = ySpread;
	}
}
