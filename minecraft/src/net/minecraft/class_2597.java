package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2597 extends class_2586 implements class_3000 {
	private static final class_2248[] field_11931 = new class_2248[]{
		class_2246.field_10135, class_2246.field_10006, class_2246.field_10174, class_2246.field_10297
	};
	public int field_11936;
	private float field_11932;
	private boolean field_11934;
	private boolean field_11933;
	private final List<class_2338> field_11937 = Lists.<class_2338>newArrayList();
	@Nullable
	private class_1309 field_11939;
	@Nullable
	private UUID field_11935;
	private long field_11938;

	public class_2597() {
		this(class_2591.field_11902);
	}

	public class_2597(class_2591<?> arg) {
		super(arg);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10545("target_uuid")) {
			this.field_11935 = class_2512.method_10690(arg.method_10562("target_uuid"));
		} else {
			this.field_11935 = null;
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (this.field_11939 != null) {
			arg.method_10566("target_uuid", class_2512.method_10689(this.field_11939.method_5667()));
		}

		return arg;
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 5, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	@Override
	public void method_16896() {
		this.field_11936++;
		long l = this.field_11863.method_8510();
		if (l % 40L == 0L) {
			this.method_11057(this.method_11069());
			if (!this.field_11863.field_9236 && this.method_11065()) {
				this.method_11055();
				this.method_11068();
			}
		}

		if (l % 80L == 0L && this.method_11065()) {
			this.method_11067(class_3417.field_14632);
		}

		if (l > this.field_11938 && this.method_11065()) {
			this.field_11938 = l + 60L + (long)this.field_11863.method_8409().nextInt(40);
			this.method_11067(class_3417.field_15071);
		}

		if (this.field_11863.field_9236) {
			this.method_11064();
			this.method_11063();
			if (this.method_11065()) {
				this.field_11932++;
			}
		}
	}

	private boolean method_11069() {
		this.field_11937.clear();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					class_2338 lv = this.field_11867.method_10069(i, j, k);
					if (!this.field_11863.method_8585(lv)) {
						return false;
					}
				}
			}
		}

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int kx = -2; kx <= 2; kx++) {
					int l = Math.abs(i);
					int m = Math.abs(j);
					int n = Math.abs(kx);
					if ((l > 1 || m > 1 || n > 1) && (i == 0 && (m == 2 || n == 2) || j == 0 && (l == 2 || n == 2) || kx == 0 && (l == 2 || m == 2))) {
						class_2338 lv2 = this.field_11867.method_10069(i, j, kx);
						class_2680 lv3 = this.field_11863.method_8320(lv2);

						for (class_2248 lv4 : field_11931) {
							if (lv3.method_11614() == lv4) {
								this.field_11937.add(lv2);
							}
						}
					}
				}
			}
		}

		this.method_11062(this.field_11937.size() >= 42);
		return this.field_11937.size() >= 16;
	}

	private void method_11055() {
		int i = this.field_11937.size();
		int j = i / 7 * 16;
		int k = this.field_11867.method_10263();
		int l = this.field_11867.method_10264();
		int m = this.field_11867.method_10260();
		class_238 lv = new class_238((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
			.method_1014((double)j)
			.method_1012(0.0, (double)this.field_11863.method_8322(), 0.0);
		List<class_1657> list = this.field_11863.method_18467(class_1657.class, lv);
		if (!list.isEmpty()) {
			for (class_1657 lv2 : list) {
				if (this.field_11867.method_19771(new class_2338(lv2), (double)j) && lv2.method_5721()) {
					lv2.method_6092(new class_1293(class_1294.field_5927, 260, 0, true, true));
				}
			}
		}
	}

	private void method_11068() {
		class_1309 lv = this.field_11939;
		int i = this.field_11937.size();
		if (i < 42) {
			this.field_11939 = null;
		} else if (this.field_11939 == null && this.field_11935 != null) {
			this.field_11939 = this.method_11056();
			this.field_11935 = null;
		} else if (this.field_11939 == null) {
			List<class_1309> list = this.field_11863.method_8390(class_1309.class, this.method_11059(), arg -> arg instanceof class_1569 && arg.method_5721());
			if (!list.isEmpty()) {
				this.field_11939 = (class_1309)list.get(this.field_11863.field_9229.nextInt(list.size()));
			}
		} else if (!this.field_11939.method_5805() || !this.field_11867.method_19771(new class_2338(this.field_11939), 8.0)) {
			this.field_11939 = null;
		}

		if (this.field_11939 != null) {
			this.field_11863
				.method_8465(
					null, this.field_11939.field_5987, this.field_11939.field_6010, this.field_11939.field_6035, class_3417.field_15177, class_3419.field_15245, 1.0F, 1.0F
				);
			this.field_11939.method_5643(class_1282.field_5846, 4.0F);
		}

		if (lv != this.field_11939) {
			class_2680 lv2 = this.method_11010();
			this.field_11863.method_8413(this.field_11867, lv2, lv2, 2);
		}
	}

	private void method_11064() {
		if (this.field_11935 == null) {
			this.field_11939 = null;
		} else if (this.field_11939 == null || !this.field_11939.method_5667().equals(this.field_11935)) {
			this.field_11939 = this.method_11056();
			if (this.field_11939 == null) {
				this.field_11935 = null;
			}
		}
	}

	private class_238 method_11059() {
		int i = this.field_11867.method_10263();
		int j = this.field_11867.method_10264();
		int k = this.field_11867.method_10260();
		return new class_238((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)).method_1014(8.0);
	}

	@Nullable
	private class_1309 method_11056() {
		List<class_1309> list = this.field_11863.method_8390(class_1309.class, this.method_11059(), arg -> arg.method_5667().equals(this.field_11935));
		return list.size() == 1 ? (class_1309)list.get(0) : null;
	}

	private void method_11063() {
		Random random = this.field_11863.field_9229;
		float f = class_3532.method_15374((float)(this.field_11936 + 35) * 0.1F) / 2.0F + 0.5F;
		f = (f * f + f) * 0.3F;
		class_243 lv = new class_243(
			(double)((float)this.field_11867.method_10263() + 0.5F),
			(double)((float)this.field_11867.method_10264() + 1.5F + f),
			(double)((float)this.field_11867.method_10260() + 0.5F)
		);

		for (class_2338 lv2 : this.field_11937) {
			if (random.nextInt(50) == 0) {
				float g = -0.5F + random.nextFloat();
				float h = -2.0F + random.nextFloat();
				float i = -0.5F + random.nextFloat();
				class_2338 lv3 = lv2.method_10059(this.field_11867);
				class_243 lv4 = new class_243((double)g, (double)h, (double)i)
					.method_1031((double)lv3.method_10263(), (double)lv3.method_10264(), (double)lv3.method_10260());
				this.field_11863.method_8406(class_2398.field_11229, lv.field_1352, lv.field_1351, lv.field_1350, lv4.field_1352, lv4.field_1351, lv4.field_1350);
			}
		}

		if (this.field_11939 != null) {
			class_243 lv5 = new class_243(this.field_11939.field_5987, this.field_11939.field_6010 + (double)this.field_11939.method_5751(), this.field_11939.field_6035);
			float j = (-0.5F + random.nextFloat()) * (3.0F + this.field_11939.method_17681());
			float g = -1.0F + random.nextFloat() * this.field_11939.method_17682();
			float h = (-0.5F + random.nextFloat()) * (3.0F + this.field_11939.method_17681());
			class_243 lv6 = new class_243((double)j, (double)g, (double)h);
			this.field_11863.method_8406(class_2398.field_11229, lv5.field_1352, lv5.field_1351, lv5.field_1350, lv6.field_1352, lv6.field_1351, lv6.field_1350);
		}
	}

	public boolean method_11065() {
		return this.field_11934;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11066() {
		return this.field_11933;
	}

	private void method_11057(boolean bl) {
		if (bl != this.field_11934) {
			this.method_11067(bl ? class_3417.field_14700 : class_3417.field_14979);
		}

		this.field_11934 = bl;
	}

	private void method_11062(boolean bl) {
		this.field_11933 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float method_11061(float f) {
		return (this.field_11932 + f) * -0.0375F;
	}

	public void method_11067(class_3414 arg) {
		this.field_11863.method_8396(null, this.field_11867, arg, class_3419.field_15245, 1.0F, 1.0F);
	}
}
