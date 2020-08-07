package net.minecraft.block;

import java.util.Random;

public class VineLogic {
	public static boolean isValidForWeepingStem(BlockState state) {
		return state.isAir();
	}

	public static int method_26381(Random random) {
		double d = 1.0;

		int i;
		for (i = 0; random.nextDouble() < d; i++) {
			d *= 0.826;
		}

		return i;
	}
}
