package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_194 {
	private final class_161 field_1263;
	private final class_194 field_1258;
	private final class_194 field_1260;
	private final int field_1261;
	private final List<class_194> field_1267 = Lists.<class_194>newArrayList();
	private class_194 field_1262;
	private class_194 field_1264;
	private int field_1259;
	private float field_1269;
	private float field_1268;
	private float field_1266;
	private float field_1265;

	public class_194(class_161 arg, @Nullable class_194 arg2, @Nullable class_194 arg3, int i, int j) {
		if (arg.method_686() == null) {
			throw new IllegalArgumentException("Can't position an invisible advancement!");
		} else {
			this.field_1263 = arg;
			this.field_1258 = arg2;
			this.field_1260 = arg3;
			this.field_1261 = i;
			this.field_1262 = this;
			this.field_1259 = j;
			this.field_1269 = -1.0F;
			class_194 lv = null;

			for (class_161 lv2 : arg.method_681()) {
				lv = this.method_846(lv2, lv);
			}
		}
	}

	@Nullable
	private class_194 method_846(class_161 arg, @Nullable class_194 arg2) {
		if (arg.method_686() != null) {
			arg2 = new class_194(arg, this, arg2, this.field_1267.size() + 1, this.field_1259 + 1);
			this.field_1267.add(arg2);
		} else {
			for (class_161 lv : arg.method_681()) {
				arg2 = this.method_846(lv, arg2);
			}
		}

		return arg2;
	}

	private void method_847() {
		if (this.field_1267.isEmpty()) {
			if (this.field_1260 != null) {
				this.field_1269 = this.field_1260.field_1269 + 1.0F;
			} else {
				this.field_1269 = 0.0F;
			}
		} else {
			class_194 lv = null;

			for (class_194 lv2 : this.field_1267) {
				lv2.method_847();
				lv = lv2.method_841(lv == null ? lv2 : lv);
			}

			this.method_850();
			float f = (((class_194)this.field_1267.get(0)).field_1269 + ((class_194)this.field_1267.get(this.field_1267.size() - 1)).field_1269) / 2.0F;
			if (this.field_1260 != null) {
				this.field_1269 = this.field_1260.field_1269 + 1.0F;
				this.field_1268 = this.field_1269 - f;
			} else {
				this.field_1269 = f;
			}
		}
	}

	private float method_842(float f, int i, float g) {
		this.field_1269 += f;
		this.field_1259 = i;
		if (this.field_1269 < g) {
			g = this.field_1269;
		}

		for (class_194 lv : this.field_1267) {
			g = lv.method_842(f + this.field_1268, i + 1, g);
		}

		return g;
	}

	private void method_843(float f) {
		this.field_1269 += f;

		for (class_194 lv : this.field_1267) {
			lv.method_843(f);
		}
	}

	private void method_850() {
		float f = 0.0F;
		float g = 0.0F;

		for (int i = this.field_1267.size() - 1; i >= 0; i--) {
			class_194 lv = (class_194)this.field_1267.get(i);
			lv.field_1269 += f;
			lv.field_1268 += f;
			g += lv.field_1266;
			f += lv.field_1265 + g;
		}
	}

	@Nullable
	private class_194 method_849() {
		if (this.field_1264 != null) {
			return this.field_1264;
		} else {
			return !this.field_1267.isEmpty() ? (class_194)this.field_1267.get(0) : null;
		}
	}

	@Nullable
	private class_194 method_844() {
		if (this.field_1264 != null) {
			return this.field_1264;
		} else {
			return !this.field_1267.isEmpty() ? (class_194)this.field_1267.get(this.field_1267.size() - 1) : null;
		}
	}

	private class_194 method_841(class_194 arg) {
		if (this.field_1260 == null) {
			return arg;
		} else {
			class_194 lv = this;
			class_194 lv2 = this;
			class_194 lv3 = this.field_1260;
			class_194 lv4 = (class_194)this.field_1258.field_1267.get(0);
			float f = this.field_1268;
			float g = this.field_1268;
			float h = lv3.field_1268;

			float i;
			for (i = lv4.field_1268; lv3.method_844() != null && lv.method_849() != null; g += lv2.field_1268) {
				lv3 = lv3.method_844();
				lv = lv.method_849();
				lv4 = lv4.method_849();
				lv2 = lv2.method_844();
				lv2.field_1262 = this;
				float j = lv3.field_1269 + h - (lv.field_1269 + f) + 1.0F;
				if (j > 0.0F) {
					lv3.method_845(this, arg).method_848(this, j);
					f += j;
					g += j;
				}

				h += lv3.field_1268;
				f += lv.field_1268;
				i += lv4.field_1268;
			}

			if (lv3.method_844() != null && lv2.method_844() == null) {
				lv2.field_1264 = lv3.method_844();
				lv2.field_1268 += h - g;
			} else {
				if (lv.method_849() != null && lv4.method_849() == null) {
					lv4.field_1264 = lv.method_849();
					lv4.field_1268 += f - i;
				}

				arg = this;
			}

			return arg;
		}
	}

	private void method_848(class_194 arg, float f) {
		float g = (float)(arg.field_1261 - this.field_1261);
		if (g != 0.0F) {
			arg.field_1266 -= f / g;
			this.field_1266 += f / g;
		}

		arg.field_1265 += f;
		arg.field_1269 += f;
		arg.field_1268 += f;
	}

	private class_194 method_845(class_194 arg, class_194 arg2) {
		return this.field_1262 != null && arg.field_1258.field_1267.contains(this.field_1262) ? this.field_1262 : arg2;
	}

	private void method_851() {
		if (this.field_1263.method_686() != null) {
			this.field_1263.method_686().method_816((float)this.field_1259, this.field_1269);
		}

		if (!this.field_1267.isEmpty()) {
			for (class_194 lv : this.field_1267) {
				lv.method_851();
			}
		}
	}

	public static void method_852(class_161 arg) {
		if (arg.method_686() == null) {
			throw new IllegalArgumentException("Can't position children of an invisible root!");
		} else {
			class_194 lv = new class_194(arg, null, null, 1, 0);
			lv.method_847();
			float f = lv.method_842(0.0F, 0, lv.field_1269);
			if (f < 0.0F) {
				lv.method_843(-f);
			}

			lv.method_851();
		}
	}
}
