package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1685 extends class_1665 {
	private static final class_2940<Byte> field_7647 = class_2945.method_12791(class_1685.class, class_2943.field_13319);
	private class_1799 field_7650 = new class_1799(class_1802.field_8547);
	private boolean field_7648;
	public int field_7649;

	public class_1685(class_1299<? extends class_1685> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1685(class_1937 arg, class_1309 arg2, class_1799 arg3) {
		super(class_1299.field_6127, arg2, arg);
		this.field_7650 = arg3.method_7972();
		this.field_6011.method_12778(field_7647, (byte)class_1890.method_8206(arg3));
	}

	@Environment(EnvType.CLIENT)
	public class_1685(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6127, d, e, f, arg);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7647, (byte)0);
	}

	@Override
	public void method_5773() {
		if (this.field_7576 > 4) {
			this.field_7648 = true;
		}

		class_1297 lv = this.method_7452();
		if ((this.field_7648 || this.method_7441()) && lv != null) {
			int i = this.field_6011.method_12789(field_7647);
			if (i > 0 && !this.method_7493()) {
				if (!this.field_6002.field_9236 && this.field_7572 == class_1665.class_1666.field_7593) {
					this.method_5699(this.method_7445(), 0.1F);
				}

				this.method_5650();
			} else if (i > 0) {
				this.method_7433(true);
				class_243 lv2 = new class_243(lv.field_5987 - this.field_5987, lv.field_6010 + (double)lv.method_5751() - this.field_6010, lv.field_6035 - this.field_6035);
				this.field_6010 = this.field_6010 + lv2.field_1351 * 0.015 * (double)i;
				if (this.field_6002.field_9236) {
					this.field_5971 = this.field_6010;
				}

				double d = 0.05 * (double)i;
				this.method_18799(this.method_18798().method_1021(0.95).method_1019(lv2.method_1029().method_1021(d)));
				if (this.field_7649 == 0) {
					this.method_5783(class_3417.field_14698, 10.0F, 1.0F);
				}

				this.field_7649++;
			}
		}

		super.method_5773();
	}

	private boolean method_7493() {
		class_1297 lv = this.method_7452();
		return lv == null || !lv.method_5805() ? false : !(lv instanceof class_3222) || !lv.method_7325();
	}

	@Override
	protected class_1799 method_7445() {
		return this.field_7650.method_7972();
	}

	@Nullable
	@Override
	protected class_3966 method_7434(class_243 arg, class_243 arg2) {
		return this.field_7648 ? null : super.method_7434(arg, arg2);
	}

	@Override
	protected void method_7454(class_3966 arg) {
		class_1297 lv = arg.method_17782();
		float f = 8.0F;
		if (lv instanceof class_1309) {
			class_1309 lv2 = (class_1309)lv;
			f += class_1890.method_8218(this.field_7650, lv2.method_6046());
		}

		class_1297 lv3 = this.method_7452();
		class_1282 lv4 = class_1282.method_5520(this, (class_1297)(lv3 == null ? this : lv3));
		this.field_7648 = true;
		class_3414 lv5 = class_3417.field_15213;
		if (lv.method_5643(lv4, f) && lv instanceof class_1309) {
			class_1309 lv6 = (class_1309)lv;
			if (lv3 instanceof class_1309) {
				class_1890.method_8210(lv6, lv3);
				class_1890.method_8213((class_1309)lv3, lv6);
			}

			this.method_7450(lv6);
		}

		this.method_18799(this.method_18798().method_18805(-0.01, -0.1, -0.01));
		float g = 1.0F;
		if (this.field_6002 instanceof class_3218 && this.field_6002.method_8546() && class_1890.method_8228(this.field_7650)) {
			class_2338 lv7 = lv.method_5704();
			if (this.field_6002.method_8311(lv7)) {
				class_1538 lv8 = new class_1538(this.field_6002, (double)lv7.method_10263() + 0.5, (double)lv7.method_10264(), (double)lv7.method_10260() + 0.5, false);
				lv8.method_6961(lv3 instanceof class_3222 ? (class_3222)lv3 : null);
				((class_3218)this.field_6002).method_8416(lv8);
				lv5 = class_3417.field_14896;
				g = 5.0F;
			}
		}

		this.method_5783(lv5, g, 1.0F);
	}

	@Override
	protected class_3414 method_7440() {
		return class_3417.field_15104;
	}

	@Override
	public void method_5694(class_1657 arg) {
		class_1297 lv = this.method_7452();
		if (lv == null || lv.method_5667() == arg.method_5667()) {
			super.method_5694(arg);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("Trident", 10)) {
			this.field_7650 = class_1799.method_7915(arg.method_10562("Trident"));
		}

		this.field_7648 = arg.method_10577("DealtDamage");
		this.field_6011.method_12778(field_7647, (byte)class_1890.method_8206(this.field_7650));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10566("Trident", this.field_7650.method_7953(new class_2487()));
		arg.method_10556("DealtDamage", this.field_7648);
	}

	@Override
	protected void method_7446() {
		int i = this.field_6011.method_12789(field_7647);
		if (this.field_7572 != class_1665.class_1666.field_7593 || i <= 0) {
			super.method_7446();
		}
	}

	@Override
	protected float method_7436() {
		return 0.99F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5727(double d, double e, double f) {
		return true;
	}
}
