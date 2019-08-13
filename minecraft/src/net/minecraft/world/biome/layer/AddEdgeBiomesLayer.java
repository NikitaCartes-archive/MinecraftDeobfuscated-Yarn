package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public enum AddEdgeBiomesLayer implements CrossSamplingLayer {
	field_16184;

	private static final int BEACH_ID = Registry.BIOME.getRawId(Biomes.field_9434);
	private static final int SNOWY_BEACH_ID = Registry.BIOME.getRawId(Biomes.field_9478);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9460);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9409);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.field_9474);
	private static final int JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9432);
	private static final int BADLANDS_ID = Registry.BIOME.getRawId(Biomes.field_9415);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9433);
	private static final int ERODED_BADLANDS_ID = Registry.BIOME.getRawId(Biomes.field_9443);
	private static final int MODIFIED_WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9413);
	private static final int MODIFIED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9406);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.field_9462);
	private static final int MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.field_9407);
	private static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.field_9438);
	private static final int MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.field_9464);
	private static final int STONE_SHORE_ID = Registry.BIOME.getRawId(Biomes.field_9419);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.field_9471);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9420);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		Biome biome = Registry.BIOME.get(m);
		if (m == MUSHROOM_FIELDS_ID) {
			if (BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(j) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(l)) {
				return MUSHROOM_FIELD_SHORE_ID;
			}
		} else if (biome != null && biome.getCategory() == Biome.Category.field_9358) {
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
		return Registry.BIOME.get(i) != null && Registry.BIOME.get(i).getCategory() == Biome.Category.field_9358
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
