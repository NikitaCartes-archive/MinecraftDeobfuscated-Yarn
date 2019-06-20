package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_1283 {
	private final List<class_1281> field_5870 = Lists.<class_1281>newArrayList();
	private final class_1309 field_5877;
	private int field_5876;
	private int field_5875;
	private int field_5873;
	private boolean field_5874;
	private boolean field_5872;
	private String field_5871;

	public class_1283(class_1309 arg) {
		this.field_5877 = arg;
	}

	public void method_5542() {
		this.method_5545();
		if (this.field_5877.method_6101()) {
			class_2248 lv = this.field_5877
				.field_6002
				.method_8320(new class_2338(this.field_5877.field_5987, this.field_5877.method_5829().field_1322, this.field_5877.field_6035))
				.method_11614();
			if (lv == class_2246.field_9983) {
				this.field_5871 = "ladder";
			} else if (lv == class_2246.field_10597) {
				this.field_5871 = "vines";
			}
		} else if (this.field_5877.method_5799()) {
			this.field_5871 = "water";
		}
	}

	public void method_5547(class_1282 arg, float f, float g) {
		this.method_5539();
		this.method_5542();
		class_1281 lv = new class_1281(arg, this.field_5877.field_6012, f, g, this.field_5871, this.field_5877.field_6017);
		this.field_5870.add(lv);
		this.field_5876 = this.field_5877.field_6012;
		this.field_5872 = true;
		if (lv.method_5502() && !this.field_5874 && this.field_5877.method_5805()) {
			this.field_5874 = true;
			this.field_5875 = this.field_5877.field_6012;
			this.field_5873 = this.field_5875;
			this.field_5877.method_6000();
		}
	}

	public class_2561 method_5548() {
		if (this.field_5870.isEmpty()) {
			return new class_2588("death.attack.generic", this.field_5877.method_5476());
		} else {
			class_1281 lv = this.method_5544();
			class_1281 lv2 = (class_1281)this.field_5870.get(this.field_5870.size() - 1);
			class_2561 lv3 = lv2.method_5498();
			class_1297 lv4 = lv2.method_5499().method_5529();
			class_2561 lv6;
			if (lv != null && lv2.method_5499() == class_1282.field_5868) {
				class_2561 lv5 = lv.method_5498();
				if (lv.method_5499() == class_1282.field_5868 || lv.method_5499() == class_1282.field_5849) {
					lv6 = new class_2588("death.fell.accident." + this.method_5543(lv), this.field_5877.method_5476());
				} else if (lv5 != null && (lv3 == null || !lv5.equals(lv3))) {
					class_1297 lv7 = lv.method_5499().method_5529();
					class_1799 lv8 = lv7 instanceof class_1309 ? ((class_1309)lv7).method_6047() : class_1799.field_8037;
					if (!lv8.method_7960() && lv8.method_7938()) {
						lv6 = new class_2588("death.fell.assist.item", this.field_5877.method_5476(), lv5, lv8.method_7954());
					} else {
						lv6 = new class_2588("death.fell.assist", this.field_5877.method_5476(), lv5);
					}
				} else if (lv3 != null) {
					class_1799 lv9 = lv4 instanceof class_1309 ? ((class_1309)lv4).method_6047() : class_1799.field_8037;
					if (!lv9.method_7960() && lv9.method_7938()) {
						lv6 = new class_2588("death.fell.finish.item", this.field_5877.method_5476(), lv3, lv9.method_7954());
					} else {
						lv6 = new class_2588("death.fell.finish", this.field_5877.method_5476(), lv3);
					}
				} else {
					lv6 = new class_2588("death.fell.killer", this.field_5877.method_5476());
				}
			} else {
				lv6 = lv2.method_5499().method_5506(this.field_5877);
			}

			return lv6;
		}
	}

	@Nullable
	public class_1309 method_5541() {
		class_1309 lv = null;
		class_1657 lv2 = null;
		float f = 0.0F;
		float g = 0.0F;

		for (class_1281 lv3 : this.field_5870) {
			if (lv3.method_5499().method_5529() instanceof class_1657 && (lv2 == null || lv3.method_5503() > g)) {
				g = lv3.method_5503();
				lv2 = (class_1657)lv3.method_5499().method_5529();
			}

			if (lv3.method_5499().method_5529() instanceof class_1309 && (lv == null || lv3.method_5503() > f)) {
				f = lv3.method_5503();
				lv = (class_1309)lv3.method_5499().method_5529();
			}
		}

		return (class_1309)(lv2 != null && g >= f / 3.0F ? lv2 : lv);
	}

	@Nullable
	private class_1281 method_5544() {
		class_1281 lv = null;
		class_1281 lv2 = null;
		float f = 0.0F;
		float g = 0.0F;

		for (int i = 0; i < this.field_5870.size(); i++) {
			class_1281 lv3 = (class_1281)this.field_5870.get(i);
			class_1281 lv4 = i > 0 ? (class_1281)this.field_5870.get(i - 1) : null;
			if ((lv3.method_5499() == class_1282.field_5868 || lv3.method_5499() == class_1282.field_5849)
				&& lv3.method_5501() > 0.0F
				&& (lv == null || lv3.method_5501() > g)) {
				if (i > 0) {
					lv = lv4;
				} else {
					lv = lv3;
				}

				g = lv3.method_5501();
			}

			if (lv3.method_5500() != null && (lv2 == null || lv3.method_5503() > f)) {
				lv2 = lv3;
				f = lv3.method_5503();
			}
		}

		if (g > 5.0F && lv != null) {
			return lv;
		} else {
			return f > 5.0F && lv2 != null ? lv2 : null;
		}
	}

	private String method_5543(class_1281 arg) {
		return arg.method_5500() == null ? "generic" : arg.method_5500();
	}

	public int method_5546() {
		return this.field_5874 ? this.field_5877.field_6012 - this.field_5875 : this.field_5873 - this.field_5875;
	}

	private void method_5545() {
		this.field_5871 = null;
	}

	public void method_5539() {
		int i = this.field_5874 ? 300 : 100;
		if (this.field_5872 && (!this.field_5877.method_5805() || this.field_5877.field_6012 - this.field_5876 > i)) {
			boolean bl = this.field_5874;
			this.field_5872 = false;
			this.field_5874 = false;
			this.field_5873 = this.field_5877.field_6012;
			if (bl) {
				this.field_5877.method_6044();
			}

			this.field_5870.clear();
		}
	}

	public class_1309 method_5540() {
		return this.field_5877;
	}
}
