package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_3784 {
	@Nullable
	private volatile class_3785.class_3786 field_16862 = null;

	protected class_3784(class_3785.class_3786 arg) {
		this.field_16862 = arg;
	}

	protected class_3784(Dynamic<?> dynamic) {
		this.field_16862 = class_3785.class_3786.method_16638(dynamic.get("projection").asString(class_3785.class_3786.field_16687.method_16635()));
	}

	public abstract List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random);

	public abstract class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3);

	public abstract boolean method_16626(class_3485 arg, class_1936 arg2, class_2338 arg3, class_2470 arg4, class_3341 arg5, Random random);

	public abstract class_3816 method_16757();

	public void method_16756(class_1936 arg, class_3499.class_3501 arg2, class_2338 arg3, class_2470 arg4, Random random, class_3341 arg5) {
	}

	public class_3784 method_16622(class_3785.class_3786 arg) {
		this.field_16862 = arg;
		return this;
	}

	public class_3785.class_3786 method_16624() {
		class_3785.class_3786 lv = this.field_16862;
		if (lv == null) {
			throw new IllegalStateException();
		} else {
			return lv;
		}
	}

	protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
		T object = this.method_16625(dynamicOps).getValue();
		T object2 = dynamicOps.mergeInto(
			object, dynamicOps.createString("element_type"), dynamicOps.createString(class_2378.field_16793.method_10221(this.method_16757()).toString())
		);
		return new Dynamic<>(
			dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.field_16862.method_16635()))
		);
	}

	public int method_19308() {
		return 1;
	}
}
