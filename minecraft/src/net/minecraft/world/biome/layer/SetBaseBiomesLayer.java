package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SetBaseBiomesLayer implements IdentitySamplingLayer {
	private static final int[] OLD_GROUP_1 = new int[]{2, 4, 3, 6, 1, 5};
	private static final int[] DRY_BIOMES = new int[]{2, 2, 2, 35, 35, 1};
	private static final int[] TEMPERATE_BIOMES = new int[]{4, 29, 3, 1, 27, 6};
	private static final int[] COOL_BIOMES = new int[]{4, 3, 5, 1};
	private static final int[] SNOWY_BIOMES = new int[]{12, 12, 12, 30};
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
		if (!BiomeLayers.isOcean(value) && value != 14) {
			switch (value) {
				case 1:
					if (i > 0) {
						return context.nextInt(3) == 0 ? 39 : 38;
					}

					return this.chosenGroup1[context.nextInt(this.chosenGroup1.length)];
				case 2:
					if (i > 0) {
						return 21;
					}

					return TEMPERATE_BIOMES[context.nextInt(TEMPERATE_BIOMES.length)];
				case 3:
					if (i > 0) {
						return 32;
					}

					return COOL_BIOMES[context.nextInt(COOL_BIOMES.length)];
				case 4:
					return SNOWY_BIOMES[context.nextInt(SNOWY_BIOMES.length)];
				default:
					return 14;
			}
		} else {
			return value;
		}
	}
}
