package net.minecraft;

import java.util.List;

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

		class_2338 lv = this.method_11016();
		if (this.field_17095 >= 50) {
			this.field_17096 = false;
			this.field_17095 = 0;
		}

		if (this.field_17095 >= 5 && this.field_19158 == 0) {
			this.method_20216(this.field_11863, lv);
		}

		if (this.field_19157) {
			if (this.field_19158 < 40) {
				this.field_19158++;
			} else {
				this.field_19157 = false;
				this.method_20218(this.field_11863, lv);
			}
		}
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

	private void method_20216(class_1937 arg, class_2338 arg2) {
		for (class_1309 lv : this.field_19156) {
			if (lv.method_5805() && !lv.field_5988 && arg2.method_19769(lv.method_19538(), 32.0) && lv.method_5864().method_20210(class_3483.field_19168)) {
				this.field_19157 = true;
			}
		}

		if (this.field_19157) {
			arg.method_8396(null, arg2, class_3417.field_19167, class_3419.field_15245, 1.0F, 1.0F);
		}
	}

	private void method_20218(class_1937 arg, class_2338 arg2) {
		int i = 16700985;
		int j = (int)this.field_19156.stream().filter(arg2x -> arg2.method_19769(arg2x.method_19538(), 32.0)).count();

		for (class_1309 lv : this.field_19156) {
			if (lv.method_5805() && !lv.field_5988 && arg2.method_19769(lv.method_19538(), 32.0) && lv.method_5864().method_20210(class_3483.field_19168)) {
				if (!arg.field_9236) {
					lv.method_6092(new class_1293(class_1294.field_5912, 60));
				} else {
					float f = 1.0F;
					float g = class_3532.method_15368(
						(lv.field_5987 - (double)arg2.method_10263()) * (lv.field_5987 - (double)arg2.method_10263())
							+ (lv.field_6035 - (double)arg2.method_10260()) * (lv.field_6035 - (double)arg2.method_10260())
					);
					double d = (double)((float)arg2.method_10263() + 0.5F) + (double)(1.0F / g) * (lv.field_5987 - (double)arg2.method_10263());
					double e = (double)((float)arg2.method_10260() + 0.5F) + (double)(1.0F / g) * (lv.field_6035 - (double)arg2.method_10260());
					int k = class_3532.method_15340((j - 21) / -2, 3, 15);

					for (int l = 0; l < k; l++) {
						i += 5;
						double h = (double)(i >> 16 & 0xFF) / 255.0;
						double m = (double)(i >> 8 & 0xFF) / 255.0;
						double n = (double)(i & 0xFF) / 255.0;
						arg.method_8406(class_2398.field_11226, d, (double)((float)arg2.method_10264() + 0.5F), e, h, m, n);
					}
				}
			}
		}
	}
}
