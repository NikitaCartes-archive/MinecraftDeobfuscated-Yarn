package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface SouthEastSamplingLayer extends ParentedLayer, NorthWestCoordinateTransformer {
	int sample(LayerRandomnessSource context, int se);

	@Override
	default int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
		int i = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
		return this.sample(context, i);
	}
}
