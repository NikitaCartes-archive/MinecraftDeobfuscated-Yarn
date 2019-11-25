package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface IdentitySamplingLayer extends ParentedLayer, IdentityCoordinateTransformer {
	int sample(LayerRandomnessSource context, int value);

	@Override
	default int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
		return this.sample(context, parent.sample(this.transformX(x), this.transformZ(z)));
	}
}
