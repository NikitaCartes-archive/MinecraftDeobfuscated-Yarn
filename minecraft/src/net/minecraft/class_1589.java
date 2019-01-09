package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1589 extends class_1621 {
	public class_1589(class_1937 arg) {
		super(class_1299.field_6102, arg);
		this.field_5977 = true;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.2F);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return arg.method_8407() != class_1267.field_5801;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return arg.method_8606(this, this.method_5829()) && arg.method_8587(this, this.method_5829()) && !arg.method_8599(this.method_5829());
	}

	@Override
	protected void method_7161(int i, boolean bl) {
		super.method_7161(i, bl);
		this.method_5996(class_1612.field_7358).method_6192((double)(i * 3));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Override
	protected class_2394 method_7162() {
		return class_2398.field_11240;
	}

	@Override
	protected class_2960 method_5991() {
		return this.method_7157() ? class_39.field_844 : this.method_5864().method_16351();
	}

	@Override
	public boolean method_5809() {
		return false;
	}

	@Override
	protected int method_7154() {
		return super.method_7154() * 4;
	}

	@Override
	protected void method_7156() {
		this.field_7389 *= 0.9F;
	}

	@Override
	protected void method_6043() {
		this.field_5984 = (double)(0.42F + (float)this.method_7152() * 0.1F);
		this.field_6007 = true;
	}

	@Override
	protected void method_6010(class_3494<class_3611> arg) {
		if (arg == class_3486.field_15518) {
			this.field_5984 = (double)(0.22F + (float)this.method_7152() * 0.05F);
			this.field_6007 = true;
		} else {
			super.method_6010(arg);
		}
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected boolean method_7163() {
		return this.method_6034();
	}

	@Override
	protected int method_7158() {
		return super.method_7158() + 2;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_7157() ? class_3417.field_15005 : class_3417.field_14747;
	}

	@Override
	protected class_3414 method_6002() {
		return this.method_7157() ? class_3417.field_14889 : class_3417.field_14662;
	}

	@Override
	protected class_3414 method_7160() {
		return this.method_7157() ? class_3417.field_14749 : class_3417.field_14949;
	}

	@Override
	protected class_3414 method_7153() {
		return class_3417.field_14847;
	}
}
