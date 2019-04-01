package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1439 extends class_1427 {
	protected static final class_2940<Byte> field_6763 = class_2945.method_12791(class_1439.class, class_2943.field_13319);
	private int field_6762;
	private int field_6759;

	public class_1439(class_1299<? extends class_1439> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6013 = 1.0F;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1366(this, 1.0, true));
		this.field_6201.method_6277(2, new class_1369(this, 0.9, 32.0F));
		this.field_6201.method_6277(3, new class_1368(this, 0.6, false, 4, () -> false));
		this.field_6201.method_6277(5, new class_1372(this));
		this.field_6201.method_6277(6, new class_1394(this, 0.6));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6185.method_6277(1, new class_1397(this));
		this.field_6185.method_6277(2, new class_1399(this));
		this.field_6185.method_6277(3, new class_1400(this, class_1308.class, 5, false, false, arg -> arg instanceof class_1569 && !(arg instanceof class_1548)));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6763, (byte)0);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(100.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
		this.method_5996(class_1612.field_7360).method_6192(1.0);
	}

	@Override
	protected int method_6130(int i) {
		return i;
	}

	@Override
	protected void method_6087(class_1297 arg) {
		if (arg instanceof class_1569 && !(arg instanceof class_1548) && this.method_6051().nextInt(20) == 0) {
			this.method_5980((class_1309)arg);
		}

		super.method_6087(arg);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.field_6762 > 0) {
			this.field_6762--;
		}

		if (this.field_6759 > 0) {
			this.field_6759--;
		}

		if (method_17996(this.method_18798()) > 2.5000003E-7F && this.field_5974.nextInt(5) == 0) {
			int i = class_3532.method_15357(this.field_5987);
			int j = class_3532.method_15357(this.field_6010 - 0.2F);
			int k = class_3532.method_15357(this.field_6035);
			class_2680 lv = this.field_6002.method_8320(new class_2338(i, j, k));
			if (!lv.method_11588()) {
				this.field_6002
					.method_8406(
						new class_2388(class_2398.field_11217, lv),
						this.field_5987 + ((double)this.field_5974.nextFloat() - 0.5) * (double)this.method_17681(),
						this.method_5829().field_1322 + 0.1,
						this.field_6035 + ((double)this.field_5974.nextFloat() - 0.5) * (double)this.method_17681(),
						4.0 * ((double)this.field_5974.nextFloat() - 0.5),
						0.5,
						((double)this.field_5974.nextFloat() - 0.5) * 4.0
					);
			}
		}
	}

	@Override
	public boolean method_5973(class_1299<?> arg) {
		if (this.method_6496() && arg == class_1299.field_6097) {
			return false;
		} else {
			return arg == class_1299.field_6046 ? false : super.method_5973(arg);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("PlayerCreated", this.method_6496());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6499(arg.method_10577("PlayerCreated"));
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		this.field_6762 = 10;
		this.field_6002.method_8421(this, (byte)4);
		boolean bl = arg.method_5643(class_1282.method_5511(this), (float)(7 + this.field_5974.nextInt(15)));
		if (bl) {
			arg.method_18799(arg.method_18798().method_1031(0.0, 0.4F, 0.0));
			this.method_5723(this, arg);
		}

		this.method_5783(class_3417.field_14649, 1.0F, 1.0F);
		return bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 4) {
			this.field_6762 = 10;
			this.method_5783(class_3417.field_14649, 1.0F, 1.0F);
		} else if (b == 11) {
			this.field_6759 = 400;
		} else if (b == 34) {
			this.field_6759 = 0;
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_6501() {
		return this.field_6762;
	}

	public void method_6497(boolean bl) {
		if (bl) {
			this.field_6759 = 400;
			this.field_6002.method_8421(this, (byte)11);
		} else {
			this.field_6759 = 0;
			this.field_6002.method_8421(this, (byte)34);
		}
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14959;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15055;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_15233, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public int method_6502() {
		return this.field_6759;
	}

	public boolean method_6496() {
		return (this.field_6011.method_12789(field_6763) & 1) != 0;
	}

	public void method_6499(boolean bl) {
		byte b = this.field_6011.method_12789(field_6763);
		if (bl) {
			this.field_6011.method_12778(field_6763, (byte)(b | 1));
		} else {
			this.field_6011.method_12778(field_6763, (byte)(b & -2));
		}
	}

	@Override
	public void method_6078(class_1282 arg) {
		super.method_6078(arg);
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		class_2338 lv = new class_2338(this);
		class_2338 lv2 = lv.method_10074();
		class_2680 lv3 = arg.method_8320(lv2);
		if (!lv3.method_11631(arg, lv2, this)) {
			return false;
		} else {
			class_2338 lv4 = lv.method_10084();
			class_2680 lv5 = arg.method_8320(lv4);
			return class_1948.method_8662(arg, lv4, lv5, lv5.method_11618())
				&& class_1948.method_8662(arg, lv, arg.method_8320(lv), class_3612.field_15906.method_15785())
				&& arg.method_8606(this);
		}
	}
}
