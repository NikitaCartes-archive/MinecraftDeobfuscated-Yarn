package net.minecraft;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class class_3721 extends class_2586 implements class_3000 {
	private long field_19155;
	public int field_17095;
	public boolean field_17096;
	public class_2350 field_17097;
	private List<class_1309> field_19156;
	private boolean field_19157;
	private int field_19158;

	public class_3721() {
		super(class_2591.field_16413);
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.method_20219();
			this.field_19158 = 0;
			this.field_17097 = class_2350.method_10143(j);
			this.field_17095 = 0;
			this.field_17096 = true;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void method_16896() {
		if (this.field_17096) {
			this.field_17095++;
		}

		if (this.field_17095 >= 50) {
			this.field_17096 = false;
			this.field_17095 = 0;
		}

		if (this.field_17095 >= 5 && this.field_19158 == 0 && this.method_20523()) {
			this.field_19157 = true;
			this.method_20522();
		}

		if (this.field_19157) {
			if (this.field_19158 < 40) {
				this.field_19158++;
			} else {
				this.method_20521(this.field_11863);
				this.method_20218(this.field_11863);
				this.field_19157 = false;
			}
		}
	}

	private void method_20522() {
		this.field_11863.method_8396(null, this.method_11016(), class_3417.field_19167, class_3419.field_15245, 1.0F, 1.0F);
	}

	public void method_17031(class_2350 arg) {
		class_2338 lv = this.method_11016();
		this.field_17097 = arg;
		if (this.field_17096) {
			this.field_17095 = 0;
		} else {
			this.field_17096 = true;
		}

		this.field_11863.method_8427(lv, this.method_11010().method_11614(), 1, arg.method_10146());
	}

	private void method_20219() {
		class_2338 lv = this.method_11016();
		if (this.field_11863.method_8510() > this.field_19155 + 60L || this.field_19156 == null) {
			this.field_19155 = this.field_11863.method_8510();
			class_238 lv2 = new class_238(lv).method_1014(48.0);
			this.field_19156 = this.field_11863.method_18467(class_1309.class, lv2);
		}

		if (!this.field_11863.field_9236) {
			for (class_1309 lv3 : this.field_19156) {
				if (lv3.method_5805() && !lv3.field_5988 && lv.method_19769(lv3.method_19538(), 32.0)) {
					lv3.method_18868().method_18878(class_4140.field_19009, this.field_11863.method_8510());
				}
			}
		}
	}

	private boolean method_20523() {
		class_2338 lv = this.method_11016();

		for (class_1309 lv2 : this.field_19156) {
			if (lv2.method_5805() && !lv2.field_5988 && lv.method_19769(lv2.method_19538(), 32.0) && lv2.method_5864().method_20210(class_3483.field_19168)) {
				return true;
			}
		}

		return false;
	}

	private void method_20521(class_1937 arg) {
		if (!arg.field_9236) {
			this.field_19156.stream().filter(this::method_20518).forEach(this::method_20520);
		}
	}

	private void method_20218(class_1937 arg) {
		if (arg.field_9236) {
			class_2338 lv = this.method_11016();
			AtomicInteger atomicInteger = new AtomicInteger(16700985);
			int i = (int)this.field_19156.stream().filter(arg2 -> lv.method_19769(arg2.method_19538(), 48.0)).count();
			this.field_19156
				.stream()
				.filter(this::method_20518)
				.forEach(
					arg3 -> {
						float f = 1.0F;
						float g = class_3532.method_15368(
							(arg3.field_5987 - (double)lv.method_10263()) * (arg3.field_5987 - (double)lv.method_10263())
								+ (arg3.field_6035 - (double)lv.method_10260()) * (arg3.field_6035 - (double)lv.method_10260())
						);
						double d = (double)((float)lv.method_10263() + 0.5F) + (double)(1.0F / g) * (arg3.field_5987 - (double)lv.method_10263());
						double e = (double)((float)lv.method_10260() + 0.5F) + (double)(1.0F / g) * (arg3.field_6035 - (double)lv.method_10260());
						int j = class_3532.method_15340((i - 21) / -2, 3, 15);

						for (int k = 0; k < j; k++) {
							atomicInteger.addAndGet(5);
							double h = (double)(atomicInteger.get() >> 16 & 0xFF) / 255.0;
							double l = (double)(atomicInteger.get() >> 8 & 0xFF) / 255.0;
							double m = (double)(atomicInteger.get() & 0xFF) / 255.0;
							arg.method_8406(class_2398.field_11226, d, (double)((float)lv.method_10264() + 0.5F), e, h, l, m);
						}
					}
				);
		}
	}

	private boolean method_20518(class_1309 arg) {
		return arg.method_5805()
			&& !arg.field_5988
			&& this.method_11016().method_19769(arg.method_19538(), 48.0)
			&& arg.method_5864().method_20210(class_3483.field_19168);
	}

	private void method_20520(class_1309 arg) {
		arg.method_6092(new class_1293(class_1294.field_5912, 60));
	}
}
