package net.minecraft.world.biome.layer;

public interface DiagonalCrossSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer {
	int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m);

	@Override
	default int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		return this.sample(
			layerSampleContext,
			layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 2)),
			layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 2)),
			layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 0)),
			layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 0)),
			layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1))
		);
	}
}
