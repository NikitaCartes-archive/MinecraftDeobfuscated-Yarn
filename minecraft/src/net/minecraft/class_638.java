package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_638 extends class_1937 {
	private final class_634 field_3727;
	private final Set<class_1297> field_3728 = Sets.<class_1297>newHashSet();
	private final Set<class_1297> field_3726 = Sets.<class_1297>newHashSet();
	private final class_310 field_3729 = class_310.method_1551();
	private int field_3732;
	private int field_3731;
	private int field_3730 = this.field_9229.nextInt(12000);
	private class_269 field_3733 = new class_269();

	public class_638(class_634 arg, class_1940 arg2, class_2874 arg3, class_1267 arg4, class_3695 arg5) {
		super(new class_36(), new class_38(), new class_31(arg2, "MpServer"), arg3, (argx, arg2x) -> new class_631(argx), arg5, true);
		this.field_3727 = arg;
		this.method_8401().method_208(arg4);
		this.method_8554(new class_2338(8, 64, 8));
		this.method_8451();
		this.method_8543();
	}

	@Override
	public void method_8441(BooleanSupplier booleanSupplier) {
		super.method_8441(booleanSupplier);
		this.method_8560();
		this.method_16107().method_15396("reEntryProcessing");

		for (int i = 0; i < 10 && !this.field_3726.isEmpty(); i++) {
			class_1297 lv = (class_1297)this.field_3726.iterator().next();
			this.field_3726.remove(lv);
			if (!this.field_9240.contains(lv)) {
				this.method_8649(lv);
			}
		}

		this.method_16107().method_15405("blocks");
		this.field_9248.method_12127(booleanSupplier);
		this.method_2939();
		this.method_16107().method_15407();
	}

	@Override
	public boolean method_8393(int i, int j) {
		return true;
	}

	private void method_2939() {
		int i = this.field_3729.field_1690.field_1870;
		int j = class_3532.method_15357(this.field_3729.field_1724.field_5987 / 16.0);
		int k = class_3532.method_15357(this.field_3729.field_1724.field_6035 / 16.0);
		if (this.field_3730 > 0) {
			this.field_3730--;
		} else {
			for (int l = 0; l < 10; l++) {
				int m = j + this.field_3732;
				int n = k + this.field_3731;
				int o = m * 16;
				int p = n * 16;
				this.field_3731++;
				if (this.field_3731 >= i) {
					this.field_3731 = -i;
					this.field_3732++;
					if (this.field_3732 >= i) {
						this.field_3732 = -i;
					}
				}

				class_2818 lv = this.method_8497(m, n);
				this.field_9256 = this.field_9256 * 3 + 1013904223;
				int q = this.field_9256 >> 2;
				int r = q & 15;
				int s = q >> 8 & 15;
				int t = q >> 16 & 0xFF;
				double d = this.field_3729.field_1724.method_5649((double)r + 0.5, (double)t + 0.5, (double)s + 0.5);
				if (this.field_3729.field_1724 != null && d > 4.0 && d < 256.0) {
					class_2338 lv2 = new class_2338(r + o, t, s + p);
					class_2680 lv3 = lv.method_8320(lv2);
					r += o;
					s += p;
					if (lv3.method_11588() && this.method_8624(lv2, 0) <= this.field_9229.nextInt(8) && this.method_8314(class_1944.field_9284, lv2) <= 0) {
						this.method_8486(
							(double)r + 0.5,
							(double)t + 0.5,
							(double)s + 0.5,
							class_3417.field_14564,
							class_3419.field_15256,
							0.7F,
							0.8F + this.field_9229.nextFloat() * 0.2F,
							false
						);
						this.field_3730 = this.field_9229.nextInt(12000) + 6000;
					}
				}
			}
		}
	}

	@Override
	public boolean method_8649(class_1297 arg) {
		boolean bl = super.method_8649(arg);
		this.field_3728.add(arg);
		if (bl) {
			if (arg instanceof class_1688) {
				this.field_3729.method_1483().method_4873(new class_1108((class_1688)arg));
			}
		} else {
			this.field_3726.add(arg);
		}

		return bl;
	}

	@Override
	public void method_8463(class_1297 arg) {
		super.method_8463(arg);
		this.field_3728.remove(arg);
	}

	@Override
	protected void method_8485(class_1297 arg) {
		super.method_8485(arg);
		if (this.field_3726.contains(arg)) {
			this.field_3726.remove(arg);
		}
	}

	@Override
	protected void method_8539(class_1297 arg) {
		super.method_8539(arg);
		if (this.field_3728.contains(arg)) {
			if (arg.method_5805()) {
				this.field_3726.add(arg);
			} else {
				this.field_3728.remove(arg);
			}
		}
	}

	public void method_2942(int i, class_1297 arg) {
		class_1297 lv = this.method_8469(i);
		if (lv != null) {
			this.method_8463(lv);
		}

		this.field_3728.add(arg);
		arg.method_5838(i);
		if (!this.method_8649(arg)) {
			this.field_3726.add(arg);
		}

		this.field_9225.method_15313(i, arg);
	}

	@Nullable
	@Override
	public class_1297 method_8469(int i) {
		return (class_1297)(i == this.field_3729.field_1724.method_5628() ? this.field_3729.field_1724 : super.method_8469(i));
	}

	public class_1297 method_2945(int i) {
		class_1297 lv = this.field_9225.method_15312(i);
		if (lv != null) {
			this.field_3728.remove(lv);
			this.method_8463(lv);
		}

		return lv;
	}

	public void method_2937(class_2338 arg, class_2680 arg2) {
		this.method_8652(arg, arg2, 19);
	}

	@Override
	public void method_8525() {
		this.field_3727.method_2872().method_10747(new class_2588("multiplayer.status.quitting"));
	}

	@Override
	protected void method_8511() {
	}

	public void method_2941(int i, int j, int k) {
		int l = 32;
		Random random = new Random();
		class_1799 lv = this.field_3729.field_1724.method_6047();
		boolean bl = this.field_3729.field_1761.method_2920() == class_1934.field_9220
			&& !lv.method_7960()
			&& lv.method_7909() == class_2246.field_10499.method_8389();
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int m = 0; m < 667; m++) {
			this.method_2943(i, j, k, 16, random, bl, lv2);
			this.method_2943(i, j, k, 32, random, bl, lv2);
		}
	}

	public void method_2943(int i, int j, int k, int l, Random random, boolean bl, class_2338.class_2339 arg) {
		int m = i + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		int n = j + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		int o = k + this.field_9229.nextInt(l) - this.field_9229.nextInt(l);
		arg.method_10103(m, n, o);
		class_2680 lv = this.method_8320(arg);
		lv.method_11614().method_9496(lv, this, arg, random);
		class_3610 lv2 = this.method_8316(arg);
		if (!lv2.method_15769()) {
			lv2.method_15768(this, arg, random);
			class_2394 lv3 = lv2.method_15766();
			if (lv3 != null && this.field_9229.nextInt(10) == 0) {
				boolean bl2 = class_2248.method_9501(lv.method_11628(this, arg), class_2350.field_11033);
				class_2338 lv4 = arg.method_10074();
				this.method_2938(lv4, this.method_8320(lv4), lv3, bl2);
			}
		}

		if (bl && lv.method_11614() == class_2246.field_10499) {
			this.method_8406(class_2398.field_11235, (double)((float)m + 0.5F), (double)((float)n + 0.5F), (double)((float)o + 0.5F), 0.0, 0.0, 0.0);
		}
	}

	private void method_2938(class_2338 arg, class_2680 arg2, class_2394 arg3, boolean bl) {
		if (arg2.method_11618().method_15769()) {
			class_265 lv = arg2.method_11628(this, arg);
			double d = lv.method_1105(class_2350.class_2351.field_11052);
			if (d < 1.0) {
				if (bl) {
					this.method_2932(
						(double)arg.method_10263(),
						(double)(arg.method_10263() + 1),
						(double)arg.method_10260(),
						(double)(arg.method_10260() + 1),
						(double)(arg.method_10264() + 1) - 0.05,
						arg3
					);
				}
			} else if (!arg2.method_11602(class_3481.field_15490)) {
				double e = lv.method_1091(class_2350.class_2351.field_11052);
				if (e > 0.0) {
					this.method_2948(arg, arg3, lv, (double)arg.method_10264() + e - 0.05);
				} else {
					class_2338 lv2 = arg.method_10074();
					class_2680 lv3 = this.method_8320(lv2);
					class_265 lv4 = lv3.method_11628(this, lv2);
					double f = lv4.method_1105(class_2350.class_2351.field_11052);
					if (f < 1.0 && lv3.method_11618().method_15769()) {
						this.method_2948(arg, arg3, lv, (double)arg.method_10264() - 0.05);
					}
				}
			}
		}
	}

	private void method_2948(class_2338 arg, class_2394 arg2, class_265 arg3, double d) {
		this.method_2932(
			(double)arg.method_10263() + arg3.method_1091(class_2350.class_2351.field_11048),
			(double)arg.method_10263() + arg3.method_1105(class_2350.class_2351.field_11048),
			(double)arg.method_10260() + arg3.method_1091(class_2350.class_2351.field_11051),
			(double)arg.method_10260() + arg3.method_1105(class_2350.class_2351.field_11051),
			d,
			arg2
		);
	}

	private void method_2932(double d, double e, double f, double g, double h, class_2394 arg) {
		this.method_8406(
			arg, class_3532.method_16436(this.field_9229.nextDouble(), d, e), h, class_3532.method_16436(this.field_9229.nextDouble(), f, g), 0.0, 0.0, 0.0
		);
	}

	public void method_2936() {
		this.field_9240.removeAll(this.field_9227);

		for (int i = 0; i < this.field_9227.size(); i++) {
			class_1297 lv = (class_1297)this.field_9227.get(i);
			int j = lv.field_6024;
			int k = lv.field_5980;
			if (lv.field_6016 && this.method_8393(j, k)) {
				this.method_8497(j, k).method_12203(lv);
			}
		}

		for (int ix = 0; ix < this.field_9227.size(); ix++) {
			this.method_8539((class_1297)this.field_9227.get(ix));
		}

		this.field_9227.clear();

		for (int ix = 0; ix < this.field_9240.size(); ix++) {
			class_1297 lv = (class_1297)this.field_9240.get(ix);
			class_1297 lv2 = lv.method_5854();
			if (lv2 != null) {
				if (!lv2.field_5988 && lv2.method_5626(lv)) {
					continue;
				}

				lv.method_5848();
			}

			if (lv.field_5988) {
				int k = lv.field_6024;
				int l = lv.field_5980;
				if (lv.field_6016 && this.method_8393(k, l)) {
					this.method_8497(k, l).method_12203(lv);
				}

				this.field_9240.remove(ix--);
				this.method_8539(lv);
			}
		}
	}

	@Override
	public class_129 method_8538(class_128 arg) {
		class_129 lv = super.method_8538(arg);
		lv.method_577("Forced entities", () -> this.field_3728.size() + " total; " + this.field_3728);
		lv.method_577("Retry entities", () -> this.field_3726.size() + " total; " + this.field_3726);
		lv.method_577("Server brand", () -> this.field_3729.field_1724.method_3135());
		lv.method_577("Server type", () -> this.field_3729.method_1576() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
		return lv;
	}

	@Override
	public void method_8465(@Nullable class_1657 arg, double d, double e, double f, class_3414 arg2, class_3419 arg3, float g, float h) {
		if (arg == this.field_3729.field_1724) {
			this.method_8486(d, e, f, arg2, arg3, g, h, false);
		}
	}

	@Override
	public void method_8449(@Nullable class_1657 arg, class_1297 arg2, class_3414 arg3, class_3419 arg4, float f, float g) {
		if (arg == this.field_3729.field_1724) {
			this.field_3729.method_1483().method_4873(new class_1106(arg3, arg4, arg2));
		}
	}

	public void method_2947(class_2338 arg, class_3414 arg2, class_3419 arg3, float f, float g, boolean bl) {
		this.method_8486((double)arg.method_10263() + 0.5, (double)arg.method_10264() + 0.5, (double)arg.method_10260() + 0.5, arg2, arg3, f, g, bl);
	}

	@Override
	public void method_8486(double d, double e, double f, class_3414 arg, class_3419 arg2, float g, float h, boolean bl) {
		double i = this.field_3729.method_1560().method_5649(d, e, f);
		class_1109 lv = new class_1109(arg, arg2, g, h, (float)d, (float)e, (float)f);
		if (bl && i > 100.0) {
			double j = Math.sqrt(i) / 40.0;
			this.field_3729.method_1483().method_4872(lv, (int)(j * 20.0));
		} else {
			this.field_3729.method_1483().method_4873(lv);
		}
	}

	@Override
	public void method_8547(double d, double e, double f, double g, double h, double i, @Nullable class_2487 arg) {
		this.field_3729.field_1713.method_3058(new class_677.class_681(this, d, e, f, g, h, i, this.field_3729.field_1713, arg));
	}

	@Override
	public void method_8522(class_2596<?> arg) {
		this.field_3727.method_2883(arg);
	}

	@Override
	public class_1863 method_8433() {
		return this.field_3727.method_2877();
	}

	public void method_2944(class_269 arg) {
		this.field_3733 = arg;
	}

	@Override
	public void method_8435(long l) {
		if (l < 0L) {
			l = -l;
			this.method_8450().method_8359("doDaylightCycle", "false", null);
		} else {
			this.method_8450().method_8359("doDaylightCycle", "true", null);
		}

		super.method_8435(l);
	}

	@Override
	public class_1951<class_2248> method_8397() {
		return class_1925.method_8339();
	}

	@Override
	public class_1951<class_3611> method_8405() {
		return class_1925.method_8339();
	}

	public class_631 method_2935() {
		return (class_631)super.method_8398();
	}

	@Override
	public class_269 method_8428() {
		return this.field_3733;
	}

	@Override
	public class_3505 method_8514() {
		return this.field_3727.method_2867();
	}
}
