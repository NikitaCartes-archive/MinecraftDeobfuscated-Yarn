package net.minecraft;

import javax.annotation.Nullable;

public class class_2636 extends class_2586 implements class_3000 {
	private final class_1917 field_12114 = new class_1917() {
		@Override
		public void method_8273(int i) {
			class_2636.this.field_11863.method_8427(class_2636.this.field_11867, class_2246.field_10260, i, 0);
		}

		@Override
		public class_1937 method_8271() {
			return class_2636.this.field_11863;
		}

		@Override
		public class_2338 method_8276() {
			return class_2636.this.field_11867;
		}

		@Override
		public void method_8277(class_1952 arg) {
			super.method_8277(arg);
			if (this.method_8271() != null) {
				class_2680 lv = this.method_8271().method_8320(this.method_8276());
				this.method_8271().method_8413(class_2636.this.field_11867, lv, lv, 4);
			}
		}
	};

	public class_2636() {
		super(class_2591.field_11889);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_12114.method_8280(arg);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		this.field_12114.method_8272(arg);
		return arg;
	}

	@Override
	public void method_16896() {
		this.field_12114.method_8285();
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 1, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		class_2487 lv = this.method_11007(new class_2487());
		lv.method_10551("SpawnPotentials");
		return lv;
	}

	@Override
	public boolean method_11004(int i, int j) {
		return this.field_12114.method_8275(i) ? true : super.method_11004(i, j);
	}

	@Override
	public boolean method_11011() {
		return true;
	}

	public class_1917 method_11390() {
		return this.field_12114;
	}
}
