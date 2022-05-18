package net.minecraft.block;

import net.minecraft.util.math.random.Random;

public class VineLogic {
	private static final double field_31198 = 0.826;
	public static final double field_31197 = 0.1;

	public static boolean isValidForWeepingStem(BlockState state) {
		return state.isAir();
	}

	public static int getGrowthLength(Random random) {
		double d = 1.0;

		int i;
		for (i = 0; random.nextDouble() < d; i++) {
			d *= 0.826;
		}

		return i;
	}
}
