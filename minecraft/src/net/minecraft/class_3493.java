package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;

public class class_3493<T> extends class_3503<T> {
	private final class_2378<T> field_15577;

	public class_3493(class_2378<T> arg, String string, String string2) {
		super(arg::method_10250, arg::method_10223, string, false, string2);
		this.field_15577 = arg;
	}

	public void method_15137(class_2540 arg) {
		arg.method_10804(this.method_15196().size());

		for (Entry<class_2960, class_3494<T>> entry : this.method_15196().entrySet()) {
			arg.method_10812((class_2960)entry.getKey());
			arg.method_10804(((class_3494)entry.getValue()).method_15138().size());

			for (T object : ((class_3494)entry.getValue()).method_15138()) {
				arg.method_10804(this.field_15577.method_10249(object));
			}
		}
	}

	public void method_15136(class_2540 arg) {
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			class_2960 lv = arg.method_10810();
			int k = arg.method_10816();
			List<T> list = Lists.<T>newArrayList();

			for (int l = 0; l < k; l++) {
				list.add(this.field_15577.method_10200(arg.method_10816()));
			}

			this.method_15196().put(lv, class_3494.class_3495.method_15146().method_15151(list).method_15144(lv));
		}
	}
}
