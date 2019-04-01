package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2788 implements class_2596<class_2602> {
	private List<class_1860<?>> field_12751;

	public class_2788() {
	}

	public class_2788(Collection<class_1860<?>> collection) {
		this.field_12751 = Lists.<class_1860<?>>newArrayList(collection);
	}

	public void method_11997(class_2602 arg) {
		arg.method_11106(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12751 = Lists.<class_1860<?>>newArrayList();
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			this.field_12751.add(method_17817(arg));
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12751.size());

		for (class_1860<?> lv : this.field_12751) {
			method_17816(lv, arg);
		}
	}

	@Environment(EnvType.CLIENT)
	public List<class_1860<?>> method_11998() {
		return this.field_12751;
	}

	public static class_1860<?> method_17817(class_2540 arg) {
		class_2960 lv = arg.method_10810();
		class_2960 lv2 = arg.method_10810();
		return ((class_1865)class_2378.field_17598.method_17966(lv).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + lv)))
			.method_8122(lv2, arg);
	}

	public static <T extends class_1860<?>> void method_17816(T arg, class_2540 arg2) {
		arg2.method_10812(class_2378.field_17598.method_10221(arg.method_8119()));
		arg2.method_10812(arg.method_8114());
		((class_1865<T>)arg.method_8119()).method_8124(arg2, arg);
	}
}
