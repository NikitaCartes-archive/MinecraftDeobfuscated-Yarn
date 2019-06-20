package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_746 extends class_742 {
	public final class_634 field_3944;
	private final class_3469 field_3928;
	private final class_299 field_3930;
	private final List<class_1104> field_3933 = Lists.<class_1104>newArrayList();
	private int field_3912 = 0;
	private double field_3926;
	private double field_3940;
	private double field_3924;
	private float field_3941;
	private float field_3925;
	private boolean field_3920;
	private boolean field_3936;
	private boolean field_3919;
	private int field_3923;
	private boolean field_3918;
	private String field_3943;
	public class_744 field_3913;
	protected final class_310 field_3937;
	protected int field_3935;
	public int field_3921;
	public float field_3932;
	public float field_3916;
	public float field_3931;
	public float field_3914;
	private int field_3938;
	private float field_3922;
	public float field_3929;
	public float field_3911;
	private boolean field_3915;
	private class_1268 field_3945;
	private boolean field_3942;
	private boolean field_3927 = true;
	private int field_3934;
	private boolean field_3939;
	private int field_3917;
	private boolean field_23660;

	public class_746(class_310 arg, class_638 arg2, class_634 arg3, class_3469 arg4, class_299 arg5) {
		super(arg2, arg3.method_2879());
		this.field_3944 = arg3;
		this.field_3928 = arg4;
		this.field_3930 = arg5;
		this.field_3937 = arg;
		this.field_6026 = class_2874.field_13072;
		this.field_3933.add(new class_1116(this, arg.method_1483()));
		this.field_3933.add(new class_4277(this));
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return false;
	}

	@Override
	public void method_6025(float f) {
	}

	@Override
	public boolean method_5873(class_1297 arg, boolean bl) {
		if (!super.method_5873(arg, bl)) {
			return false;
		} else {
			if (arg instanceof class_1688) {
				this.field_3937.method_1483().method_4873(new class_1107(this, (class_1688)arg));
			}

			if (arg instanceof class_1690) {
				this.field_5982 = arg.field_6031;
				this.field_6031 = arg.field_6031;
				this.method_5847(arg.field_6031);
			}

			return true;
		}
	}

	@Override
	public void method_5848() {
		super.method_5848();
		this.field_3942 = false;
	}

	@Override
	public float method_5695(float f) {
		return this.field_5965;
	}

	@Override
	public float method_5705(float f) {
		return this.method_5765() ? super.method_5705(f) : this.field_6031;
	}

	@Override
	public void method_5773() {
		if (this.field_6002.method_8591(new class_2338(this.field_5987, 0.0, this.field_6035))) {
			super.method_5773();
			if (this.method_5765()) {
				this.field_3944.method_2883(new class_2828.class_2831(this.field_6031, this.field_5965, this.field_5952));
				this.field_3944.method_2883(new class_2851(this.field_6212, this.field_6250, this.field_3913.field_3904, this.field_3913.field_3903));
				class_1297 lv = this.method_5668();
				if (lv != this && lv.method_5787()) {
					this.field_3944.method_2883(new class_2833(lv));
				}
			} else {
				this.method_3136();
			}

			for (class_1104 lv2 : this.field_3933) {
				lv2.method_4756();
			}

			boolean bl = this.method_6039();
			if (bl != this.field_23660) {
				this.field_23660 = bl;
				this.field_3937.field_1773.field_4012.method_3215(class_1268.field_5810);
			}
		}
	}

	private void method_3136() {
		boolean bl = this.method_5624();
		if (bl != this.field_3919) {
			class_2848.class_2849 lv = bl ? class_2848.class_2849.field_12981 : class_2848.class_2849.field_12985;
			this.field_3944.method_2883(new class_2848(this, lv));
			this.field_3919 = bl;
		}

		boolean bl2 = this.method_20303();
		if (bl2 != this.field_3936) {
			class_2848.class_2849 lv2 = bl2 ? class_2848.class_2849.field_12979 : class_2848.class_2849.field_12984;
			this.field_3944.method_2883(new class_2848(this, lv2));
			this.field_3936 = bl2;
		}

		if (this.method_3134()) {
			class_238 lv3 = this.method_5829();
			double d = this.field_5987 - this.field_3926;
			double e = lv3.field_1322 - this.field_3940;
			double f = this.field_6035 - this.field_3924;
			double g = (double)(this.field_6031 - this.field_3941);
			double h = (double)(this.field_5965 - this.field_3925);
			this.field_3923++;
			boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.field_3923 >= 20;
			boolean bl4 = g != 0.0 || h != 0.0;
			if (this.method_5765()) {
				class_243 lv4 = this.method_18798();
				this.field_3944.method_2883(new class_2828.class_2830(lv4.field_1352, -999.0, lv4.field_1350, this.field_6031, this.field_5965, this.field_5952));
				bl3 = false;
			} else if (bl3 && bl4) {
				this.field_3944.method_2883(new class_2828.class_2830(this.field_5987, lv3.field_1322, this.field_6035, this.field_6031, this.field_5965, this.field_5952));
			} else if (bl3) {
				this.field_3944.method_2883(new class_2828.class_2829(this.field_5987, lv3.field_1322, this.field_6035, this.field_5952));
			} else if (bl4) {
				this.field_3944.method_2883(new class_2828.class_2831(this.field_6031, this.field_5965, this.field_5952));
			} else if (this.field_3920 != this.field_5952) {
				this.field_3944.method_2883(new class_2828(this.field_5952));
			}

			if (bl3) {
				this.field_3926 = this.field_5987;
				this.field_3940 = lv3.field_1322;
				this.field_3924 = this.field_6035;
				this.field_3923 = 0;
			}

			if (bl4) {
				this.field_3941 = this.field_6031;
				this.field_3925 = this.field_5965;
			}

			this.field_3920 = this.field_5952;
			this.field_3927 = this.field_3937.field_1690.field_1848;
		}
	}

	@Nullable
	@Override
	public class_1542 method_7290(boolean bl) {
		class_2846.class_2847 lv = bl ? class_2846.class_2847.field_12970 : class_2846.class_2847.field_12975;
		this.field_3944.method_2883(new class_2846(lv, class_2338.field_10980, class_2350.field_11033));
		this.field_7514.method_5434(this.field_7514.field_7545, bl && !this.field_7514.method_7391().method_7960() ? this.field_7514.method_7391().method_7947() : 1);
		return null;
	}

	public void method_3142(String string) {
		this.field_3944.method_2883(new class_2797(string));
	}

	@Override
	public void method_6104(class_1268 arg) {
		super.method_6104(arg);
		this.field_3944.method_2883(new class_2879(arg));
	}

	@Override
	public void method_7331() {
		this.field_3944.method_2883(new class_2799(class_2799.class_2800.field_12774));
	}

	@Override
	protected void method_6074(class_1282 arg, float f) {
		if (!this.method_5679(arg)) {
			this.method_6033(this.method_6032() - f);
		}
	}

	@Override
	public void method_7346() {
		this.field_3944.method_2883(new class_2815(this.field_7512.field_7763));
		this.method_3137();
	}

	public void method_3137() {
		this.field_7514.method_7396(class_1799.field_8037);
		super.method_7346();
		this.field_3937.method_1507(null);
	}

	public void method_3138(float f) {
		if (this.field_3918) {
			float g = this.method_6032() - f;
			if (g <= 0.0F) {
				this.method_6033(f);
				if (g < 0.0F) {
					this.field_6008 = 5;
				}
			} else {
				this.field_6253 = g;
				this.method_6033(this.method_6032());
				this.field_6008 = 10;
				this.method_6074(class_1282.field_5869, g);
				this.field_6254 = 10;
				this.field_6235 = this.field_6254;
			}
		} else {
			this.method_6033(f);
			this.field_3918 = true;
		}
	}

	@Override
	public void method_7355() {
		this.field_3944.method_2883(new class_2842(this.field_7503));
	}

	@Override
	public boolean method_7340() {
		return true;
	}

	protected void method_3133() {
		this.field_3944.method_2883(new class_2848(this, class_2848.class_2849.field_12987, class_3532.method_15375(this.method_3151() * 100.0F)));
	}

	public void method_3132() {
		this.field_3944.method_2883(new class_2848(this, class_2848.class_2849.field_12988));
	}

	public void method_3146(String string) {
		this.field_3943 = string;
	}

	public String method_3135() {
		return this.field_3943;
	}

	public class_3469 method_3143() {
		return this.field_3928;
	}

	public class_299 method_3130() {
		return this.field_3930;
	}

	public void method_3141(class_1860<?> arg) {
		if (this.field_3930.method_14883(arg)) {
			this.field_3930.method_14886(arg);
			this.field_3944.method_2883(new class_2853(arg));
		}
	}

	@Override
	protected int method_5691() {
		return this.field_3912;
	}

	public void method_3147(int i) {
		this.field_3912 = i;
	}

	@Override
	public void method_7353(class_2561 arg, boolean bl) {
		if (bl) {
			this.field_3937.field_1705.method_1758(arg, false);
		} else {
			this.field_3937.field_1705.method_1743().method_1812(arg);
		}
	}

	@Override
	protected void method_5632(double d, double e, double f) {
		class_2338 lv = new class_2338(d, e, f);
		if (this.method_3150(lv)) {
			double g = d - (double)lv.method_10263();
			double h = f - (double)lv.method_10260();
			class_2350 lv2 = null;
			double i = 9999.0;
			if (!this.method_3150(lv.method_10067()) && g < i) {
				i = g;
				lv2 = class_2350.field_11039;
			}

			if (!this.method_3150(lv.method_10078()) && 1.0 - g < i) {
				i = 1.0 - g;
				lv2 = class_2350.field_11034;
			}

			if (!this.method_3150(lv.method_10095()) && h < i) {
				i = h;
				lv2 = class_2350.field_11043;
			}

			if (!this.method_3150(lv.method_10072()) && 1.0 - h < i) {
				i = 1.0 - h;
				lv2 = class_2350.field_11035;
			}

			if (lv2 != null) {
				class_243 lv3 = this.method_18798();
				switch (lv2) {
					case field_11039:
						this.method_18800(-0.1, lv3.field_1351, lv3.field_1350);
						break;
					case field_11034:
						this.method_18800(0.1, lv3.field_1351, lv3.field_1350);
						break;
					case field_11043:
						this.method_18800(lv3.field_1352, lv3.field_1351, -0.1);
						break;
					case field_11035:
						this.method_18800(lv3.field_1352, lv3.field_1351, 0.1);
				}
			}
		}
	}

	private boolean method_3150(class_2338 arg) {
		class_238 lv = this.method_5829();
		class_2338.class_2339 lv2 = new class_2338.class_2339(arg);

		for (int i = class_3532.method_15357(lv.field_1322); i < class_3532.method_15384(lv.field_1325); i++) {
			lv2.method_10099(i);
			if (!this.method_7326(lv2)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void method_5728(boolean bl) {
		super.method_5728(bl);
		this.field_3921 = 0;
	}

	public void method_3145(float f, int i, int j) {
		this.field_7510 = f;
		this.field_7495 = i;
		this.field_7520 = j;
	}

	@Override
	public void method_9203(class_2561 arg) {
		this.field_3937.field_1705.method_1743().method_1812(arg);
	}

	@Override
	public void method_5711(byte b) {
		if (b >= 24 && b <= 28) {
			this.method_3147(b - 24);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void method_5783(class_3414 arg, float f, float g) {
		this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, arg, this.method_5634(), f, g, false);
	}

	@Override
	public void method_17356(class_3414 arg, class_3419 arg2, float f, float g) {
		this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, arg, arg2, f, g, false);
	}

	@Override
	public boolean method_6034() {
		return true;
	}

	@Override
	public void method_6019(class_1268 arg) {
		class_1799 lv = this.method_5998(arg);
		if (!lv.method_7960() && !this.method_6115()) {
			super.method_6019(arg);
			this.field_3915 = true;
			this.field_3945 = arg;
		}
	}

	@Override
	public boolean method_6115() {
		return this.field_3915;
	}

	@Override
	public void method_6021() {
		super.method_6021();
		this.field_3915 = false;
	}

	@Override
	public class_1268 method_6058() {
		return this.field_3945;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		super.method_5674(arg);
		if (field_6257.equals(arg)) {
			boolean bl = (this.field_6011.method_12789(field_6257) & 1) > 0;
			class_1268 lv = (this.field_6011.method_12789(field_6257) & 2) > 0 ? class_1268.field_5810 : class_1268.field_5808;
			if (bl && !this.field_3915) {
				this.method_6019(lv);
			} else if (!bl && this.field_3915) {
				this.method_6021();
			}
		}

		if (field_5990.equals(arg) && this.method_6128() && !this.field_3939) {
			this.field_3937.method_1483().method_4873(new class_1103(this));
		}
	}

	public boolean method_3131() {
		class_1297 lv = this.method_5854();
		return this.method_5765() && lv instanceof class_1316 && ((class_1316)lv).method_6153();
	}

	public float method_3151() {
		return this.field_3922;
	}

	@Override
	public void method_7311(class_2625 arg) {
		this.field_3937.method_1507(new class_498(arg));
	}

	@Override
	public void method_7257(class_1918 arg) {
		this.field_3937.method_1507(new class_496(arg));
	}

	@Override
	public void method_7323(class_2593 arg) {
		this.field_3937.method_1507(new class_477(arg));
	}

	@Override
	public void method_7303(class_2633 arg) {
		this.field_3937.method_1507(new class_497(arg));
	}

	@Override
	public void method_16354(class_3751 arg) {
		this.field_3937.method_1507(new class_3742(arg));
	}

	@Override
	public void method_7315(class_1799 arg, class_1268 arg2) {
		class_1792 lv = arg.method_7909();
		if (lv == class_1802.field_8674) {
			this.field_3937.method_1507(new class_473(this, arg, arg2));
		}
	}

	@Override
	public void method_7277(class_1297 arg) {
		this.field_3937.field_1713.method_3061(arg, class_2398.field_11205);
	}

	@Override
	public void method_7304(class_1297 arg) {
		this.field_3937.field_1713.method_3061(arg, class_2398.field_11208);
	}

	@Override
	public boolean method_5715() {
		return this.method_20303();
	}

	public boolean method_20303() {
		return this.field_3913 != null && this.field_3913.field_3903;
	}

	@Override
	public boolean method_20231() {
		return !this.field_7503.field_7479 && !this.method_5681() && this.method_20233(class_4050.field_18081)
			? this.method_20303() || !this.method_20233(class_4050.field_18076)
			: false;
	}

	@Override
	public void method_6023() {
		super.method_6023();
		if (this.method_3134()) {
			this.field_6212 = this.field_3913.field_3907;
			this.field_6250 = this.field_3913.field_3905;
			this.field_6282 = this.field_3913.field_3904;
			this.field_3931 = this.field_3932;
			this.field_3914 = this.field_3916;
			this.field_3916 = (float)((double)this.field_3916 + (double)(this.field_5965 - this.field_3916) * 0.5);
			this.field_3932 = (float)((double)this.field_3932 + (double)(this.field_6031 - this.field_3932) * 0.5);
		}
	}

	protected boolean method_3134() {
		return this.field_3937.method_1560() == this;
	}

	@Override
	public void method_6007() {
		this.field_3921++;
		if (this.field_3935 > 0) {
			this.field_3935--;
		}

		this.method_18654();
		boolean bl = this.field_3913.field_3904;
		boolean bl2 = this.field_3913.field_3903;
		boolean bl3 = this.method_20623();
		boolean bl4 = this.method_20231() || this.method_20448();
		this.field_3913.method_3129(bl4, this.method_7325());
		this.field_3937.method_1577().method_4909(this.field_3913);
		if (this.method_6115() && !this.method_5765()) {
			this.field_3913.field_3907 *= 0.2F;
			this.field_3913.field_3905 *= 0.2F;
			this.field_3935 = 0;
		}

		boolean bl5 = false;
		if (this.field_3934 > 0) {
			this.field_3934--;
			bl5 = true;
			this.field_3913.field_3904 = true;
		}

		if (!this.field_5960) {
			class_238 lv = this.method_5829();
			this.method_5632(this.field_5987 - (double)this.method_17681() * 0.35, lv.field_1322 + 0.5, this.field_6035 + (double)this.method_17681() * 0.35);
			this.method_5632(this.field_5987 - (double)this.method_17681() * 0.35, lv.field_1322 + 0.5, this.field_6035 - (double)this.method_17681() * 0.35);
			this.method_5632(this.field_5987 + (double)this.method_17681() * 0.35, lv.field_1322 + 0.5, this.field_6035 - (double)this.method_17681() * 0.35);
			this.method_5632(this.field_5987 + (double)this.method_17681() * 0.35, lv.field_1322 + 0.5, this.field_6035 + (double)this.method_17681() * 0.35);
		}

		boolean bl6 = (float)this.method_7344().method_7586() > 6.0F || this.field_7503.field_7478;
		if ((this.field_5952 || this.method_5869())
			&& !bl2
			&& !bl3
			&& this.method_20623()
			&& !this.method_5624()
			&& bl6
			&& !this.method_6115()
			&& !this.method_6059(class_1294.field_5919)) {
			if (this.field_3935 <= 0 && !this.field_3937.field_1690.field_1867.method_1434()) {
				this.field_3935 = 7;
			} else {
				this.method_5728(true);
			}
		}

		if (!this.method_5624()
			&& (!this.method_5799() || this.method_5869())
			&& this.method_20623()
			&& bl6
			&& !this.method_6115()
			&& !this.method_6059(class_1294.field_5919)
			&& this.field_3937.field_1690.field_1867.method_1434()) {
			this.method_5728(true);
		}

		if (this.method_5624()) {
			boolean bl7 = !this.field_3913.method_20622() || !bl6;
			boolean bl8 = bl7 || this.field_5976 || this.method_5799() && !this.method_5869();
			if (this.method_5681()) {
				if (!this.field_5952 && !this.field_3913.field_3903 && bl7 || !this.method_5799()) {
					this.method_5728(false);
				}
			} else if (bl8) {
				this.method_5728(false);
			}
		}

		if (this.field_7503.field_7478) {
			if (this.field_3937.field_1761.method_2928()) {
				if (!this.field_7503.field_7479) {
					this.field_7503.field_7479 = true;
					this.method_7355();
				}
			} else if (!bl && this.field_3913.field_3904 && !bl5) {
				if (this.field_7489 == 0) {
					this.field_7489 = 7;
				} else if (!this.method_5681()) {
					this.field_7503.field_7479 = !this.field_7503.field_7479;
					this.method_7355();
					this.field_7489 = 0;
				}
			}
		}

		if (this.field_3913.field_3904 && !bl && !this.field_5952 && this.method_18798().field_1351 < 0.0 && !this.method_6128() && !this.field_7503.field_7479) {
			class_1799 lv2 = this.method_6118(class_1304.field_6174);
			if (lv2.method_7909() == class_1802.field_8833 && class_1770.method_7804(lv2)) {
				this.field_3944.method_2883(new class_2848(this, class_2848.class_2849.field_12982));
			}
		}

		this.field_3939 = this.method_6128();
		if (this.method_5799() && this.field_3913.field_3903) {
			this.method_6093();
		}

		if (this.method_5777(class_3486.field_15517)) {
			int i = this.method_7325() ? 10 : 1;
			this.field_3917 = class_3532.method_15340(this.field_3917 + i, 0, 600);
		} else if (this.field_3917 > 0) {
			this.method_5777(class_3486.field_15517);
			this.field_3917 = class_3532.method_15340(this.field_3917 - 10, 0, 600);
		}

		if (this.field_7503.field_7479 && this.method_3134()) {
			int i = 0;
			if (this.field_3913.field_3903) {
				this.field_3913.field_3907 = (float)((double)this.field_3913.field_3907 / 0.3);
				this.field_3913.field_3905 = (float)((double)this.field_3913.field_3905 / 0.3);
				i--;
			}

			if (this.field_3913.field_3904) {
				i++;
			}

			if (i != 0) {
				this.method_18799(this.method_18798().method_1031(0.0, (double)((float)i * this.field_7503.method_7252() * 3.0F), 0.0));
			}
		}

		if (this.method_3131()) {
			class_1316 lv3 = (class_1316)this.method_5854();
			if (this.field_3938 < 0) {
				this.field_3938++;
				if (this.field_3938 == 0) {
					this.field_3922 = 0.0F;
				}
			}

			if (bl && !this.field_3913.field_3904) {
				this.field_3938 = -10;
				lv3.method_6154(class_3532.method_15375(this.method_3151() * 100.0F));
				this.method_3133();
			} else if (!bl && this.field_3913.field_3904) {
				this.field_3938 = 0;
				this.field_3922 = 0.0F;
			} else if (bl) {
				this.field_3938++;
				if (this.field_3938 < 10) {
					this.field_3922 = (float)this.field_3938 * 0.1F;
				} else {
					this.field_3922 = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
				}
			}
		} else {
			this.field_3922 = 0.0F;
		}

		super.method_6007();
		if (this.field_5952 && this.field_7503.field_7479 && !this.field_3937.field_1761.method_2928()) {
			this.field_7503.field_7479 = false;
			this.method_7355();
		}
	}

	private void method_18654() {
		this.field_3911 = this.field_3929;
		if (this.field_5963) {
			if (this.field_3937.field_1755 != null && !this.field_3937.field_1755.isPauseScreen()) {
				if (this.field_3937.field_1755 instanceof class_465) {
					this.method_7346();
				}

				this.field_3937.method_1507(null);
			}

			if (this.field_3929 == 0.0F) {
				this.field_3937.method_1483().method_4873(class_1109.method_4758(class_3417.field_14669, this.field_5974.nextFloat() * 0.4F + 0.8F));
			}

			this.field_3929 += 0.0125F;
			if (this.field_3929 >= 1.0F) {
				this.field_3929 = 1.0F;
			}

			this.field_5963 = false;
		} else if (this.method_6059(class_1294.field_5916) && this.method_6112(class_1294.field_5916).method_5584() > 60) {
			this.field_3929 += 0.006666667F;
			if (this.field_3929 > 1.0F) {
				this.field_3929 = 1.0F;
			}
		} else {
			if (this.field_3929 > 0.0F) {
				this.field_3929 -= 0.05F;
			}

			if (this.field_3929 < 0.0F) {
				this.field_3929 = 0.0F;
			}
		}

		this.method_5760();
	}

	@Override
	public void method_5842() {
		super.method_5842();
		this.field_3942 = false;
		if (this.method_5854() instanceof class_1690) {
			class_1690 lv = (class_1690)this.method_5854();
			lv.method_7535(this.field_3913.field_3908, this.field_3913.field_3906, this.field_3913.field_3910, this.field_3913.field_3909);
			this.field_3942 = this.field_3942 | (this.field_3913.field_3908 || this.field_3913.field_3906 || this.field_3913.field_3910 || this.field_3913.field_3909);
		}
	}

	public boolean method_3144() {
		return this.field_3942;
	}

	@Nullable
	@Override
	public class_1293 method_6111(@Nullable class_1291 arg) {
		if (arg == class_1294.field_5916) {
			this.field_3911 = 0.0F;
			this.field_3929 = 0.0F;
		}

		return super.method_6111(arg);
	}

	@Override
	public void method_5784(class_1313 arg, class_243 arg2) {
		double d = this.field_5987;
		double e = this.field_6035;
		super.method_5784(arg, arg2);
		this.method_3148((float)(this.field_5987 - d), (float)(this.field_6035 - e));
	}

	public boolean method_3149() {
		return this.field_3927;
	}

	protected void method_3148(float f, float g) {
		if (this.method_3149()) {
			if (this.field_3934 <= 0 && this.field_5952 && !this.method_5715() && !this.method_5765()) {
				class_241 lv = this.field_3913.method_3128();
				if (lv.field_1343 != 0.0F || lv.field_1342 != 0.0F) {
					class_243 lv2 = new class_243(this.field_5987, this.method_5829().field_1322, this.field_6035);
					class_243 lv3 = new class_243(this.field_5987 + (double)f, this.method_5829().field_1322, this.field_6035 + (double)g);
					class_243 lv4 = new class_243((double)f, 0.0, (double)g);
					float h = this.method_6029();
					float i = (float)lv4.method_1027();
					if (i <= 0.001F) {
						float j = h * lv.field_1343;
						float k = h * lv.field_1342;
						float l = class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0));
						float m = class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0));
						lv4 = new class_243((double)(j * m - k * l), lv4.field_1351, (double)(k * m + j * l));
						i = (float)lv4.method_1027();
						if (i <= 0.001F) {
							return;
						}
					}

					float j = (float)class_3532.method_15345((double)i);
					class_243 lv5 = lv4.method_1021((double)j);
					class_243 lv6 = this.method_5663();
					float m = (float)(lv6.field_1352 * lv5.field_1352 + lv6.field_1350 * lv5.field_1350);
					if (!(m < -0.15F)) {
						class_3726 lv7 = class_3726.method_16195(this);
						class_2338 lv8 = new class_2338(this.field_5987, this.method_5829().field_1325, this.field_6035);
						class_2680 lv9 = this.field_6002.method_8320(lv8);
						if (lv9.method_16337(this.field_6002, lv8, lv7).method_1110()) {
							lv8 = lv8.method_10084();
							class_2680 lv10 = this.field_6002.method_8320(lv8);
							if (lv10.method_16337(this.field_6002, lv8, lv7).method_1110()) {
								float n = 7.0F;
								float o = 1.2F;
								if (this.method_6059(class_1294.field_5913)) {
									o += (float)(this.method_6112(class_1294.field_5913).method_5578() + 1) * 0.75F;
								}

								float p = Math.max(h * 7.0F, 1.0F / j);
								class_243 lv12 = lv3.method_1019(lv5.method_1021((double)p));
								float q = this.method_17681();
								float r = this.method_17682();
								class_238 lv13 = new class_238(lv2, lv12.method_1031(0.0, (double)r, 0.0)).method_1009((double)q, 0.0, (double)q);
								class_243 lv11 = lv2.method_1031(0.0, 0.51F, 0.0);
								lv12 = lv12.method_1031(0.0, 0.51F, 0.0);
								class_243 lv14 = lv5.method_1036(new class_243(0.0, 1.0, 0.0));
								class_243 lv15 = lv14.method_1021((double)(q * 0.5F));
								class_243 lv16 = lv11.method_1020(lv15);
								class_243 lv17 = lv12.method_1020(lv15);
								class_243 lv18 = lv11.method_1019(lv15);
								class_243 lv19 = lv12.method_1019(lv15);
								Iterator<class_238> iterator = this.field_6002.method_8600(this, lv13, Collections.emptySet()).flatMap(arg -> arg.method_1090().stream()).iterator();
								float s = Float.MIN_VALUE;

								while (iterator.hasNext()) {
									class_238 lv20 = (class_238)iterator.next();
									if (lv20.method_993(lv16, lv17) || lv20.method_993(lv18, lv19)) {
										s = (float)lv20.field_1325;
										class_243 lv21 = lv20.method_1005();
										class_2338 lv22 = new class_2338(lv21);

										for (int t = 1; (float)t < o; t++) {
											class_2338 lv23 = lv22.method_10086(t);
											class_2680 lv24 = this.field_6002.method_8320(lv23);
											class_265 lv25;
											if (!(lv25 = lv24.method_16337(this.field_6002, lv23, lv7)).method_1110()) {
												s = (float)lv25.method_1105(class_2350.class_2351.field_11052) + (float)lv23.method_10264();
												if ((double)s - this.method_5829().field_1322 > (double)o) {
													return;
												}
											}

											if (t > 1) {
												lv8 = lv8.method_10084();
												class_2680 lv26 = this.field_6002.method_8320(lv8);
												if (!lv26.method_16337(this.field_6002, lv8, lv7).method_1110()) {
													return;
												}
											}
										}
										break;
									}
								}

								if (s != Float.MIN_VALUE) {
									float u = (float)((double)s - this.method_5829().field_1322);
									if (!(u <= 0.5F) && !(u > o)) {
										this.field_3934 = 1;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean method_20623() {
		double d = 0.8;
		return this.method_5869() ? this.field_3913.method_20622() : (double)this.field_3913.field_3905 >= 0.8;
	}

	public float method_3140() {
		if (!this.method_5777(class_3486.field_15517)) {
			return 0.0F;
		} else {
			float f = 600.0F;
			float g = 100.0F;
			if ((float)this.field_3917 >= 600.0F) {
				return 1.0F;
			} else {
				float h = class_3532.method_15363((float)this.field_3917 / 100.0F, 0.0F, 1.0F);
				float i = (float)this.field_3917 < 100.0F ? 0.0F : class_3532.method_15363(((float)this.field_3917 - 100.0F) / 500.0F, 0.0F, 1.0F);
				return h * 0.6F + i * 0.39999998F;
			}
		}
	}

	@Override
	public boolean method_5869() {
		return this.field_7490;
	}

	@Override
	protected boolean method_7295() {
		boolean bl = this.field_7490;
		boolean bl2 = super.method_7295();
		if (this.method_7325()) {
			return this.field_7490;
		} else {
			if (!bl && bl2) {
				this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14756, class_3419.field_15256, 1.0F, 1.0F, false);
				this.field_3937.method_1483().method_4873(new class_1118.class_1120(this));
			}

			if (bl && !bl2) {
				this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14828, class_3419.field_15256, 1.0F, 1.0F, false);
			}

			return this.field_7490;
		}
	}
}
