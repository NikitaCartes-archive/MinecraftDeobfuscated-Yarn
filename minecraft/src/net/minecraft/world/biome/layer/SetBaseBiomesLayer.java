package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SetBaseBiomesLayer implements IdentitySamplingLayer {
	private static final int BIRCH_FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BIRCH_FOREST);
	private static final int DESERT_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DESERT);
	private static final int MOUNTAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MOUNTAINS);
	private static final int FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FOREST);
	private static final int SNOWY_TUNDRA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
	private static final int JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE);
	private static final int BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int WOODED_BADLANDS_PLATEAU_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int MUSHROOM_FIELDS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
	private static final int PLAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.PLAINS);
	private static final int GIANT_TREE_TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
	private static final int DARK_FOREST_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DARK_FOREST);
	private static final int SAVANNA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SAVANNA);
	private static final int SWAMP_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SWAMP);
	private static final int TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.TAIGA);
	private static final int SNOWY_TAIGA_ID = BuiltinRegistries.BIOME.getRawId(Biomes.SNOWY_TAIGA);
	private static final int[] OLD_GROUP_1 = new int[]{DESERT_ID, FOREST_ID, MOUNTAINS_ID, SWAMP_ID, PLAINS_ID, TAIGA_ID};
	private static final int[] DRY_BIOMES = new int[]{DESERT_ID, DESERT_ID, DESERT_ID, SAVANNA_ID, SAVANNA_ID, PLAINS_ID};
	private static final int[] TEMPERATE_BIOMES = new int[]{FOREST_ID, DARK_FOREST_ID, MOUNTAINS_ID, PLAINS_ID, BIRCH_FOREST_ID, SWAMP_ID};
	private static final int[] COOL_BIOMES = new int[]{FOREST_ID, MOUNTAINS_ID, TAIGA_ID, PLAINS_ID};
	private static final int[] SNOWY_BIOMES = new int[]{SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TAIGA_ID};
	private int[] chosenGroup1 = DRY_BIOMES;

	public SetBaseBiomesLayer(boolean old) {
		if (old) {
			this.chosenGroup1 = OLD_GROUP_1;
		}
	}

	@Override
	public int sample(LayerRandomnessSource context, int value) {
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
