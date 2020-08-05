package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer, NorthWestCoordinateTransformer {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Int2IntMap field_26727 = Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
		int2IntOpenHashMap.put(1, 129);
		int2IntOpenHashMap.put(2, 130);
		int2IntOpenHashMap.put(3, 131);
		int2IntOpenHashMap.put(4, 132);
		int2IntOpenHashMap.put(5, 133);
		int2IntOpenHashMap.put(6, 134);
		int2IntOpenHashMap.put(12, 140);
		int2IntOpenHashMap.put(21, 149);
		int2IntOpenHashMap.put(23, 151);
		int2IntOpenHashMap.put(27, 155);
		int2IntOpenHashMap.put(28, 156);
		int2IntOpenHashMap.put(29, 157);
		int2IntOpenHashMap.put(30, 158);
		int2IntOpenHashMap.put(32, 160);
		int2IntOpenHashMap.put(33, 161);
		int2IntOpenHashMap.put(34, 162);
		int2IntOpenHashMap.put(35, 163);
		int2IntOpenHashMap.put(36, 164);
		int2IntOpenHashMap.put(37, 165);
		int2IntOpenHashMap.put(38, 166);
		int2IntOpenHashMap.put(39, 167);
	});

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x + 1), this.transformZ(z + 1));
		int j = sampler2.sample(this.transformX(x + 1), this.transformZ(z + 1));
		if (i > 255) {
			LOGGER.debug("old! {}", i);
		}

		int k = (j - 2) % 29;
		if (!BiomeLayers.isShallowOcean(i) && j >= 2 && k == 1) {
			return field_26727.getOrDefault(i, i);
		} else {
			if (context.nextInt(3) == 0 || k == 0) {
				int l = i;
				if (i == 2) {
					l = 17;
				} else if (i == 4) {
					l = 18;
				} else if (i == 27) {
					l = 28;
				} else if (i == 29) {
					l = 1;
				} else if (i == 5) {
					l = 19;
				} else if (i == 32) {
					l = 33;
				} else if (i == 30) {
					l = 31;
				} else if (i == 1) {
					l = context.nextInt(3) == 0 ? 18 : 4;
				} else if (i == 12) {
					l = 13;
				} else if (i == 21) {
					l = 22;
				} else if (i == 168) {
					l = 169;
				} else if (i == 0) {
					l = 24;
				} else if (i == 45) {
					l = 48;
				} else if (i == 46) {
					l = 49;
				} else if (i == 10) {
					l = 50;
				} else if (i == 3) {
					l = 34;
				} else if (i == 35) {
					l = 36;
				} else if (BiomeLayers.areSimilar(i, 38)) {
					l = 37;
				} else if ((i == 24 || i == 48 || i == 49 || i == 50) && context.nextInt(3) == 0) {
					l = context.nextInt(2) == 0 ? 1 : 4;
				}

				if (k == 0 && l != i) {
					l = field_26727.getOrDefault(l, i);
				}

				if (l != i) {
					int m = 0;
					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 0)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 2), this.transformZ(z + 1)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 0), this.transformZ(z + 1)), i)) {
						m++;
					}

					if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 2)), i)) {
						m++;
					}

					if (m >= 3) {
						return l;
					}
				}
			}

			return i;
		}
	}
}
