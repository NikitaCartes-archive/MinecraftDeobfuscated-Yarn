package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.LightingView;

public interface class_3562 extends LightingView {
	@Nullable
	class_2804 method_15544(int i, int j, int k);

	int getLightLevel(BlockPos blockPos);

	public static enum class_3563 implements class_3562 {
		field_15812;

		@Nullable
		@Override
		public class_2804 method_15544(int i, int j, int k) {
			return null;
		}

		@Override
		public int getLightLevel(BlockPos blockPos) {
			return 0;
		}

		@Override
		public void method_15551(int i, int j, int k, boolean bl) {
		}
	}
}
