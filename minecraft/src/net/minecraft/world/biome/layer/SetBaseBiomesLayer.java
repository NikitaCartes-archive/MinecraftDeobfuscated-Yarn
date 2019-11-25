package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.level.LevelGeneratorType;

public class SetBaseBiomesLayer implements IdentitySamplingLayer {
	private static final int BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.MOUNTAINS);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
	private static final int DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.DARK_FOREST);
	private static final int SAVANNA_ID = Registry.BIOME.getRawId(Biomes.SAVANNA);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.TAIGA);
	private static final int SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TAIGA);
	private static final int[] OLD_GROUP_1 = new int[]{DESERT_ID, FOREST_ID, MOUNTAINS_ID, SWAMP_ID, PLAINS_ID, TAIGA_ID};
	private static final int[] DRY_BIOMES = new int[]{DESERT_ID, DESERT_ID, DESERT_ID, SAVANNA_ID, SAVANNA_ID, PLAINS_ID};
	private static final int[] TEMPERATE_BIOMES = new int[]{FOREST_ID, DARK_FOREST_ID, MOUNTAINS_ID, PLAINS_ID, BIRCH_FOREST_ID, SWAMP_ID};
	private static final int[] COOL_BIOMES = new int[]{FOREST_ID, MOUNTAINS_ID, TAIGA_ID, PLAINS_ID};
	private static final int[] SNOWY_BIOMES = new int[]{SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TAIGA_ID};
	private final int field_20621;
	private int[] chosenGroup1 = DRY_BIOMES;

	public SetBaseBiomesLayer(LevelGeneratorType generatorType, int i) {
		if (generatorType == LevelGeneratorType.DEFAULT_1_1) {
			this.chosenGroup1 = OLD_GROUP_1;
			this.field_20621 = -1;
		} else {
			this.field_20621 = i;
		}
	}

	@Override
	public int sample(LayerRandomnessSource context, int value) {
		if (this.field_20621 >= 0) {
			return this.field_20621;
		} else {
			int i = (value & 3840) >> 8;
			value &= -3841;
			if (!BiomeLayers.isOcean(value) && value != MUSHROOM_FIELDS_ID) {
				switch (value) {
					case 1:
						if (i > 0) {
							return context.nextInt(3) == 0 ? BADLANDS_PLATEAU_ID : WOODED_BADLANDS_PLATEAU_ID;
						}

						return this.chosenGroup1[context.nextInt(this.chosenGroup1.length)];
					case 2:
						if (i > 0) {
							return JUNGLE_ID;
						}

						return TEMPERATE_BIOMES[context.nextInt(TEMPERATE_BIOMES.length)];
					case 3:
						if (i > 0) {
							return GIANT_TREE_TAIGA_ID;
						}

						return COOL_BIOMES[context.nextInt(COOL_BIOMES.length)];
					case 4:
						return SNOWY_BIOMES[context.nextInt(SNOWY_BIOMES.length)];
					default:
						return MUSHROOM_FIELDS_ID;
				}
			} else {
				return value;
			}
		}
	}
}
