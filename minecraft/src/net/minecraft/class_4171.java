package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4171 {
	private final class_4170 field_18608;
	private final List<class_4171.class_4172> field_18609 = Lists.<class_4171.class_4172>newArrayList();

	public class_4171(class_4170 arg) {
		this.field_18608 = arg;
	}

	public class_4171 method_19221(int i, class_4168 arg) {
		this.field_18609.add(new class_4171.class_4172(i, arg));
		return this;
	}

	public class_4170 method_19220() {
		((Set)this.field_18609.stream().map(class_4171.class_4172::method_19225).collect(Collectors.toSet())).forEach(this.field_18608::method_19215);
		this.field_18609.forEach(arg -> {
			class_4168 lv = arg.method_19225();
			this.field_18608.method_19219(lv).forEach(arg2 -> arg2.method_19227(arg.method_19224(), 0.0F));
			this.field_18608.method_19218(lv).method_19227(arg.method_19224(), 1.0F);
		});
		return this.field_18608;
	}

	static class class_4172 {
		private final int field_18610;
		private final class_4168 field_18611;

		public class_4172(int i, class_4168 arg) {
			this.field_18610 = i;
			this.field_18611 = arg;
		}

		public int method_19224() {
			return this.field_18610;
		}

		public class_4168 method_19225() {
			return this.field_18611;
		}
	}
}
