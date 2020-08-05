package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer {
	INSTANCE;

	private static final IntSet field_26728 = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
	private static final IntSet field_26729 = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		if (center == 14) {
			if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
				return 15;
			}
		} else if (field_26729.contains(center)) {
			if (!isWooded(n) || !isWooded(e) || !isWooded(s) || !isWooded(w)) {
				return 23;
			}

			if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
				return 16;
			}
		} else if (center != 3 && center != 34 && center != 20) {
			if (field_26728.contains(center)) {
				if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return 26;
				}
			} else if (center != 37 && center != 38) {
				if (!BiomeLayers.isOcean(center)
					&& center != 7
					&& center != 6
					&& (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return 16;
				}
			} else if (!BiomeLayers.isOcean(n)
				&& !BiomeLayers.isOcean(e)
				&& !BiomeLayers.isOcean(s)
				&& !BiomeLayers.isOcean(w)
				&& (!this.isBadlands(n) || !this.isBadlands(e) || !this.isBadlands(s) || !this.isBadlands(w))) {
				return 2;
			}
		} else if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
			return 25;
		}

		return center;
	}

	private static boolean isWooded(int id) {
		return field_26729.contains(id) || id == 4 || id == 5 || BiomeLayers.isOcean(id);
	}

	private boolean isBadlands(int id) {
		return id == 37 || id == 38 || id == 39 || id == 165 || id == 166 || id == 167;
	}
}
