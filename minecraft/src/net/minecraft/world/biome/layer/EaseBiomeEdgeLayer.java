package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public enum EaseBiomeEdgeLayer implements CrossSamplingLayer {
	field_16091;

	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9460);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9440);
	private static final int JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.field_9474);
	private static final int BALDANDS_ID = Registry.BIOME.getRawId(Biomes.field_9415);
	private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9433);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9477);
	private static final int MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.field_9464);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.field_9471);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9420);
	private static final int SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9454);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		int[] is = new int[1];
		if (!this.method_15841(is, i, j, k, l, m, MOUNTAINS_ID, MOUNTAIN_EDGE_ID)
			&& !this.method_15840(is, i, j, k, l, m, WOODED_BADLANDS_PLATEAU_ID, BALDANDS_ID)
			&& !this.method_15840(is, i, j, k, l, m, BADLANDS_PLATEAU_ID, BALDANDS_ID)
			&& !this.method_15840(is, i, j, k, l, m, GIANT_TREE_TAIGA_ID, TAIGA_ID)) {
			if (m != DESERT_ID || i != SNOWY_TUNDRA_ID && j != SNOWY_TUNDRA_ID && l != SNOWY_TUNDRA_ID && k != SNOWY_TUNDRA_ID) {
				if (m == SWAMP_ID) {
					if (i == DESERT_ID
						|| j == DESERT_ID
						|| l == DESERT_ID
						|| k == DESERT_ID
						|| i == SNOWY_TAIGA_ID
						|| j == SNOWY_TAIGA_ID
						|| l == SNOWY_TAIGA_ID
						|| k == SNOWY_TAIGA_ID
						|| i == SNOWY_TUNDRA_ID
						|| j == SNOWY_TUNDRA_ID
						|| l == SNOWY_TUNDRA_ID
						|| k == SNOWY_TUNDRA_ID) {
						return PLAINS_ID;
					}

					if (i == JUNGLE_ID
						|| k == JUNGLE_ID
						|| j == JUNGLE_ID
						|| l == JUNGLE_ID
						|| i == BAMBOO_JUNGLE_ID
						|| k == BAMBOO_JUNGLE_ID
						|| j == BAMBOO_JUNGLE_ID
						|| l == BAMBOO_JUNGLE_ID) {
						return JUNGLE_EDGE_ID;
					}
				}

				return m;
			} else {
				return WOODED_MOUNTAINS_ID;
			}
		} else {
			return is[0];
		}
	}

	private boolean method_15841(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (!BiomeLayers.areSimilar(m, n)) {
			return false;
		} else {
			if (this.method_15839(i, n) && this.method_15839(j, n) && this.method_15839(l, n) && this.method_15839(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_15840(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (m != n) {
			return false;
		} else {
			if (BiomeLayers.areSimilar(i, n) && BiomeLayers.areSimilar(j, n) && BiomeLayers.areSimilar(l, n) && BiomeLayers.areSimilar(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_15839(int i, int j) {
		if (BiomeLayers.areSimilar(i, j)) {
			return true;
		} else {
			Biome biome = Registry.BIOME.get(i);
			Biome biome2 = Registry.BIOME.get(j);
			if (biome != null && biome2 != null) {
				Biome.TemperatureGroup temperatureGroup = biome.getTemperatureGroup();
				Biome.TemperatureGroup temperatureGroup2 = biome2.getTemperatureGroup();
				return temperatureGroup == temperatureGroup2
					|| temperatureGroup == Biome.TemperatureGroup.field_9375
					|| temperatureGroup2 == Biome.TemperatureGroup.field_9375;
			} else {
				return false;
			}
		}
	}
}
