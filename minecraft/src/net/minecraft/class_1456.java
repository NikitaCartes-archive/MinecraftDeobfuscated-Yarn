package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1456 extends class_1429 {
	private static final class_2940<Boolean> field_6840 = class_2945.method_12791(class_1456.class, class_2943.field_13323);
	private float field_6838;
	private float field_6837;
	private int field_6839;

	public class_1456(class_1937 arg) {
		super(class_1299.field_6042, arg);
		this.method_5835(1.3F, 1.4F);
	}

	@Override
	public class_1296 method_5613(class_1296 arg) {
		return new class_1456(this.field_6002);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return false;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1456.class_1460());
		this.field_6201.method_6277(1, new class_1456.class_1461());
		this.field_6201.method_6277(4, new class_1353(this, 1.25));
		this.field_6201.method_6277(5, new class_1379(this, 1.0));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(7, new class_1376(this));
		this.field_6185.method_6277(1, new class_1456.class_1459());
		this.field_6185.method_6277(2, new class_1456.class_1457());
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(30.0);
		this.method_5996(class_1612.field_7365).method_6192(20.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
		this.method_6127().method_6208(class_1612.field_7363);
		this.method_5996(class_1612.field_7363).method_6192(6.0);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		int i = class_3532.method_15357(this.field_5987);
		int j = class_3532.method_15357(this.method_5829().field_1322);
		int k = class_3532.method_15357(this.field_6035);
		class_2338 lv = new class_2338(i, j, k);
		class_1959 lv2 = arg.method_8310(lv);
		return lv2 != class_1972.field_9435 && lv2 != class_1972.field_9418
			? super.method_5979(arg, arg2)
			: arg.method_8624(lv, 0) > 8 && arg.method_8320(lv.method_10074()).method_11614() == class_2246.field_10295;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_6109() ? class_3417.field_14605 : class_3417.field_15078;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15107;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15209;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_15036, 0.15F, 1.0F);
	}

	protected void method_6602() {
		if (this.field_6839 <= 0) {
			this.method_5783(class_3417.field_14937, 1.0F, 1.0F);
			this.field_6839 = 40;
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6840, false);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236) {
			this.field_6838 = this.field_6837;
			if (this.method_6600()) {
				this.field_6837 = class_3532.method_15363(this.field_6837 + 1.0F, 0.0F, 6.0F);
			} else {
				this.field_6837 = class_3532.method_15363(this.field_6837 - 1.0F, 0.0F, 6.0F);
			}
		}

		if (this.field_6839 > 0) {
			this.field_6839--;
		}
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		boolean bl = arg.method_5643(class_1282.method_5511(this), (float)((int)this.method_5996(class_1612.field_7363).method_6194()));
		if (bl) {
			this.method_5723(this, arg);
		}

		return bl;
	}

	public boolean method_6600() {
		return this.field_6011.method_12789(field_6840);
	}

	public void method_6603(boolean bl) {
		this.field_6011.method_12778(field_6840, bl);
	}

	@Environment(EnvType.CLIENT)
	public float method_6601(float f) {
		return class_3532.method_16439(f, this.field_6838, this.field_6837) / 6.0F;
	}

	@Override
	protected float method_6120() {
		return 0.98F;
	}

	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		if (arg4 instanceof class_1456.class_1458) {
			if (((class_1456.class_1458)arg4).field_6842) {
				this.method_5614(-24000);
			}
		} else {
			class_1456.class_1458 lv = new class_1456.class_1458();
			lv.field_6842 = true;
			arg4 = lv;
		}

		return arg4;
	}

	class class_1457 extends class_1400<class_1657> {
		public class_1457() {
			super(class_1456.this, class_1657.class, 20, true, true, null);
		}

		@Override
		public boolean method_6264() {
			if (class_1456.this.method_6109()) {
				return false;
			} else {
				if (super.method_6264()) {
					for (class_1456 lv : class_1456.this.field_6002.method_8403(class_1456.class, class_1456.this.method_5829().method_1009(8.0, 4.0, 8.0))) {
						if (lv.method_6109()) {
							return true;
						}
					}
				}

				class_1456.this.method_5980(null);
				return false;
			}
		}

		@Override
		protected double method_6326() {
			return super.method_6326() * 0.5;
		}
	}

	static class class_1458 implements class_1315 {
		public boolean field_6842;

		private class_1458() {
		}
	}

	class class_1459 extends class_1399 {
		public class_1459() {
			super(class_1456.this);
		}

		@Override
		public void method_6269() {
			super.method_6269();
			if (class_1456.this.method_6109()) {
				this.method_6317();
				this.method_6270();
			}
		}

		@Override
		protected void method_6319(class_1314 arg, class_1309 arg2) {
			if (arg instanceof class_1456 && !arg.method_6109()) {
				super.method_6319(arg, arg2);
			}
		}
	}

	class class_1460 extends class_1366 {
		public class_1460() {
			super(class_1456.this, 1.25, true);
		}

		@Override
		protected void method_6288(class_1309 arg, double d) {
			double e = this.method_6289(arg);
			if (d <= e && this.field_6505 <= 0) {
				this.field_6505 = 20;
				this.field_6503.method_6121(arg);
				class_1456.this.method_6603(false);
			} else if (d <= e * 2.0) {
				if (this.field_6505 <= 0) {
					class_1456.this.method_6603(false);
					this.field_6505 = 20;
				}

				if (this.field_6505 <= 10) {
					class_1456.this.method_6603(true);
					class_1456.this.method_6602();
				}
			} else {
				this.field_6505 = 20;
				class_1456.this.method_6603(false);
			}
		}

		@Override
		public void method_6270() {
			class_1456.this.method_6603(false);
			super.method_6270();
		}

		@Override
		protected double method_6289(class_1309 arg) {
			return (double)(4.0F + arg.field_5998);
		}
	}

	class class_1461 extends class_1374 {
		public class_1461() {
			super(class_1456.this, 2.0);
		}

		@Override
		public boolean method_6264() {
			return !class_1456.this.method_6109() && !class_1456.this.method_5809() ? false : super.method_6264();
		}
	}
}
