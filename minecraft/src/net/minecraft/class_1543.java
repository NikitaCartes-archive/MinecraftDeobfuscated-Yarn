package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1543 extends class_3763 {
	protected static final class_2940<Byte> field_7206 = class_2945.method_12791(class_1543.class, class_2943.field_13319);

	protected class_1543(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7206, (byte)0);
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_6991(int i) {
		int j = this.field_6011.method_12789(field_7206);
		return (j & i) != 0;
	}

	protected void method_6992(int i, boolean bl) {
		int j = this.field_6011.method_12789(field_7206);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.field_6011.method_12778(field_7206, (byte)(j & 0xFF));
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
		field_7210;
	}
}
