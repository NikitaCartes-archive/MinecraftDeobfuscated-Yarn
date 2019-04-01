package net.minecraft;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;

public abstract class class_1547 extends class_1588 implements class_1603 {
	private final class_1380<class_1547> field_7220 = new class_1380<>(this, 1.0, 20, 15.0F);
	private final class_1366 field_7221 = new class_1366(this, 1.2, false) {
		@Override
		public void method_6270() {
			super.method_6270();
			class_1547.this.method_19540(false);
		}

		@Override
		public void method_6269() {
			super.method_6269();
			class_1547.this.method_19540(true);
		}
	};

	protected class_1547(class_1299<? extends class_1547> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_6997();
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(2, new class_1384(this));
		this.field_6201.method_6277(3, new class_1344(this, 1.0));
		this.field_6201.method_6277(3, new class_1338(this, class_1493.class, 6.0F, 1.0, 1.2));
		this.field_6201.method_6277(5, new class_1394(this, 1.0));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(6, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this));
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_1481.class, 10, true, false, class_1481.field_6921));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(this.method_6998(), 0.15F, 1.0F);
	}

	abstract class_3414 method_6998();

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	public void method_6007() {
		boolean bl = this.method_5972();
		if (bl) {
			class_1799 lv = this.method_6118(class_1304.field_6169);
			if (!lv.method_7960()) {
				if (lv.method_7963()) {
					lv.method_7974(lv.method_7919() + this.field_5974.nextInt(2));
					if (lv.method_7919() >= lv.method_7936()) {
						this.method_6045(lv);
						this.method_5673(class_1304.field_6169, class_1799.field_8037);
					}
				}

				bl = false;
			}

			if (bl) {
				this.method_5639(8);
			}
		}

		super.method_6007();
	}

	@Override
	public void method_5842() {
		super.method_5842();
		if (this.method_5854() instanceof class_1314) {
			class_1314 lv = (class_1314)this.method_5854();
			this.field_6283 = lv.field_6283;
		}
	}

	@Override
	protected void method_5964(class_1266 arg) {
		super.method_5964(arg);
		this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8102));
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		this.method_5964(arg2);
		this.method_5984(arg2);
		this.method_6997();
		this.method_5952(this.field_5974.nextFloat() < 0.55F * arg2.method_5458());
		if (this.method_6118(class_1304.field_6169).method_7960()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.field_5974.nextFloat() < 0.25F) {
				this.method_5673(class_1304.field_6169, new class_1799(this.field_5974.nextFloat() < 0.1F ? class_2246.field_10009 : class_2246.field_10147));
				this.field_6186[class_1304.field_6169.method_5927()] = 0.0F;
			}
		}

		return arg4;
	}

	public void method_6997() {
		if (this.field_6002 != null && !this.field_6002.field_9236) {
			this.field_6201.method_6280(this.field_7221);
			this.field_6201.method_6280(this.field_7220);
			class_1799 lv = this.method_5998(class_1675.method_18812(this, class_1802.field_8102));
			if (lv.method_7909() == class_1802.field_8102) {
				int i = 20;
				if (this.field_6002.method_8407() != class_1267.field_5807) {
					i = 40;
				}

				this.field_7220.method_6305(i);
				this.field_6201.method_6277(4, this.field_7220);
			} else {
				this.field_6201.method_6277(4, this.field_7221);
			}
		}
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		class_1799 lv = this.method_18808(this.method_5998(class_1675.method_18812(this, class_1802.field_8102)));
		class_1665 lv2 = this.method_6996(lv, f);
		double d = arg.field_5987 - this.field_5987;
		double e = arg.method_5829().field_1322 + (double)(arg.method_17682() / 3.0F) - lv2.field_6010;
		double g = arg.field_6035 - this.field_6035;
		double h = (double)class_3532.method_15368(d * d + g * g);
		lv2.method_7485(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4));
		this.method_5783(class_3417.field_14633, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
		this.field_6002.method_8649(lv2);
	}

	protected class_1665 method_6996(class_1799 arg, float f) {
		return class_1675.method_18813(this, arg, f);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6997();
	}

	@Override
	public void method_5673(class_1304 arg, class_1799 arg2) {
		super.method_5673(arg, arg2);
		if (!this.field_6002.field_9236) {
			this.method_6997();
		}
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 1.74F;
	}

	@Override
	public double method_5678() {
		return -0.6;
	}
}
