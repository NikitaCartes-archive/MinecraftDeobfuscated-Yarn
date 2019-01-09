package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public abstract class class_3763 extends class_3732 {
	private static final Predicate<class_1542> field_16600 = arg -> !arg.method_6977()
			&& arg.method_5805()
			&& class_1799.method_7973(arg.method_6983(), class_3765.field_16609);
	protected class_3765 field_16599;
	private int field_16601;
	private boolean field_16602;
	private int field_16997;

	protected class_3763(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public abstract void method_16484(int i, boolean bl);

	public boolean method_16481() {
		return this.field_16602;
	}

	public void method_16480(boolean bl) {
		this.field_16602 = bl;
	}

	@Override
	public void method_6007() {
		if (!this.field_6002.field_9236) {
			class_3765 lv = this.method_16478();
			if (lv == null) {
				if (this.field_6002.method_8510() % 20L == 0L) {
					class_3765 lv2 = this.method_16837();
					if (lv2 != null && class_3767.method_16838(this, this.field_16599)) {
						lv2.method_16516(lv2.method_16490(), this, null, true);
					}
				}
			} else {
				class_1309 lv3 = this.method_5968();
				if (lv3 instanceof class_1657 || lv3 instanceof class_1427) {
					this.field_6278 = 0;
				}
			}
		}

		super.method_6007();
	}

	@Override
	protected void method_16827() {
		this.field_6278 += 2;
	}

	@Override
	public void method_6078(class_1282 arg) {
		if (!this.field_6002.field_9236) {
			if (this.method_16478() != null) {
				if (this.method_16219()) {
					this.method_16478().method_16500(this.method_16486());
				}

				this.method_16478().method_16510(this, false);
			}

			if (this.method_16219() && this.method_16478() == null && this.method_16837() == null) {
				class_1799 lv = this.method_6118(class_1304.field_6169);
				if (!lv.method_7960() && class_1799.method_7973(lv, class_3765.field_16609) && arg.method_5529() instanceof class_1657) {
					class_1657 lv2 = (class_1657)arg.method_5529();
					class_1293 lv3 = lv2.method_6112(class_1294.field_16595);
					int i = class_3765.method_16944(this.field_5974, this.method_16915());
					if (lv3 != null) {
						i += lv3.method_5578();
						lv2.method_6111(class_1294.field_16595);
					} else {
						i--;
					}

					i = class_3532.method_15340(i, 0, 5);
					class_1293 lv4 = new class_1293(class_1294.field_16595, 120000, i, false, false, true);
					lv2.method_6092(lv4);
				}
			}
		}

		super.method_6078(arg);
	}

	@Nullable
	private class_3765 method_16837() {
		class_1418 lv = this.field_6002.method_8557();
		class_3765 lv2 = null;
		if (lv != null) {
			class_1415 lv3 = lv.method_6438(new class_2338(this.field_5987, this.field_6010, this.field_6035), 0);
			if (lv3 != null) {
				lv2 = lv3.method_16469();
			}
		}

		return lv2 != null && lv2.method_16832() ? lv2 : null;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(2, new class_3763.class_3764<>(this));
		this.field_6201.method_6277(3, new class_3759<>(this));
	}

	@Override
	public boolean method_16472() {
		return !this.method_16482();
	}

	public void method_16476(class_3765 arg) {
		this.field_16599 = arg;
	}

	public class_3765 method_16478() {
		return this.field_16599;
	}

	public boolean method_16482() {
		return this.method_16478() != null && this.method_16478().method_16504();
	}

	public void method_16477(int i) {
		this.field_16601 = i;
	}

	public int method_16486() {
		return this.field_16601;
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Wave", this.field_16601);
		arg.method_10556("HasRaidGoal", this.field_16602);
		if (this.field_16599 != null) {
			arg.method_10569("RaidId", this.field_16599.method_16494());
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_16601 = arg.method_10550("Wave");
		this.field_16602 = arg.method_10577("HasRaidGoal");
		if (arg.method_10573("RaidId", 3)) {
			this.field_16599 = this.field_6002.method_16542().method_16541(arg.method_10550("RaidId"));
			if (this.field_16599 != null) {
				this.field_16599.method_16487(this.field_16601, this, false);
				if (this.method_16219()) {
					this.field_16599.method_16491(this.field_16601, this);
				}
			}
		}
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		boolean bl = this.method_16482() && this.method_16478().method_16496(this.method_16486()) != null;
		if (this.method_16482() && !bl && class_1799.method_7973(lv, class_3765.field_16609)) {
			class_1304 lv2 = class_1304.field_6169;
			class_1799 lv3 = this.method_6118(lv2);
			double d = (double)this.method_5929(lv2);
			if (!lv3.method_7960() && (double)(this.field_5974.nextFloat() - 0.1F) < d) {
				this.method_5775(lv3);
			}

			this.method_5673(lv2, lv);
			this.method_6103(arg, lv.method_7947());
			arg.method_5650();
			this.method_16478().method_16491(this.method_16486(), this);
			this.method_16217(true);
		} else {
			super.method_5949(arg);
		}
	}

	@Override
	public boolean method_5974(double d) {
		return this.method_16478() != null || super.method_5974(d);
	}

	@Override
	public boolean method_17326() {
		return this.method_16478() != null;
	}

	public int method_16836() {
		return this.field_16997;
	}

	public void method_16835(int i) {
		this.field_16997 = i;
	}

	public class class_3764<T extends class_3763> extends class_1352 {
		private final T field_16603;

		public class_3764(T arg2) {
			this.field_16603 = arg2;
			this.method_6265(1);
		}

		@Override
		public boolean method_6264() {
			class_3765 lv = this.field_16603.method_16478();
			if (!class_3763.this.method_16482()
				|| !this.field_16603.method_16485()
				|| class_1799.method_7973(this.field_16603.method_6118(class_1304.field_6169), class_3765.field_16609)) {
				return false;
			} else if (lv.method_16496(this.field_16603.method_16486()) != null && lv.method_16496(this.field_16603.method_16486()).method_5805()) {
				return false;
			} else {
				List<class_1542> list = this.field_16603
					.field_6002
					.method_8390(class_1542.class, this.field_16603.method_5829().method_1009(16.0, 8.0, 16.0), class_3763.field_16600);
				if (!list.isEmpty()) {
					this.field_16603.method_5942().method_6335((class_1297)list.get(0), 1.2F);
				}

				return !list.isEmpty();
			}
		}

		@Override
		public void method_6268() {
			if (this.field_16603.method_5831(this.field_16603.method_5942().method_6355()) < 2.0) {
				List<class_1542> list = this.field_16603
					.field_6002
					.method_8390(class_1542.class, this.field_16603.method_5829().method_1009(4.0, 4.0, 4.0), class_3763.field_16600);
				if (!list.isEmpty()) {
					this.field_16603.method_5949((class_1542)list.get(0));
				}
			}
		}
	}
}
