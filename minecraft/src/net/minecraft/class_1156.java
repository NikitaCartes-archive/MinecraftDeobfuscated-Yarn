package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1156 {
	private final class_310 field_5645;
	@Nullable
	private class_1155 field_5646;

	public class_1156(class_310 arg) {
		this.field_5645 = arg;
	}

	public void method_4909(class_744 arg) {
		if (this.field_5646 != null) {
			this.field_5646.method_4903(arg);
		}
	}

	public void method_4908(double d, double e) {
		if (this.field_5646 != null) {
			this.field_5646.method_4901(d, e);
		}
	}

	public void method_4911(@Nullable class_638 arg, @Nullable class_239 arg2) {
		if (this.field_5646 != null && arg2 != null && arg != null) {
			this.field_5646.method_4898(arg, arg2);
		}
	}

	public void method_4907(class_638 arg, class_2338 arg2, class_2680 arg3, float f) {
		if (this.field_5646 != null) {
			this.field_5646.method_4900(arg, arg2, arg3, f);
		}
	}

	public void method_4912() {
		if (this.field_5646 != null) {
			this.field_5646.method_4904();
		}
	}

	public void method_4906(class_1799 arg) {
		if (this.field_5646 != null) {
			this.field_5646.method_4897(arg);
		}
	}

	public void method_4915() {
		if (this.field_5646 != null) {
			this.field_5646.method_4902();
			this.field_5646 = null;
		}
	}

	public void method_4916() {
		if (this.field_5646 != null) {
			this.method_4915();
		}

		this.field_5646 = this.field_5645.field_1690.field_1875.method_4918(this);
	}

	public void method_4917() {
		if (this.field_5646 != null) {
			if (this.field_5645.field_1687 != null) {
				this.field_5646.method_4899();
			} else {
				this.method_4915();
			}
		} else if (this.field_5645.field_1687 != null) {
			this.method_4916();
		}
	}

	public void method_4910(class_1157 arg) {
		this.field_5645.field_1690.field_1875 = arg;
		this.field_5645.field_1690.method_1640();
		if (this.field_5646 != null) {
			this.field_5646.method_4902();
			this.field_5646 = arg.method_4918(this);
		}
	}

	public class_310 method_4914() {
		return this.field_5645;
	}

	public class_1934 method_4905() {
		return this.field_5645.field_1761 == null ? class_1934.field_9218 : this.field_5645.field_1761.method_2920();
	}

	public static class_2561 method_4913(String string) {
		return new class_2572("key." + string).method_10854(class_124.field_1067);
	}
}
