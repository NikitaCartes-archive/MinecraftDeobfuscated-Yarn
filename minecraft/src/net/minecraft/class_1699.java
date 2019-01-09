package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1699 extends class_1688 {
	private final class_1917 field_7746 = new class_1917() {
		@Override
		public void method_8273(int i) {
			class_1699.this.field_6002.method_8421(class_1699.this, (byte)i);
		}

		@Override
		public class_1937 method_8271() {
			return class_1699.this.field_6002;
		}

		@Override
		public class_2338 method_8276() {
			return new class_2338(class_1699.this);
		}
	};

	public class_1699(class_1937 arg) {
		super(class_1299.field_6142, arg);
	}

	public class_1699(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6142, arg, d, e, f);
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7680;
	}

	@Override
	public class_2680 method_7517() {
		return class_2246.field_10260.method_9564();
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7746.method_8280(arg);
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		this.field_7746.method_8272(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		this.field_7746.method_8275(b);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		this.field_7746.method_8285();
	}

	@Override
	public boolean method_5833() {
		return true;
	}
}
