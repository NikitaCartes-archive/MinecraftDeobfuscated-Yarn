package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.GenerationStep;

public class CarvingMaskDecoratorConfig implements DecoratorConfig {
	public static final Codec<CarvingMaskDecoratorConfig> CODEC = GenerationStep.Carver.CODEC
		.fieldOf("step")
		.<CarvingMaskDecoratorConfig>xmap(CarvingMaskDecoratorConfig::new, config -> config.carver)
		.codec();
	protected final GenerationStep.Carver carver;

	public CarvingMaskDecoratorConfig(GenerationStep.Carver carver) {
		this.carver = carver;
	}
}
