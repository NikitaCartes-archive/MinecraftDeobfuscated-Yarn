package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum CellScaleLayer implements ParentedLayer {
	INSTANCE;

	@Override
	public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
		int i = x - 2;
		int j = z - 2;
		int k = i >> 2;
		int l = j >> 2;
		int m = k << 2;
		int n = l << 2;
		context.initSeed((long)m, (long)n);
		double d = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		double e = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		context.initSeed((long)(m + 4), (long)n);
		double f = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double g = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		context.initSeed((long)m, (long)(n + 4));
		double h = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6;
		double o = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		context.initSeed((long)(m + 4), (long)(n + 4));
		double p = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double q = ((double)context.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		int r = i & 3;
		int s = j & 3;
		double t = ((double)s - e) * ((double)s - e) + ((double)r - d) * ((double)r - d);
		double u = ((double)s - g) * ((double)s - g) + ((double)r - f) * ((double)r - f);
		double v = ((double)s - o) * ((double)s - o) + ((double)r - h) * ((double)r - h);
		double w = ((double)s - q) * ((double)s - q) + ((double)r - p) * ((double)r - p);
		if (t < u && t < v && t < w) {
			return parent.sample(this.transformX(m), this.transformZ(n));
		} else if (u < t && u < v && u < w) {
			return parent.sample(this.transformX(m + 4), this.transformZ(n)) & 0xFF;
		} else {
			return v < t && v < u && v < w
				? parent.sample(this.transformX(m), this.transformZ(n + 4))
				: parent.sample(this.transformX(m + 4), this.transformZ(n + 4)) & 0xFF;
		}
	}

	@Override
	public int transformX(int x) {
		return x >> 2;
	}

	@Override
	public int transformZ(int y) {
		return y >> 2;
	}
}
