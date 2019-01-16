package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3638 implements class_3662 {
	INSTANCE;

	@Override
	public int method_15867(class_3630 arg, int i, int j, int k, int l, int m) {
		if (!BiomeLayers.isShallowOcean(m)
			|| BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(k) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(j)) {
			if (!BiomeLayers.isShallowOcean(m)
				&& (BiomeLayers.isShallowOcean(l) || BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(j))
				&& arg.nextInt(5) == 0) {
				if (BiomeLayers.isShallowOcean(l)) {
					return m == 4 ? 4 : l;
				}

				if (BiomeLayers.isShallowOcean(i)) {
					return m == 4 ? 4 : i;
				}

				if (BiomeLayers.isShallowOcean(k)) {
					return m == 4 ? 4 : k;
				}

				if (BiomeLayers.isShallowOcean(j)) {
					return m == 4 ? 4 : j;
				}
			}

			return m;
		} else {
			int n = 1;
			int o = 1;
			if (!BiomeLayers.isShallowOcean(l) && arg.nextInt(n++) == 0) {
				o = l;
			}

			if (!BiomeLayers.isShallowOcean(k) && arg.nextInt(n++) == 0) {
				o = k;
			}

			if (!BiomeLayers.isShallowOcean(i) && arg.nextInt(n++) == 0) {
				o = i;
			}

			if (!BiomeLayers.isShallowOcean(j) && arg.nextInt(n++) == 0) {
				o = j;
			}

			if (arg.nextInt(3) == 0) {
				return o;
			} else {
				return o == 4 ? 4 : m;
			}
		}
	}
}
