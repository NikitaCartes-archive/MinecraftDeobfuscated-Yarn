package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public class class_3632 {
	public static enum class_3633 implements class_3663 {
		field_16046;

		@Override
		public int sample(class_3630 arg, int i, int j, int k, int l, int m) {
			return m != 1 || i != 3 && j != 3 && l != 3 && k != 3 && i != 4 && j != 4 && l != 4 && k != 4 ? m : 2;
		}
	}

	public static enum class_3634 implements class_3663 {
		field_16049;

		@Override
		public int sample(class_3630 arg, int i, int j, int k, int l, int m) {
			return m != 4 || i != 1 && j != 1 && l != 1 && k != 1 && i != 2 && j != 2 && l != 2 && k != 2 ? m : 3;
		}
	}

	public static enum class_3635 implements class_3661 {
		field_16051;

		@Override
		public int method_15866(class_3630 arg, int i) {
			if (!BiomeLayers.isShallowOcean(i) && arg.nextInt(13) == 0) {
				i |= 1 + arg.nextInt(15) << 8 & 3840;
			}

			return i;
		}
	}
}
