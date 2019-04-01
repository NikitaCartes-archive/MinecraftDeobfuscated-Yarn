package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class class_1327 extends class_1325 {
	private final Set<class_1324> field_6342 = Sets.<class_1324>newHashSet();
	protected final Map<String, class_1324> field_6341 = new class_3522();

	public class_1328 method_6216(class_1320 arg) {
		return (class_1328)super.method_6205(arg);
	}

	public class_1328 method_6214(String string) {
		class_1324 lv = super.method_6207(string);
		if (lv == null) {
			lv = (class_1324)this.field_6341.get(string);
		}

		return (class_1328)lv;
	}

	@Override
	public class_1324 method_6208(class_1320 arg) {
		class_1324 lv = super.method_6208(arg);
		if (arg instanceof class_1329 && ((class_1329)arg).method_6221() != null) {
			this.field_6341.put(((class_1329)arg).method_6221(), lv);
		}

		return lv;
	}

	@Override
	protected class_1324 method_6206(class_1320 arg) {
		return new class_1328(this, arg);
	}

	@Override
	public void method_6211(class_1324 arg) {
		if (arg.method_6198().method_6168()) {
			this.field_6342.add(arg);
		}

		for (class_1320 lv : this.field_6336.get(arg.method_6198())) {
			class_1328 lv2 = this.method_6216(lv);
			if (lv2 != null) {
				lv2.method_6217();
			}
		}
	}

	public Set<class_1324> method_6215() {
		return this.field_6342;
	}

	public Collection<class_1324> method_6213() {
		Set<class_1324> set = Sets.<class_1324>newHashSet();

		for (class_1324 lv : this.method_6204()) {
			if (lv.method_6198().method_6168()) {
				set.add(lv);
			}
		}

		return set;
	}
}
