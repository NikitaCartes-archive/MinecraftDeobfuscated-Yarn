package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer {
	INSTANCE;

	private static final int BEACH_ID = Registry.BIOME.getRawId(Biomes.BEACH);
	private static final int SNOWY_BEACH_ID = Registry.BIOME.getRawId(Biomes.SNOWY_BEACH);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.MOUNTAINS);
	private static final int WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.WOODED_MOUNTAINS);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE_EDGE);
	private static final int JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.JUNGLE_HILLS);
	private static final int BADLANDS_ID = Registry.BIOME.getRawId(Biomes.BADLANDS);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int ERODED_BADLANDS_ID = Registry.BIOME.getRawId(Biomes.ERODED_BADLANDS);
	private static final int MODIFIED_WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
	private static final int MODIFIED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.MODIFIED_BADLANDS_PLATEAU);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
	private static final int MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELD_SHORE);
	private static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.RIVER);
	private static final int MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.MOUNTAIN_EDGE);
	private static final int STONE_SHORE_ID = Registry.BIOME.getRawId(Biomes.STONE_SHORE);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.TAIGA);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		Biome biome = Registry.BIOME.get(m);
		if (m == MUSHROOM_FIELDS_ID) {
			if (BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(j) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(l)) {
				return MUSHROOM_FIELD_SHORE_ID;
			}
		} else if (biome != null && biome.getCategory() == Biome.Category.JUNGLE) {
			if (!isWooded(i) || !isWooded(j) || !isWooded(k) || !isWooded(l)) {
				return JUNGLE_EDGE_ID;
			}

			if (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l)) {
				return BEACH_ID;
			}
		} else if (m != MOUNTAINS_ID && m != WOODED_MOUNTAINS_ID && m != MOUNTAIN_EDGE_ID) {
			if (biome != null && biome.getPrecipitation() == Biome.Precipitation.SNOW) {
				if (!BiomeLayers.isOcean(m) && (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
					return SNOWY_BEACH_ID;
				}
			} else if (m != BADLANDS_ID && m != WOODED_BADLANDS_PLATEAU_ID) {
				if (!BiomeLayers.isOcean(m)
					&& m != RIVER_ID
					&& m != SWAMP_ID
					&& (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
					return BEACH_ID;
				}
			} else if (!BiomeLayers.isOcean(i)
				&& !BiomeLayers.isOcean(j)
				&& !BiomeLayers.isOcean(k)
				&& !BiomeLayers.isOcean(l)
				&& (!this.isBadlands(i) || !this.isBadlands(j) || !this.isBadlands(k) || !this.isBadlands(l))) {
				return DESERT_ID;
			}
		} else if (!BiomeLayers.isOcean(m) && (BiomeLayers.isOcean(i) || BiomeLayers.isOcean(j) || BiomeLayers.isOcean(k) || BiomeLayers.isOcean(l))) {
			return STONE_SHORE_ID;
		}

		return m;
	}

	private static boolean isWooded(int i) {
		return Registry.BIOME.get(i) != null && Registry.BIOME.get(i).getCategory() == Biome.Category.JUNGLE
			? true
			: i == JUNGLE_EDGE_ID || i == JUNGLE_ID || i == JUNGLE_HILLS_ID || i == FOREST_ID || i == TAIGA_ID || BiomeLayers.isOcean(i);
	}

	private boolean isBadlands(int i) {
		return i == BADLANDS_ID
			|| i == WOODED_BADLANDS_PLATEAU_ID
			|| i == BADLANDS_PLATEAU_ID
			|| i == ERODED_BADLANDS_ID
			|| i == MODIFIED_WOODED_BADLANDS_PLATEAU_ID
			|| i == MODIFIED_BADLANDS_PLATEAU_ID;
	}
}
