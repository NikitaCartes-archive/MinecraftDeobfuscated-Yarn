package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3469 {
	protected final Object2IntMap<class_3445<?>> field_15431 = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

	public class_3469() {
		this.field_15431.defaultReturnValue(0);
	}

	public void method_15022(class_1657 arg, class_3445<?> arg2, int i) {
		this.method_15023(arg, arg2, this.method_15025(arg2) + i);
	}

	public void method_15023(class_1657 arg, class_3445<?> arg2, int i) {
		this.field_15431.put(arg2, i);
	}

	@Environment(EnvType.CLIENT)
	public <T> int method_15024(class_3448<T> arg, T object) {
		return arg.method_14958(object) ? this.method_15025(arg.method_14956(object)) : 0;
	}

	public int method_15025(class_3445<?> arg) {
		return this.field_15431.getInt(arg);
	}
}
