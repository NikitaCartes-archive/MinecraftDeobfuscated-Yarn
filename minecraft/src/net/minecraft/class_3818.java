package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class class_3818 extends class_3825 {
	public static final class_3818 field_16868 = new class_3818();

	private class_3818() {
	}

	@Override
	public boolean method_16768(class_2680 arg, Random random) {
		return true;
	}

	@Override
	protected class_3827 method_16766() {
		return class_3827.field_16982;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
