package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class class_4994 extends class_4995 {
	public static final class_4994 field_23343 = new class_4994();

	private class_4994() {
	}

	@Override
	public boolean method_26406(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
		return true;
	}

	@Override
	protected class_4996 method_26404() {
		return class_4996.field_23344;
	}

	@Override
	protected <T> Dynamic<T> method_26405(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
