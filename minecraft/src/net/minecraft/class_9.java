package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_9 {
	public final int field_40;
	public final int field_39;
	public final int field_38;
	private final int field_44;
	public int field_37 = -1;
	public float field_36;
	public float field_34;
	public float field_47;
	public class_9 field_35;
	public boolean field_42;
	public float field_46;
	public float field_45;
	public float field_43;
	public class_7 field_41 = class_7.field_22;

	public class_9(int i, int j, int k) {
		this.field_40 = i;
		this.field_39 = j;
		this.field_38 = k;
		this.field_44 = method_30(i, j, k);
	}

	public class_9 method_26(int i, int j, int k) {
		class_9 lv = new class_9(i, j, k);
		lv.field_37 = this.field_37;
		lv.field_36 = this.field_36;
		lv.field_34 = this.field_34;
		lv.field_47 = this.field_47;
		lv.field_35 = this.field_35;
		lv.field_42 = this.field_42;
		lv.field_46 = this.field_46;
		lv.field_45 = this.field_45;
		lv.field_43 = this.field_43;
		lv.field_41 = this.field_41;
		return lv;
	}

	public static int method_30(int i, int j, int k) {
		return j & 0xFF | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? 32768 : 0);
	}

	public float method_31(class_9 arg) {
		float f = (float)(arg.field_40 - this.field_40);
		float g = (float)(arg.field_39 - this.field_39);
		float h = (float)(arg.field_38 - this.field_38);
		return class_3532.method_15355(f * f + g * g + h * h);
	}

	public float method_32(class_9 arg) {
		float f = (float)(arg.field_40 - this.field_40);
		float g = (float)(arg.field_39 - this.field_39);
		float h = (float)(arg.field_38 - this.field_38);
		return f * f + g * g + h * h;
	}

	public float method_29(class_9 arg) {
		float f = (float)Math.abs(arg.field_40 - this.field_40);
		float g = (float)Math.abs(arg.field_39 - this.field_39);
		float h = (float)Math.abs(arg.field_38 - this.field_38);
		return f + g + h;
	}

	public boolean equals(Object object) {
		if (!(object instanceof class_9)) {
			return false;
		} else {
			class_9 lv = (class_9)object;
			return this.field_44 == lv.field_44 && this.field_40 == lv.field_40 && this.field_39 == lv.field_39 && this.field_38 == lv.field_38;
		}
	}

	public int hashCode() {
		return this.field_44;
	}

	public boolean method_27() {
		return this.field_37 >= 0;
	}

	public String toString() {
		return this.field_40 + ", " + this.field_39 + ", " + this.field_38;
	}

	@Environment(EnvType.CLIENT)
	public static class_9 method_28(class_2540 arg) {
		class_9 lv = new class_9(arg.readInt(), arg.readInt(), arg.readInt());
		lv.field_46 = arg.readFloat();
		lv.field_45 = arg.readFloat();
		lv.field_43 = arg.readFloat();
		lv.field_42 = arg.readBoolean();
		lv.field_41 = class_7.values()[arg.readInt()];
		lv.field_47 = arg.readFloat();
		return lv;
	}
}
