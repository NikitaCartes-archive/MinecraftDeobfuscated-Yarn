package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;

public class class_3787 {
	private final Map<Identifier, class_3785> field_16688 = Maps.<Identifier, class_3785>newHashMap();

	public class_3787() {
		this.method_16640(class_3785.field_16679);
	}

	public void method_16640(class_3785 arg) {
		this.field_16688.put(arg.method_16629(), arg);
	}

	public class_3785 method_16639(Identifier identifier) {
		class_3785 lv = (class_3785)this.field_16688.get(identifier);
		return lv != null ? lv : class_3785.field_16746;
	}
}
