package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;

public abstract class class_3784 {
	private class_3785.class_3786 field_16862 = class_3785.class_3786.field_16687;

	public abstract List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random);

	public abstract class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3);

	public abstract boolean method_16626(class_1936 arg, class_2338 arg2, class_2470 arg3, class_3341 arg4, Random random);

	public abstract class_3816 method_16757();

	public void method_16756(class_1936 arg, class_3499.class_3501 arg2, class_2338 arg3, class_2470 arg4, Random random, class_3341 arg5) {
	}

	public class_3784 method_16622(class_3785.class_3786 arg) {
		this.field_16862 = arg;
		return this;
	}

	public class_3785.class_3786 method_16624() {
		return this.field_16862;
	}

	protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16625(dynamicOps).getValue(),
				dynamicOps.createString("element_type"),
				dynamicOps.createString(class_2378.field_16793.method_10221(this.method_16757()).toString())
			)
		);
	}
}
