package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class class_3989 extends class_3988 {
	@Nullable
	private class_2338 field_17758;
	private int field_17725;

	public class_3989(class_1299<? extends class_3989> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_5983 = true;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201
			.method_6277(
				0,
				new class_3993<>(
					this,
					class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8997),
					class_3417.field_18315,
					arg -> !this.field_6002.method_8530() && !arg.method_5767()
				)
			);
		this.field_6201
			.method_6277(
				0, new class_3993<>(this, new class_1799(class_1802.field_8103), class_3417.field_18314, arg -> this.field_6002.method_8530() && arg.method_5767())
			);
		this.field_6201.method_6277(1, new class_1390(this));
		this.field_6201.method_6277(1, new class_1338(this, class_1642.class, 8.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1338(this, class_1564.class, 12.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1338(this, class_1632.class, 8.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1338(this, class_1634.class, 8.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1338(this, class_1604.class, 15.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1338(this, class_1581.class, 12.0F, 0.5, 0.5));
		this.field_6201.method_6277(1, new class_1374(this, 0.5));
		this.field_6201.method_6277(1, new class_1364(this));
		this.field_6201.method_6277(2, new class_3989.class_3994(this, 2.0, 0.35));
		this.field_6201.method_6277(4, new class_1370(this, 1.0));
		this.field_6201.method_6277(8, new class_1394(this, 0.35));
		this.field_6201.method_6277(9, new class_1358(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return null;
	}

	@Override
	public boolean method_19270() {
		return false;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		boolean bl = lv.method_7909() == class_1802.field_8448;
		if (bl) {
			lv.method_7920(arg, this, arg2);
			return true;
		} else if (lv.method_7909() != class_1802.field_8086 && this.method_5805() && !this.method_18009() && !this.method_6109()) {
			if (arg2 == class_1268.field_5808) {
				arg.method_7281(class_3468.field_15384);
			}

			if (this.method_8264().isEmpty()) {
				return super.method_5992(arg, arg2);
			} else {
				if (!this.field_6002.field_9236) {
					this.method_8259(arg);
					this.method_17449(arg, this.method_5476(), 1);
				}

				return true;
			}
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	@Override
	protected void method_7237() {
		class_3853.class_1652[] lvs = class_3853.field_17724.get(1);
		class_3853.class_1652[] lvs2 = class_3853.field_17724.get(2);
		if (lvs != null && lvs2 != null) {
			class_1916 lv = this.method_8264();
			this.method_19170(lv, lvs, 5);
			int i = this.field_5974.nextInt(lvs2.length);
			class_3853.class_1652 lv2 = lvs2[i];
			class_1914 lv3 = lv2.method_7246(this, this.field_5974);
			if (lv3 != null) {
				lv.add(lv3);
			}
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("DespawnDelay", this.field_17725);
		if (this.field_17758 != null) {
			arg.method_10566("WanderTarget", class_2512.method_10692(this.field_17758));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("DespawnDelay", 99)) {
			this.field_17725 = arg.method_10550("DespawnDelay");
		}

		if (arg.method_10545("WanderTarget")) {
			this.field_17758 = class_2512.method_10691(arg.method_10562("WanderTarget"));
		}
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Override
	protected void method_18008(class_1914 arg) {
		if (arg.method_8256()) {
			int i = 3 + this.field_5974.nextInt(4);
			this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010 + 0.5, this.field_6035, i));
		}
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_18009() ? class_3417.field_17751 : class_3417.field_17747;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_17749;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_17748;
	}

	@Override
	protected class_3414 method_18807(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		return lv == class_1802.field_8103 ? class_3417.field_18316 : class_3417.field_18313;
	}

	@Override
	protected class_3414 method_18012(boolean bl) {
		return bl ? class_3417.field_17752 : class_3417.field_17750;
	}

	@Override
	protected class_3414 method_18010() {
		return class_3417.field_17752;
	}

	public void method_18013(int i) {
		this.field_17725 = i;
	}

	public int method_18014() {
		return this.field_17725;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_17725 > 0 && !this.method_18009() && --this.field_17725 == 0) {
			this.method_5650();
		}
	}

	public void method_18069(@Nullable class_2338 arg) {
		this.field_17758 = arg;
	}

	@Nullable
	private class_2338 method_18065() {
		return this.field_17758;
	}

	class class_3994 extends class_1352 {
		final class_3989 field_17759;
		final double field_17760;
		final double field_17761;

		class_3994(class_3989 arg2, double d, double e) {
			this.field_17759 = arg2;
			this.field_17760 = d;
			this.field_17761 = e;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public void method_6270() {
			this.field_17759.method_18069(null);
			class_3989.this.field_6189.method_6340();
		}

		@Override
		public boolean method_6264() {
			class_2338 lv = this.field_17759.method_18065();
			return lv != null && this.method_18070(lv, this.field_17760);
		}

		@Override
		public void method_6268() {
			class_2338 lv = this.field_17759.method_18065();
			if (lv != null && class_3989.this.field_6189.method_6357()) {
				if (this.method_18070(lv, 10.0)) {
					class_243 lv2 = new class_243(
							(double)lv.method_10263() - this.field_17759.field_5987,
							(double)lv.method_10264() - this.field_17759.field_6010,
							(double)lv.method_10260() - this.field_17759.field_6035
						)
						.method_1029();
					class_243 lv3 = lv2.method_1021(10.0).method_1031(this.field_17759.field_5987, this.field_17759.field_6010, this.field_17759.field_6035);
					class_3989.this.field_6189.method_6337(lv3.field_1352, lv3.field_1351, lv3.field_1350, this.field_17761);
				} else {
					class_3989.this.field_6189.method_6337((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), this.field_17761);
				}
			}
		}

		private boolean method_18070(class_2338 arg, double d) {
			return !arg.method_19769(this.field_17759.method_19538(), d);
		}
	}
}
