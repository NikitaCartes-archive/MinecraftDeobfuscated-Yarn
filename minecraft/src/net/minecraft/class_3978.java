package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

public class class_3978 extends class_18 {
	private final Object2IntMap<String> field_17662 = new Object2IntOpenHashMap<>();

	public class_3978() {
		super("idcounts");
		this.field_17662.defaultReturnValue(-1);
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_17662.clear();

		for (String string : arg.method_10541()) {
			if (arg.method_10573(string, 99)) {
				this.field_17662.put(string, arg.method_10550(string));
			}
		}
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		for (Entry<String> entry : this.field_17662.object2IntEntrySet()) {
			arg.method_10569((String)entry.getKey(), entry.getIntValue());
		}

		return arg;
	}

	public int method_17920() {
		int i = this.field_17662.getInt("map") + 1;
		this.field_17662.put("map", i);
		this.method_80();
		return i;
	}
}
