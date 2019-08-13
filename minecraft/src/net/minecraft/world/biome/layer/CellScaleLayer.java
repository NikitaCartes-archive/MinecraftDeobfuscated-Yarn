package net.minecraft.world.biome.layer;

public enum CellScaleLayer implements ParentedLayer {
	field_16200;

	@Override
	public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
		int k = i - 2;
		int l = j - 2;
		int m = k >> 2;
		int n = l >> 2;
		int o = m << 2;
		int p = n << 2;
		layerSampleContext.initSeed((long)o, (long)p);
		double d = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		double e = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		layerSampleContext.initSeed((long)(o + 4), (long)p);
		double f = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double g = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		layerSampleContext.initSeed((long)o, (long)(p + 4));
		double h = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		double q = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		layerSampleContext.initSeed((long)(o + 4), (long)(p + 4));
		double r = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double s = ((double)layerSampleContext.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		int t = k & 3;
		int u = l & 3;
		double v = ((double)u - e) * ((double)u - e) + ((double)t - d) * ((double)t - d);
		double w = ((double)u - g) * ((double)u - g) + ((double)t - f) * ((double)t - f);
		double x = ((double)u - q) * ((double)u - q) + ((double)t - h) * ((double)t - h);
		double y = ((double)u - s) * ((double)u - s) + ((double)t - r) * ((double)t - r);
		if (v < w && v < x && v < y) {
			return layerSampler.sample(this.transformX(o), this.transformZ(p));
		} else if (w < v && w < x && w < y) {
			return layerSampler.sample(this.transformX(o + 4), this.transformZ(p)) & 0xFF;
		} else {
			return x < v && x < w && x < y
				? layerSampler.sample(this.transformX(o), this.transformZ(p + 4))
				: layerSampler.sample(this.transformX(o + 4), this.transformZ(p + 4)) & 0xFF;
		}
	}

	@Override
	public int transformX(int i) {
		return i >> 2;
	}

	@Override
	public int transformZ(int i) {
		return i >> 2;
	}
}
