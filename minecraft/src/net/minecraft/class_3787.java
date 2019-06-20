package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;

public class class_3787 {
	private final Map<class_2960, class_3785> field_16688 = Maps.<class_2960, class_3785>newHashMap();

	public class_3787() {
		this.method_16640(class_3785.field_16679);
	}

	public void method_16640(class_3785 arg) {
		this.field_16688.put(arg.method_16629(), arg);
	}

	public class_3785 method_16639(class_2960 arg) {
		class_3785 lv = (class_3785)this.field_16688.get(arg);
		return lv != null ? lv : class_3785.field_16746;
	}
}
