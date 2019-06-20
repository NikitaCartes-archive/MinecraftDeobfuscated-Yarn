package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;

public class class_1842 {
	private final String field_8954;
	private final ImmutableList<class_1293> field_8955;

	public static class_1842 method_8048(String string) {
		return class_2378.field_11143.method_10223(class_2960.method_12829(string));
	}

	public class_1842(class_1293... args) {
		this(null, args);
	}

	public class_1842(@Nullable String string, class_1293... args) {
		this.field_8954 = string;
		this.field_8955 = ImmutableList.copyOf(args);
	}

	public String method_8051(String string) {
		return string + (this.field_8954 == null ? class_2378.field_11143.method_10221(this).method_12832() : this.field_8954);
	}

	public List<class_1293> method_8049() {
		return this.field_8955;
	}

	public boolean method_8050() {
		if (!this.field_8955.isEmpty()) {
			for (class_1293 lv : this.field_8955) {
				if (lv.method_5579().method_5561()) {
					return true;
				}
			}
		}

		return false;
	}
}
