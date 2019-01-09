package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1678 extends class_1297 {
	private class_1309 field_7630;
	private class_1297 field_7626;
	@Nullable
	private class_2350 field_7628;
	private int field_7627;
	private double field_7635;
	private double field_7633;
	private double field_7625;
	@Nullable
	private UUID field_7629;
	private class_2338 field_7634;
	@Nullable
	private UUID field_7632;
	private class_2338 field_7631;

	public class_1678(class_1937 arg) {
		super(class_1299.field_6100, arg);
		this.method_5835(0.3125F, 0.3125F);
		this.field_5960 = true;
	}

	@Environment(EnvType.CLIENT)
	public class_1678(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		this(arg);
		this.method_5808(d, e, f, this.field_6031, this.field_5965);
		this.field_5967 = g;
		this.field_5984 = h;
		this.field_6006 = i;
	}

	public class_1678(class_1937 arg, class_1309 arg2, class_1297 arg3, class_2350.class_2351 arg4) {
		this(arg);
		this.field_7630 = arg2;
		class_2338 lv = new class_2338(arg2);
		double d = (double)lv.method_10263() + 0.5;
		double e = (double)lv.method_10264() + 0.5;
		double f = (double)lv.method_10260() + 0.5;
		this.method_5808(d, e, f, this.field_6031, this.field_5965);
		this.field_7626 = arg3;
		this.field_7628 = class_2350.field_11036;
		this.method_7486(arg4);
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	protected void method_5652(class_2487 arg) {
		if (this.field_7630 != null) {
			class_2338 lv = new class_2338(this.field_7630);
			class_2487 lv2 = class_2512.method_10689(this.field_7630.method_5667());
			lv2.method_10569("X", lv.method_10263());
			lv2.method_10569("Y", lv.method_10264());
			lv2.method_10569("Z", lv.method_10260());
			arg.method_10566("Owner", lv2);
		}

		if (this.field_7626 != null) {
			class_2338 lv = new class_2338(this.field_7626);
			class_2487 lv2 = class_2512.method_10689(this.field_7626.method_5667());
			lv2.method_10569("X", lv.method_10263());
			lv2.method_10569("Y", lv.method_10264());
			lv2.method_10569("Z", lv.method_10260());
			arg.method_10566("Target", lv2);
		}

		if (this.field_7628 != null) {
			arg.method_10569("Dir", this.field_7628.method_10146());
		}

		arg.method_10569("Steps", this.field_7627);
		arg.method_10549("TXD", this.field_7635);
		arg.method_10549("TYD", this.field_7633);
		arg.method_10549("TZD", this.field_7625);
	}

	@Override
	protected void method_5749(class_2487 arg) {
		this.field_7627 = arg.method_10550("Steps");
		this.field_7635 = arg.method_10574("TXD");
		this.field_7633 = arg.method_10574("TYD");
		this.field_7625 = arg.method_10574("TZD");
		if (arg.method_10573("Dir", 99)) {
			this.field_7628 = class_2350.method_10143(arg.method_10550("Dir"));
		}

		if (arg.method_10573("Owner", 10)) {
			class_2487 lv = arg.method_10562("Owner");
			this.field_7629 = class_2512.method_10690(lv);
			this.field_7634 = new class_2338(lv.method_10550("X"), lv.method_10550("Y"), lv.method_10550("Z"));
		}

		if (arg.method_10573("Target", 10)) {
			class_2487 lv = arg.method_10562("Target");
			this.field_7632 = class_2512.method_10690(lv);
			this.field_7631 = new class_2338(lv.method_10550("X"), lv.method_10550("Y"), lv.method_10550("Z"));
		}
	}

	@Override
	protected void method_5693() {
	}

	private void method_7487(@Nullable class_2350 arg) {
		this.field_7628 = arg;
	}

	private void method_7486(@Nullable class_2350.class_2351 arg) {
		double d = 0.5;
		class_2338 lv;
		if (this.field_7626 == null) {
			lv = new class_2338(this).method_10074();
		} else {
			d = (double)this.field_7626.field_6019 * 0.5;
			lv = new class_2338(this.field_7626.field_5987, this.field_7626.field_6010 + d, this.field_7626.field_6035);
		}

		double e = (double)lv.method_10263() + 0.5;
		double f = (double)lv.method_10264() + d;
		double g = (double)lv.method_10260() + 0.5;
		class_2350 lv2 = null;
		if (lv.method_10268(this.field_5987, this.field_6010, this.field_6035) >= 4.0) {
			class_2338 lv3 = new class_2338(this);
			List<class_2350> list = Lists.<class_2350>newArrayList();
			if (arg != class_2350.class_2351.field_11048) {
				if (lv3.method_10263() < lv.method_10263() && this.field_6002.method_8623(lv3.method_10078())) {
					list.add(class_2350.field_11034);
				} else if (lv3.method_10263() > lv.method_10263() && this.field_6002.method_8623(lv3.method_10067())) {
					list.add(class_2350.field_11039);
				}
			}

			if (arg != class_2350.class_2351.field_11052) {
				if (lv3.method_10264() < lv.method_10264() && this.field_6002.method_8623(lv3.method_10084())) {
					list.add(class_2350.field_11036);
				} else if (lv3.method_10264() > lv.method_10264() && this.field_6002.method_8623(lv3.method_10074())) {
					list.add(class_2350.field_11033);
				}
			}

			if (arg != class_2350.class_2351.field_11051) {
				if (lv3.method_10260() < lv.method_10260() && this.field_6002.method_8623(lv3.method_10072())) {
					list.add(class_2350.field_11035);
				} else if (lv3.method_10260() > lv.method_10260() && this.field_6002.method_8623(lv3.method_10095())) {
					list.add(class_2350.field_11043);
				}
			}

			lv2 = class_2350.method_10162(this.field_5974);
			if (list.isEmpty()) {
				for (int i = 5; !this.field_6002.method_8623(lv3.method_10093(lv2)) && i > 0; i--) {
					lv2 = class_2350.method_10162(this.field_5974);
				}
			} else {
				lv2 = (class_2350)list.get(this.field_5974.nextInt(list.size()));
			}

			e = this.field_5987 + (double)lv2.method_10148();
			f = this.field_6010 + (double)lv2.method_10164();
			g = this.field_6035 + (double)lv2.method_10165();
		}

		this.method_7487(lv2);
		double h = e - this.field_5987;
		double j = f - this.field_6010;
		double k = g - this.field_6035;
		double l = (double)class_3532.method_15368(h * h + j * j + k * k);
		if (l == 0.0) {
			this.field_7635 = 0.0;
			this.field_7633 = 0.0;
			this.field_7625 = 0.0;
		} else {
			this.field_7635 = h / l * 0.15;
			this.field_7633 = j / l * 0.15;
			this.field_7625 = k / l * 0.15;
		}

		this.field_6007 = true;
		this.field_7627 = 10 + this.field_5974.nextInt(5) * 10;
	}

	@Override
	public void method_5773() {
		if (!this.field_6002.field_9236 && this.field_6002.method_8407() == class_1267.field_5801) {
			this.method_5650();
		} else {
			super.method_5773();
			if (!this.field_6002.field_9236) {
				if (this.field_7626 == null && this.field_7632 != null) {
					for (class_1309 lv : this.field_6002
						.method_8403(class_1309.class, new class_238(this.field_7631.method_10069(-2, -2, -2), this.field_7631.method_10069(2, 2, 2)))) {
						if (lv.method_5667().equals(this.field_7632)) {
							this.field_7626 = lv;
							break;
						}
					}

					this.field_7632 = null;
				}

				if (this.field_7630 == null && this.field_7629 != null) {
					for (class_1309 lvx : this.field_6002
						.method_8403(class_1309.class, new class_238(this.field_7634.method_10069(-2, -2, -2), this.field_7634.method_10069(2, 2, 2)))) {
						if (lvx.method_5667().equals(this.field_7629)) {
							this.field_7630 = lvx;
							break;
						}
					}

					this.field_7629 = null;
				}

				if (this.field_7626 == null || !this.field_7626.method_5805() || this.field_7626 instanceof class_1657 && ((class_1657)this.field_7626).method_7325()) {
					if (!this.method_5740()) {
						this.field_5984 -= 0.04;
					}
				} else {
					this.field_7635 = class_3532.method_15350(this.field_7635 * 1.025, -1.0, 1.0);
					this.field_7633 = class_3532.method_15350(this.field_7633 * 1.025, -1.0, 1.0);
					this.field_7625 = class_3532.method_15350(this.field_7625 * 1.025, -1.0, 1.0);
					this.field_5967 = this.field_5967 + (this.field_7635 - this.field_5967) * 0.2;
					this.field_5984 = this.field_5984 + (this.field_7633 - this.field_5984) * 0.2;
					this.field_6006 = this.field_6006 + (this.field_7625 - this.field_6006) * 0.2;
				}

				class_239 lv2 = class_1675.method_7482(this, true, false, this.field_7630);
				if (lv2 != null) {
					this.method_7488(lv2);
				}
			}

			this.method_5814(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
			class_1675.method_7484(this, 0.5F);
			if (this.field_6002.field_9236) {
				this.field_6002
					.method_8406(
						class_2398.field_11207, this.field_5987 - this.field_5967, this.field_6010 - this.field_5984 + 0.15, this.field_6035 - this.field_6006, 0.0, 0.0, 0.0
					);
			} else if (this.field_7626 != null && !this.field_7626.field_5988) {
				if (this.field_7627 > 0) {
					this.field_7627--;
					if (this.field_7627 == 0) {
						this.method_7486(this.field_7628 == null ? null : this.field_7628.method_10166());
					}
				}

				if (this.field_7628 != null) {
					class_2338 lv3 = new class_2338(this);
					class_2350.class_2351 lv4 = this.field_7628.method_10166();
					if (this.field_6002.method_8515(lv3.method_10093(this.field_7628))) {
						this.method_7486(lv4);
					} else {
						class_2338 lv5 = new class_2338(this.field_7626);
						if (lv4 == class_2350.class_2351.field_11048 && lv3.method_10263() == lv5.method_10263()
							|| lv4 == class_2350.class_2351.field_11051 && lv3.method_10260() == lv5.method_10260()
							|| lv4 == class_2350.class_2351.field_11052 && lv3.method_10264() == lv5.method_10264()) {
							this.method_7486(lv4);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean method_5809() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		return d < 16384.0;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	protected void method_7488(class_239 arg) {
		if (arg.field_1326 == null) {
			((class_3218)this.field_6002).method_14199(class_2398.field_11236, this.field_5987, this.field_6010, this.field_6035, 2, 0.2, 0.2, 0.2, 0.0);
			this.method_5783(class_3417.field_14895, 1.0F, 1.0F);
		} else {
			boolean bl = arg.field_1326.method_5643(class_1282.method_5519(this, this.field_7630).method_5517(), 4.0F);
			if (bl) {
				this.method_5723(this.field_7630, arg.field_1326);
				if (arg.field_1326 instanceof class_1309) {
					((class_1309)arg.field_1326).method_6092(new class_1293(class_1294.field_5902, 200));
				}
			}
		}

		this.method_5650();
	}

	@Override
	public boolean method_5863() {
		return true;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (!this.field_6002.field_9236) {
			this.method_5783(class_3417.field_14977, 1.0F, 1.0F);
			((class_3218)this.field_6002).method_14199(class_2398.field_11205, this.field_5987, this.field_6010, this.field_6035, 15, 0.2, 0.2, 0.2, 0.0);
			this.method_5650();
		}

		return true;
	}
}
