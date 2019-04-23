package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class SetBaseBiomesLayer implements IdentitySamplingLayer {
	private static final int BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9412);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.field_9424);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.field_9472);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9409);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9433);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.field_9410);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.field_9462);
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9477);
	private static final int DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.field_9475);
	private static final int SAVANNA_ID = Registry.BIOME.getRawId(Biomes.field_9449);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.field_9471);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9420);
	private static final int SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.field_9454);
	private static final int[] OLD_GROUP_1 = new int[]{DESERT_ID, FOREST_ID, MOUNTAINS_ID, SWAMP_ID, PLAINS_ID, TAIGA_ID};
	private static final int[] DRY_BIOMES = new int[]{DESERT_ID, DESERT_ID, DESERT_ID, SAVANNA_ID, SAVANNA_ID, PLAINS_ID};
	private static final int[] TEMPERATE_BIOMES = new int[]{FOREST_ID, DARK_FOREST_ID, MOUNTAINS_ID, PLAINS_ID, BIRCH_FOREST_ID, SWAMP_ID};
	private static final int[] COOL_BIOMES = new int[]{FOREST_ID, MOUNTAINS_ID, TAIGA_ID, PLAINS_ID};
	private static final int[] SNOWY_BIOMES = new int[]{SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TAIGA_ID};
	private final OverworldChunkGeneratorConfig config;
	private int[] chosenGroup1 = DRY_BIOMES;

	public SetBaseBiomesLayer(LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		if (levelGeneratorType == LevelGeneratorType.DEFAULT_1_1) {
			this.chosenGroup1 = OLD_GROUP_1;
			this.config = null;
		} else {
			this.config = overworldChunkGeneratorConfig;
		}
	}

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		if (this.config != null && this.config.getForcedBiome() >= 0) {
			return this.config.getForcedBiome();
		} else {
			int j = (i & 3840) >> 8;
			i &= -3841;
			if (!BiomeLayers.isOcean(i) && i != MUSHROOM_FIELDS_ID) {
				switch (i) {
					case 1:
						if (j > 0) {
							return layerRandomnessSource.nextInt(3) == 0 ? BADLANDS_PLATEAU_ID : WOODED_BADLANDS_PLATEAU_ID;
						}

						return this.chosenGroup1[layerRandomnessSource.nextInt(this.chosenGroup1.length)];
					case 2:
						if (j > 0) {
							return JUNGLE_ID;
						}

						return TEMPERATE_BIOMES[layerRandomnessSource.nextInt(TEMPERATE_BIOMES.length)];
					case 3:
						if (j > 0) {
							return GIANT_TREE_TAIGA_ID;
						}

						return COOL_BIOMES[layerRandomnessSource.nextInt(COOL_BIOMES.length)];
					case 4:
						return SNOWY_BIOMES[layerRandomnessSource.nextInt(SNOWY_BIOMES.length)];
					default:
						return MUSHROOM_FIELDS_ID;
				}
			} else {
				return i;
			}
		}
	}
}
