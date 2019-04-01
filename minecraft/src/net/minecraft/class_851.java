package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_851 {
	private volatile class_1937 field_4469;
	private final class_761 field_4466;
	public static int field_4460;
	public class_849 field_4459 = class_849.field_4451;
	private final ReentrantLock field_4468 = new ReentrantLock();
	private final ReentrantLock field_4470 = new ReentrantLock();
	private class_842 field_4461;
	private final Set<class_2586> field_4457 = Sets.<class_2586>newHashSet();
	private final FloatBuffer field_4465 = class_311.method_1597(16);
	private final class_291[] field_4462 = new class_291[class_1921.values().length];
	public class_238 field_4458;
	private int field_4471 = -1;
	private boolean field_4464 = true;
	private final class_2338.class_2339 field_4467 = new class_2338.class_2339(-1, -1, -1);
	private final class_2338.class_2339[] field_4472 = class_156.method_654(new class_2338.class_2339[6], args -> {
		for (int ix = 0; ix < args.length; ix++) {
			args[ix] = new class_2338.class_2339();
		}
	});
	private boolean field_4463;

	public class_851(class_1937 arg, class_761 arg2) {
		this.field_4469 = arg;
		this.field_4466 = arg2;
		if (GLX.useVbo()) {
			for (int i = 0; i < class_1921.values().length; i++) {
				this.field_4462[i] = new class_291(class_290.field_1582);
			}
		}
	}

	private static boolean method_3651(class_2338 arg, class_1937 arg2) {
		return !arg2.method_8497(arg.method_10263() >> 4, arg.method_10260() >> 4).method_12223();
	}

	public boolean method_3673() {
		class_2338 lv = new class_2338(class_310.method_1551().field_1724);
		class_2338 lv2 = this.method_3670();
		int i = 16;
		int j = 8;
		int k = 24;
		if (!(lv2.method_10069(8, 8, 8).method_10262(lv) > 576.0)) {
			return true;
		} else {
			class_1937 lv3 = this.method_3678();
			class_2338.class_2339 lv4 = new class_2338.class_2339(lv2);
			return method_3651(lv4.method_10101(lv2).method_10104(class_2350.field_11039, 16), lv3)
				&& method_3651(lv4.method_10101(lv2).method_10104(class_2350.field_11043, 16), lv3)
				&& method_3651(lv4.method_10101(lv2).method_10104(class_2350.field_11034, 16), lv3)
				&& method_3651(lv4.method_10101(lv2).method_10104(class_2350.field_11035, 16), lv3);
		}
	}

	public boolean method_3671(int i) {
		if (this.field_4471 == i) {
			return false;
		} else {
			this.field_4471 = i;
			return true;
		}
	}

	public class_291 method_3656(int i) {
		return this.field_4462[i];
	}

	public void method_3653(int i, int j, int k) {
		if (i != this.field_4467.method_10263() || j != this.field_4467.method_10264() || k != this.field_4467.method_10260()) {
			this.method_3675();
			this.field_4467.method_10103(i, j, k);
			this.field_4458 = new class_238((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));

			for (class_2350 lv : class_2350.values()) {
				this.field_4472[lv.ordinal()].method_10101(this.field_4467).method_10104(lv, 16);
			}

			this.method_3658();
		}
	}

	public void method_3657(float f, float g, float h, class_842 arg) {
		class_849 lv = arg.method_3609();
		if (lv.method_3644() != null && !lv.method_3641(class_1921.field_9179)) {
			this.method_3655(arg.method_3600().method_3154(class_1921.field_9179), this.field_4467);
			arg.method_3600().method_3154(class_1921.field_9179).method_1324(lv.method_3644());
			this.method_3666(class_1921.field_9179, f, g, h, arg.method_3600().method_3154(class_1921.field_9179), lv);
		}
	}

	public void method_3652(float f, float g, float h, class_842 arg) {
		class_849 lv = new class_849();
		int i = 1;
		class_2338 lv2 = this.field_4467.method_10062();
		class_2338 lv3 = lv2.method_10069(15, 15, 15);
		class_1937 lv4 = this.field_4469;
		if (lv4 != null) {
			arg.method_3605().lock();

			try {
				if (arg.method_3599() != class_842.class_843.field_4424) {
					return;
				}

				arg.method_3598(lv);
			} finally {
				arg.method_3605().unlock();
			}

			class_852 lv5 = new class_852();
			HashSet set = Sets.newHashSet();
			class_853 lv6 = arg.method_3606();
			if (lv6 != null) {
				field_4460++;
				boolean[] bls = new boolean[class_1921.values().length];
				class_778.method_3375();
				Random random = new Random();
				class_776 lv7 = class_310.method_1551().method_1541();

				for (class_2338 lv8 : class_2338.method_10097(lv2, lv3)) {
					class_2680 lv9 = lv6.method_8320(lv8);
					class_2248 lv10 = lv9.method_11614();
					if (lv9.method_11598(lv6, lv8)) {
						lv5.method_3682(lv8);
					}

					if (lv10.method_9570()) {
						class_2586 lv11 = lv6.method_3688(lv8, class_2818.class_2819.field_12859);
						if (lv11 != null) {
							class_827<class_2586> lv12 = class_824.field_4346.method_3553(lv11);
							if (lv12 != null) {
								lv.method_3646(lv11);
								if (lv12.method_3563(lv11)) {
									set.add(lv11);
								}
							}
						}
					}

					class_3610 lv13 = lv6.method_8316(lv8);
					if (!lv13.method_15769()) {
						class_1921 lv14 = lv13.method_15762();
						int j = lv14.ordinal();
						class_287 lv15 = arg.method_3600().method_3155(j);
						if (!lv.method_3649(lv14)) {
							lv.method_3647(lv14);
							this.method_3655(lv15, lv2);
						}

						bls[j] |= lv7.method_3352(lv8, lv6, lv15, lv13);
					}

					if (lv9.method_11610() != class_2464.field_11455) {
						class_1921 lv14 = lv10.method_9551();
						int j = lv14.ordinal();
						class_287 lv15 = arg.method_3600().method_3155(j);
						if (!lv.method_3649(lv14)) {
							lv.method_3647(lv14);
							this.method_3655(lv15, lv2);
						}

						bls[j] |= lv7.method_3355(lv9, lv8, lv6, lv15, random);
					}
				}

				for (class_1921 lv16 : class_1921.values()) {
					if (bls[lv16.ordinal()]) {
						lv.method_3643(lv16);
					}

					if (lv.method_3649(lv16)) {
						this.method_3666(lv16, f, g, h, arg.method_3600().method_3154(lv16), lv);
					}
				}

				class_778.method_3376();
			}

			lv.method_3640(lv5.method_3679());
			this.field_4468.lock();

			try {
				Set<class_2586> set2 = Sets.<class_2586>newHashSet(set);
				Set<class_2586> set3 = Sets.<class_2586>newHashSet(this.field_4457);
				set2.removeAll(this.field_4457);
				set3.removeAll(set);
				this.field_4457.clear();
				this.field_4457.addAll(set);
				this.field_4466.method_3245(set3, set2);
			} finally {
				this.field_4468.unlock();
			}
		}
	}

	protected void method_3663() {
		this.field_4468.lock();

		try {
			if (this.field_4461 != null && this.field_4461.method_3599() != class_842.class_843.field_4423) {
				this.field_4461.method_3596();
				this.field_4461 = null;
			}
		} finally {
			this.field_4468.unlock();
		}
	}

	public ReentrantLock method_3667() {
		return this.field_4468;
	}

	public class_842 method_3674() {
		this.field_4468.lock();

		class_842 var4;
		try {
			this.method_3663();
			class_2338 lv = this.field_4467.method_10062();
			int i = 1;
			class_853 lv2 = class_853.method_3689(this.field_4469, lv.method_10069(-1, -1, -1), lv.method_10069(16, 16, 16), 1);
			this.field_4461 = new class_842(this, class_842.class_844.field_4426, this.method_3668(), lv2);
			var4 = this.field_4461;
		} finally {
			this.field_4468.unlock();
		}

		return var4;
	}

	@Nullable
	public class_842 method_3669() {
		this.field_4468.lock();

		Object var1;
		try {
			if (this.field_4461 == null || this.field_4461.method_3599() != class_842.class_843.field_4422) {
				if (this.field_4461 != null && this.field_4461.method_3599() != class_842.class_843.field_4423) {
					this.field_4461.method_3596();
					this.field_4461 = null;
				}

				this.field_4461 = new class_842(this, class_842.class_844.field_4427, this.method_3668(), null);
				this.field_4461.method_3598(this.field_4459);
				return this.field_4461;
			}

			var1 = null;
		} finally {
			this.field_4468.unlock();
		}

		return (class_842)var1;
	}

	protected double method_3668() {
		class_4184 lv = class_310.method_1551().field_1773.method_19418();
		double d = this.field_4458.field_1323 + 8.0 - lv.method_19326().field_1352;
		double e = this.field_4458.field_1322 + 8.0 - lv.method_19326().field_1351;
		double f = this.field_4458.field_1321 + 8.0 - lv.method_19326().field_1350;
		return d * d + e * e + f * f;
	}

	private void method_3655(class_287 arg, class_2338 arg2) {
		arg.method_1328(7, class_290.field_1582);
		arg.method_1331((double)(-arg2.method_10263()), (double)(-arg2.method_10264()), (double)(-arg2.method_10260()));
	}

	private void method_3666(class_1921 arg, float f, float g, float h, class_287 arg2, class_849 arg3) {
		if (arg == class_1921.field_9179 && !arg3.method_3641(arg)) {
			arg2.method_1341(f, g, h);
			arg3.method_3648(arg2.method_1334());
		}

		arg2.method_1326();
	}

	private void method_3658() {
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		float f = 1.000001F;
		GlStateManager.translatef(-8.0F, -8.0F, -8.0F);
		GlStateManager.scalef(1.000001F, 1.000001F, 1.000001F);
		GlStateManager.translatef(8.0F, 8.0F, 8.0F);
		GlStateManager.getMatrix(2982, this.field_4465);
		GlStateManager.popMatrix();
	}

	public void method_3664() {
		GlStateManager.multMatrix(this.field_4465);
	}

	public class_849 method_3677() {
		return this.field_4459;
	}

	public void method_3665(class_849 arg) {
		this.field_4470.lock();

		try {
			this.field_4459 = arg;
		} finally {
			this.field_4470.unlock();
		}
	}

	public void method_3675() {
		this.method_3663();
		this.field_4459 = class_849.field_4451;
	}

	public void method_3659() {
		this.method_3675();
		this.field_4469 = null;

		for (int i = 0; i < class_1921.values().length; i++) {
			if (this.field_4462[i] != null) {
				this.field_4462[i].method_1355();
			}
		}
	}

	public class_2338 method_3670() {
		return this.field_4467;
	}

	public void method_3654(boolean bl) {
		if (this.field_4464) {
			bl |= this.field_4463;
		}

		this.field_4464 = true;
		this.field_4463 = bl;
	}

	public void method_3662() {
		this.field_4464 = false;
		this.field_4463 = false;
	}

	public boolean method_3672() {
		return this.field_4464;
	}

	public boolean method_3661() {
		return this.field_4464 && this.field_4463;
	}

	public class_2338 method_3676(class_2350 arg) {
		return this.field_4472[arg.ordinal()];
	}

	public class_1937 method_3678() {
		return this.field_4469;
	}
}
