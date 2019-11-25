package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

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
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		Biome biome = Registry.BIOME.get(center);
		if (center == MUSHROOM_FIELDS_ID) {
			if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
				return MUSHROOM_FIELD_SHORE_ID;
			}
		} else if (biome != null && biome.getCategory() == Biome.Category.JUNGLE) {
			if (!isWooded(n) || !isWooded(e) || !isWooded(s) || !isWooded(w)) {
				return JUNGLE_EDGE_ID;
			}

			if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
				return BEACH_ID;
			}
		} else if (center != MOUNTAINS_ID && center != WOODED_MOUNTAINS_ID && center != MOUNTAIN_EDGE_ID) {
			if (biome != null && biome.getPrecipitation() == Biome.Precipitation.SNOW) {
				if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return SNOWY_BEACH_ID;
				}
			} else if (center != BADLANDS_ID && center != WOODED_BADLANDS_PLATEAU_ID) {
				if (!BiomeLayers.isOcean(center)
					&& center != RIVER_ID
					&& center != SWAMP_ID
					&& (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
					return BEACH_ID;
				}
			} else if (!BiomeLayers.isOcean(n)
				&& !BiomeLayers.isOcean(e)
				&& !BiomeLayers.isOcean(s)
				&& !BiomeLayers.isOcean(w)
				&& (!this.isBadlands(n) || !this.isBadlands(e) || !this.isBadlands(s) || !this.isBadlands(w))) {
				return DESERT_ID;
			}
		} else if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
			return STONE_SHORE_ID;
		}

		return center;
	}

	private static boolean isWooded(int id) {
		return Registry.BIOME.get(id) != null && Registry.BIOME.get(id).getCategory() == Biome.Category.JUNGLE
			? true
			: id == JUNGLE_EDGE_ID || id == JUNGLE_ID || id == JUNGLE_HILLS_ID || id == FOREST_ID || id == TAIGA_ID || BiomeLayers.isOcean(id);
	}

	private boolean isBadlands(int id) {
		return id == BADLANDS_ID
			|| id == WOODED_BADLANDS_PLATEAU_ID
			|| id == BADLANDS_PLATEAU_ID
			|| id == ERODED_BADLANDS_ID
			|| id == MODIFIED_WOODED_BADLANDS_PLATEAU_ID
			|| id == MODIFIED_BADLANDS_PLATEAU_ID;
	}
}
