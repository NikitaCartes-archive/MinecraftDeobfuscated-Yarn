package net.minecraft;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_631 extends class_2802 {
	private static final Logger field_3679 = LogManager.getLogger();
	private final class_310 field_16250 = class_310.method_1551();
	private final class_2818 field_3676;
	private final class_3568 field_3677;
	private volatile class_631.class_3681 field_16246 = new class_631.class_3681(3);
	private int field_16249;
	private volatile int field_16248;
	private volatile int field_16247;
	private final class_1922 field_16525;

	public class_631(class_1937 arg) {
		this.field_16525 = arg;
		this.field_3676 = new class_2812(arg, 0, 0);
		this.field_3677 = new class_3568(this, true, arg.method_8597().method_12451());
	}

	private static boolean method_16024(int i, int j, int k, int l, int m) {
		return Math.abs(i - k) <= m && Math.abs(j - l) <= m;
	}

	@Override
	public class_3568 method_12130() {
		return this.field_3677;
	}

	public void method_2859(int i, int j) {
		this.field_16246.method_16031(i, j);
	}

	@Nullable
	public class_2818 method_2857(int i, int j, class_2806 arg, boolean bl) {
		class_2818 lv = this.field_16246.method_16033(i, j);
		if (lv != null) {
			return lv;
		} else {
			return bl ? this.field_3676 : null;
		}
	}

	@Override
	public class_1922 method_16399() {
		return this.field_16525;
	}

	public void method_16020(class_1937 arg, int i, int j, class_2540 arg2, class_2487 arg3, int k, boolean bl) {
		this.method_16026();
		if (!this.field_16246.method_16034(i, j)) {
			field_3679.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
		} else {
			int l = this.field_16246.method_16027(i, j);
			class_2818 lv = (class_2818)this.field_16246.field_16251.get(l);
			if (lv == null) {
				lv = new class_2818(arg, i, j, new class_1959[256]);
				this.field_16246.field_16251.set(l, lv);
				this.field_16249++;
			}

			lv.method_12224(arg2, arg3, k, bl);
			lv.method_12226(true);
			class_2826[] lvs = lv.method_12006();
			class_3568 lv2 = this.method_12130();

			for (int m = 0; m < lvs.length; m++) {
				class_2826 lv3 = lvs[m];
				lv2.method_15551(i, m, j, lv3 == class_2818.field_12852 || lv3.method_12261());
			}
		}
	}

	@Override
	public void method_12127(BooleanSupplier booleanSupplier) {
		this.method_16026();
	}

	private void method_16026() {
		int i = this.field_16246.field_16253;
		int j = Math.max(2, this.field_16250.field_1690.field_1870 + -2) + 2;
		if (i != j) {
			class_631.class_3681 lv = new class_631.class_3681(j);

			for (int k = this.field_16247 - i; k <= this.field_16247 + i; k++) {
				for (int l = this.field_16248 - i; l <= this.field_16248 + i; l++) {
					class_2818 lv2 = (class_2818)this.field_16246.field_16251.get(this.field_16246.method_16027(l, k));
					if (lv2 != null) {
						if (!method_16024(l, k, this.field_16248, this.field_16247, j)) {
							this.field_16249--;
						} else {
							lv.field_16251.set(lv.method_16027(l, k), lv2);
						}
					}
				}
			}

			this.field_16246 = lv;
		}

		int m = class_3532.method_15357(this.field_16250.field_1724.field_5987) >> 4;
		int k = class_3532.method_15357(this.field_16250.field_1724.field_6035) >> 4;
		if (this.field_16248 != m || this.field_16247 != k) {
			for (int lx = this.field_16247 - j; lx <= this.field_16247 + j; lx++) {
				for (int n = this.field_16248 - j; n <= this.field_16248 + j; n++) {
					if (!method_16024(n, lx, m, k, j)) {
						this.field_16246.field_16251.set(this.field_16246.method_16027(n, lx), null);
					}
				}
			}

			this.field_16248 = m;
			this.field_16247 = k;
		}
	}

	@Override
	public String method_12122() {
		return "MultiplayerChunkCache: " + this.field_16246.field_16251.length() + ", " + this.field_16249;
	}

	@Override
	public class_2794<?> method_12129() {
		return null;
	}

	@Override
	public void method_12247(class_1944 arg, int i, int j, int k) {
		class_310.method_1551().field_1769.method_8571(i, j, k);
	}

	@Environment(EnvType.CLIENT)
	final class class_3681 {
		private final AtomicReferenceArray<class_2818> field_16251;
		private final int field_16253;
		private final int field_16252;

		private class_3681(int i) {
			this.field_16253 = i;
			this.field_16252 = i * 2 + 1;
			this.field_16251 = new AtomicReferenceArray(this.field_16252 * this.field_16252);
		}

		private int method_16027(int i, int j) {
			return Math.floorMod(j, this.field_16252) * this.field_16252 + Math.floorMod(i, this.field_16252);
		}

		protected void method_16031(int i, int j) {
			if (this.method_16034(i, j)) {
				class_2818 lv = (class_2818)this.field_16251.getAndSet(this.method_16027(i, j), null);
				if (lv != null) {
					class_631.this.field_16249--;
					lv.method_12213();
				}
			}
		}

		private boolean method_16034(int i, int j) {
			return class_631.method_16024(i, j, class_631.this.field_16248, class_631.this.field_16247, this.field_16253);
		}

		@Nullable
		protected class_2818 method_16033(int i, int j) {
			if (this.method_16034(i, j)) {
				class_2818 lv = (class_2818)this.field_16251.get(this.method_16027(i, j));
				if (lv != null) {
					return lv;
				}
			}

			return null;
		}
	}
}
