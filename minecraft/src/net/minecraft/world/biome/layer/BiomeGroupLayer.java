package net.minecraft.world.biome.layer;

public class BiomeGroupLayer {
	public static enum AddGroup2Layer implements CrossSamplingLayer {
		field_17399;

		@Override
		public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
			return m != 1 || i != 3 && j != 3 && l != 3 && k != 3 && i != 4 && j != 4 && l != 4 && k != 4 ? m : 2;
		}
	}

	public static enum AddGroup3Layer implements CrossSamplingLayer {
		field_17401;

		@Override
		public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
			return m != 4 || i != 1 && j != 1 && l != 1 && k != 1 && i != 2 && j != 2 && l != 2 && k != 2 ? m : 3;
		}
	}

	public static enum AddSpecialBiomesLayer implements IdentitySamplingLayer {
		field_16051;

		@Override
		public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
			if (!BiomeLayers.isShallowOcean(i) && layerRandomnessSource.nextInt(13) == 0) {
				i |= 1 + layerRandomnessSource.nextInt(15) << 8 & 3840;
			}

			return i;
		}
	}
}
