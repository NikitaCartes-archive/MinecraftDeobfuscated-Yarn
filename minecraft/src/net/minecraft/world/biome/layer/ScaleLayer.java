package net.minecraft.world.biome.layer;

public enum ScaleLayer implements ParentedLayer {
	field_16196,
	field_16198 {
		@Override
		protected int method_15853(LayerSampleContext<?> layerSampleContext, int i, int j, int k, int l) {
			return layerSampleContext.choose(i, j, k, l);
		}
	};

	private ScaleLayer() {
	}

	@Override
	public int transformX(int i) {
		return i >> 1;
	}

	@Override
	public int transformZ(int i) {
		return i >> 1;
	}

	@Override
	public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		int k = layerSampler.sample(this.transformX(i), this.transformZ(j));
		layerSampleContext.initSeed((long)(i >> 1 << 1), (long)(j >> 1 << 1));
		int l = i & 1;
		int m = j & 1;
		if (l == 0 && m == 0) {
			return k;
		} else {
			int n = layerSampler.sample(this.transformX(i), this.transformZ(j + 1));
			int o = layerSampleContext.choose(k, n);
			if (l == 0 && m == 1) {
				return o;
			} else {
				int p = layerSampler.sample(this.transformX(i + 1), this.transformZ(j));
				int q = layerSampleContext.choose(k, p);
				if (l == 1 && m == 0) {
					return q;
				} else {
					int r = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
					return this.method_15853(layerSampleContext, k, p, n, r);
				}
			}
		}
	}

	protected int method_15853(LayerSampleContext<?> layerSampleContext, int i, int j, int k, int l) {
		if (j == k && k == l) {
			return j;
		} else if (i == j && i == k) {
			return i;
		} else if (i == j && i == l) {
			return i;
		} else if (i == k && i == l) {
			return i;
		} else if (i == j && k != l) {
			return i;
		} else if (i == k && j != l) {
			return i;
		} else if (i == l && j != k) {
			return i;
		} else if (j == k && i != l) {
			return j;
		} else if (j == l && i != k) {
			return j;
		} else {
			return k == l && i != j ? k : layerSampleContext.choose(i, j, k, l);
		}
	}
}
