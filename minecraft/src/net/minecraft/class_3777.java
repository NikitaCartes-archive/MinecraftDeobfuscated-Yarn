package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class class_3777 extends class_3784 {
	public static final class_3777 field_16663 = new class_3777();

	private class_3777() {
		super(class_3785.class_3786.field_16686);
	}

	@Override
	public List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random) {
		return Collections.emptyList();
	}

	@Override
	public class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3) {
		return class_3341.method_14665();
	}

	@Override
	public boolean method_16626(class_3485 arg, class_1936 arg2, class_2338 arg3, class_2470 arg4, class_3341 arg5, Random random) {
		return true;
	}

	@Override
	public class_3816 method_16757() {
		return class_3816.field_16972;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}

	public String toString() {
		return "Empty";
	}
}
