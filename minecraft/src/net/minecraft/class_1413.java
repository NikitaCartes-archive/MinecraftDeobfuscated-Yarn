package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;

public class class_1413 {
	private final class_1308 field_6691;
	private final List<class_1297> field_6692 = Lists.<class_1297>newArrayList();
	private final List<class_1297> field_6690 = Lists.<class_1297>newArrayList();

	public class_1413(class_1308 arg) {
		this.field_6691 = arg;
	}

	public void method_6370() {
		this.field_6692.clear();
		this.field_6690.clear();
	}

	public boolean method_6369(class_1297 arg) {
		if (this.field_6692.contains(arg)) {
			return true;
		} else if (this.field_6690.contains(arg)) {
			return false;
		} else {
			this.field_6691.field_6002.method_16107().method_15396("canSee");
			boolean bl = this.field_6691.method_6057(arg);
			this.field_6691.field_6002.method_16107().method_15407();
			if (bl) {
				this.field_6692.add(arg);
			} else {
				this.field_6690.add(arg);
			}

			return bl;
		}
	}
}
