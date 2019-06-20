package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1543 extends class_3763 {
	protected class_1543(class_1299<? extends class_1543> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6291;
	}

	@Environment(EnvType.CLIENT)
	public class_1543.class_1544 method_6990() {
		return class_1543.class_1544.field_7207;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1544 {
		field_7207,
		field_7211,
		field_7212,
		field_7208,
		field_7213,
		field_7210,
		field_19012;
	}

	public class class_4258 extends class_4255 {
		public class_4258(class_3763 arg2) {
			super(arg2, false);
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && class_1543.this.method_16482();
		}
	}
}
