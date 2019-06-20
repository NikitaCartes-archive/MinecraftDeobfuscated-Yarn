package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2617 implements class_2596<class_2602> {
	private Object2IntMap<class_3445<?>> field_12030;

	public class_2617() {
	}

	public class_2617(Object2IntMap<class_3445<?>> object2IntMap) {
		this.field_12030 = object2IntMap;
	}

	public void method_11270(class_2602 arg) {
		arg.method_11129(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		int i = arg.method_10816();
		this.field_12030 = new Object2IntOpenHashMap<>(i);

		for (int j = 0; j < i; j++) {
			this.method_11271(class_2378.field_11152.method_10200(arg.method_10816()), arg);
		}
	}

	private <T> void method_11271(class_3448<T> arg, class_2540 arg2) {
		int i = arg2.method_10816();
		int j = arg2.method_10816();
		this.field_12030.put(arg.method_14956(arg.method_14959().method_10200(i)), j);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12030.size());

		for (Entry<class_3445<?>> entry : this.field_12030.object2IntEntrySet()) {
			class_3445<?> lv = (class_3445<?>)entry.getKey();
			arg.method_10804(class_2378.field_11152.method_10249(lv.method_14949()));
			arg.method_10804(this.method_11272(lv));
			arg.method_10804(entry.getIntValue());
		}
	}

	private <T> int method_11272(class_3445<T> arg) {
		return arg.method_14949().method_14959().method_10249(arg.method_14951());
	}

	@Environment(EnvType.CLIENT)
	public Map<class_3445<?>, Integer> method_11273() {
		return this.field_12030;
	}
}
