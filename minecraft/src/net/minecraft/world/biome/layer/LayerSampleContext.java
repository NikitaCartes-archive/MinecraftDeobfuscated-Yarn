package net.minecraft.world.biome.layer;

public interface LayerSampleContext<R extends LayerSampler> extends LayerRandomnessSource {
	void initSeed(long l, long m);

	R method_15831(LayerOperator layerOperator);

	default R method_15832(LayerOperator layerOperator, R layerSampler) {
		return this.method_15831(layerOperator);
	}

	default R method_15828(LayerOperator layerOperator, R layerSampler, R layerSampler2) {
		return this.method_15831(layerOperator);
	}

	default int choose(int i, int j) {
		return this.nextInt(2) == 0 ? i : j;
	}

	default int choose(int i, int j, int k, int l) {
		int m = this.nextInt(4);
		if (m == 0) {
			return i;
		} else if (m == 1) {
			return j;
		} else {
			return m == 2 ? k : l;
		}
	}
}
