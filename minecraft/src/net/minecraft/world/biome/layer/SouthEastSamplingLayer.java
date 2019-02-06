package net.minecraft.world.biome.layer;

public interface SouthEastSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer {
	int sample(LayerRandomnessSource layerRandomnessSource, int i);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		int k = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
		return this.sample(layerSampleContext, k);
	}
}
