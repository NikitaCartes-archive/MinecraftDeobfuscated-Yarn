package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer, NorthWestCoordinateTransformer {
	field_16134;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9412);
	private static final int BIRCH_FOREST_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9421);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int DESERT_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9466);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9460);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9409);
	private static final int WOODED_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9459);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int SNOWY_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9444);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9432);
	private static final int BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9440);
	private static final int BAMBOO_JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9468);
	private static final int BADLANDS_ID = Registry.BIOME.getRawId(Biomes.field_9415);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.biome);
	private static final int GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9477);
	private static final int GIANT_TREE_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9429);
	private static final int DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9475);
	private static final int SAVANNA_ID = Registry.BIOME.getRawId(Biomes.field_9449);
	private static final int SAVANNA_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9430);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9420);
	private static final int SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9454);
	private static final int SNOWY_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9425);
	private static final int TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.field_9428);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
		int k = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
		int l = layerSampler2.sample(this.transformX(i + 1), this.transformZ(j + 1));
		if (k > 255) {
			LOGGER.debug("old! {}", k);
		}

		int m = (l - 2) % 29;
		if (!BiomeLayers.isShallowOcean(k) && l >= 2 && m == 1) {
			Biome biome = Registry.BIOME.getInt(k);
			if (biome == null || !biome.hasParent()) {
				Biome biome2 = Biome.getParentBiome(biome);
				return biome2 == null ? k : Registry.BIOME.getRawId(biome2);
			}
		}

		if (layerRandomnessSource.nextInt(3) == 0 || m == 0) {
			int n = k;
			if (k == DESERT_ID) {
				n = DESERT_HILLS_ID;
			} else if (k == FOREST_ID) {
				n = WOODED_HILLS_ID;
			} else if (k == BIRCH_FOREST_ID) {
				n = BIRCH_FOREST_HILLS_ID;
			} else if (k == DARK_FOREST_ID) {
				n = PLAINS_ID;
			} else if (k == TAIGA_ID) {
				n = TAIGA_HILLS_ID;
			} else if (k == GIANT_TREE_TAIGA_ID) {
				n = GIANT_TREE_TAIGA_HILLS_ID;
			} else if (k == SNOWY_TAIGA_ID) {
				n = SNOWY_TAIGA_HILLS_ID;
			} else if (k == PLAINS_ID) {
				n = layerRandomnessSource.nextInt(3) == 0 ? WOODED_HILLS_ID : FOREST_ID;
			} else if (k == SNOWY_TUNDRA_ID) {
				n = SNOWY_MOUNTAINS_ID;
			} else if (k == JUNGLE_ID) {
				n = JUNGLE_HILLS_ID;
			} else if (k == BAMBOO_JUNGLE_ID) {
				n = BAMBOO_JUNGLE_HILLS_ID;
			} else if (k == BiomeLayers.OCEAN_ID) {
				n = BiomeLayers.DEEP_OCEAN_ID;
			} else if (k == BiomeLayers.LUKEWARM_OCEAN_ID) {
				n = BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
			} else if (k == BiomeLayers.COLD_OCEAN_ID) {
				n = BiomeLayers.DEEP_COLD_OCEAN_ID;
			} else if (k == BiomeLayers.FROZEN_OCEAN_ID) {
				n = BiomeLayers.DEEP_FROZEN_OCEAN_ID;
			} else if (k == MOUNTAINS_ID) {
				n = WOODED_MOUNTAINS_ID;
			} else if (k == SAVANNA_ID) {
				n = SAVANNA_PLATEAU_ID;
			} else if (BiomeLayers.areSimilar(k, WOODED_BADLANDS_PLATEAU_ID)) {
				n = BADLANDS_ID;
			} else if ((
					k == BiomeLayers.DEEP_OCEAN_ID || k == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || k == BiomeLayers.DEEP_COLD_OCEAN_ID || k == BiomeLayers.DEEP_FROZEN_OCEAN_ID
				)
				&& layerRandomnessSource.nextInt(3) == 0) {
				n = layerRandomnessSource.nextInt(2) == 0 ? PLAINS_ID : FOREST_ID;
			}

			if (m == 0 && n != k) {
				Biome biome2 = Biome.getParentBiome(Registry.BIOME.getInt(n));
				n = biome2 == null ? k : Registry.BIOME.getRawId(biome2);
			}

			if (n != k) {
				int o = 0;
				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 0)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 1)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 1)), k)) {
					o++;
				}

				if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 2)), k)) {
					o++;
				}

				if (o >= 3) {
					return n;
				}
			}
		}

		return k;
	}
}
