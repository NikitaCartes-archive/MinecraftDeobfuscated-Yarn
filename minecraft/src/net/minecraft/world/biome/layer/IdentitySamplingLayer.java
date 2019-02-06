package net.minecraft.world.biome.layer;

public interface IdentitySamplingLayer extends ParentedLayer, IdentityCoordinateTransformer {
	int sample(LayerRandomnessSource layerRandomnessSource, int i);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		return this.sample(layerSampleContext, layerSampler.sample(this.transformX(i), this.transformZ(j)));
	}
}
