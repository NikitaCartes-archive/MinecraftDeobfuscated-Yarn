package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface CrossSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer {
	int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center);

	@Override
	default int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
		return this.sample(
			context,
			parent.sample(this.transformX(x + 1), this.transformZ(z + 0)),
			parent.sample(this.transformX(x + 2), this.transformZ(z + 1)),
			parent.sample(this.transformX(x + 1), this.transformZ(z + 2)),
			parent.sample(this.transformX(x + 0), this.transformZ(z + 1)),
			parent.sample(this.transformX(x + 1), this.transformZ(z + 1))
		);
	}
}
