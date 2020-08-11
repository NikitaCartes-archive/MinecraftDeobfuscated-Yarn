package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.GenerationStep;

public class CarvingMaskDecoratorConfig implements DecoratorConfig {
	public static final Codec<CarvingMaskDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GenerationStep.Carver.CODEC.fieldOf("step").forGetter(carvingMaskDecoratorConfig -> carvingMaskDecoratorConfig.step),
					Codec.FLOAT.fieldOf("probability").forGetter(carvingMaskDecoratorConfig -> carvingMaskDecoratorConfig.probability)
				)
				.apply(instance, CarvingMaskDecoratorConfig::new)
	);
	protected final GenerationStep.Carver step;
	protected final float probability;

	public CarvingMaskDecoratorConfig(GenerationStep.Carver step, float probability) {
		this.step = step;
		this.probability = probability;
	}
}
