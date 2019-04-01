package net.minecraft;

public class class_3201 extends class_3225 {
	private boolean field_13890;
	private boolean field_13889;
	private int field_13888;
	private int field_13887;

	public class_3201(class_3218 arg) {
		super(arg);
	}

	@Override
	public void method_14264() {
		super.method_14264();
		this.field_13887++;
		long l = this.field_14007.method_8510();
		long m = l / 24000L + 1L;
		if (!this.field_13890 && this.field_13887 > 20) {
			this.field_13890 = true;
			this.field_14008.field_13987.method_14364(new class_2668(5, 0.0F));
		}

		this.field_13889 = l > 120500L;
		if (this.field_13889) {
			this.field_13888++;
		}

		if (l % 24000L == 500L) {
			if (m <= 6L) {
				if (m == 6L) {
					this.field_14008.field_13987.method_14364(new class_2668(5, 104.0F));
				} else {
					this.field_14008.method_9203(new class_2588("demo.day." + m));
				}
			}
		} else if (m == 5L && l % 24000L == 22000L) {
			this.field_14008.method_9203(new class_2588("demo.day.warning"));
		}
	}

	private void method_14031() {
		if (this.field_13888 > 100) {
			this.field_14008.method_9203(new class_2588("demo.reminder"));
			this.field_13888 = 0;
		}
	}

	@Override
	public void method_14263(class_2338 arg, class_2350 arg2) {
		if (this.field_13889) {
			this.method_14031();
		} else {
			super.method_14263(arg, arg2);
		}
	}

	@Override
	public void method_14258(class_2338 arg) {
		if (!this.field_13889) {
			super.method_14258(arg);
		}
	}

	@Override
	public boolean method_14266(class_2338 arg) {
		return this.field_13889 ? false : super.method_14266(arg);
	}

	@Override
	public class_1269 method_14256(class_1657 arg, class_1937 arg2, class_1799 arg3, class_1268 arg4) {
		if (this.field_13889) {
			this.method_14031();
			return class_1269.field_5811;
		} else {
			return super.method_14256(arg, arg2, arg3, arg4);
		}
	}

	@Override
	public class_1269 method_14262(class_1657 arg, class_1937 arg2, class_1799 arg3, class_1268 arg4, class_3965 arg5) {
		if (this.field_13889) {
			this.method_14031();
			return class_1269.field_5811;
		} else {
			return super.method_14262(arg, arg2, arg3, arg4, arg5);
		}
	}
}
