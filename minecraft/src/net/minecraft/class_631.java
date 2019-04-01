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
	private volatile class_631.class_3681 field_16246;
	private volatile int field_16249;
	private volatile int field_16247;
	private final class_638 field_16525;

	public class_631(class_638 arg, int i) {
		this.field_16525 = arg;
		this.field_3676 = new class_2812(arg, new class_1923(0, 0));
		this.field_3677 = new class_3568(this, true, arg.method_8597().method_12451());
		this.field_16246 = new class_631.class_3681(method_20230(i));
	}

	@Override
	public class_3568 method_12130() {
		return this.field_3677;
	}

	private static boolean method_20181(class_2818 arg, int i, int j) {
		class_1923 lv = arg.method_12004();
		return lv.field_9181 == i && lv.field_9180 == j;
	}

	public void method_2859(int i, int j) {
		if (this.field_16246.method_16034(i, j)) {
			int k = this.field_16246.method_16027(i, j);
			class_2818 lv = this.field_16246.method_16033(k);
			if (lv != null && method_20181(lv, i, j)) {
				this.field_16246.method_20183(k, lv, null);
			}
		}
	}

	@Nullable
	public class_2818 method_2857(int i, int j, class_2806 arg, boolean bl) {
		if (this.field_16246.method_16034(i, j)) {
			int k = this.field_16246.method_16027(i, j);
			class_2818 lv = this.field_16246.method_16033(k);
			if (lv != null) {
				if (method_20181(lv, i, j)) {
					return lv;
				}

				this.field_16246.method_20183(k, lv, null);
			}
		}

		return bl ? this.field_3676 : null;
	}

	@Override
	public class_1922 method_16399() {
		return this.field_16525;
	}

	@Nullable
	public class_2818 method_16020(class_1937 arg, int i, int j, class_2540 arg2, class_2487 arg3, int k, boolean bl) {
		this.method_16026();
		if (!this.field_16246.method_16034(i, j)) {
			field_3679.warn("Ignoring chunk since it's not in the view range: {}, {}", i, j);
			return null;
		} else {
			int l = this.field_16246.method_16027(i, j);
			class_2818 lv = (class_2818)this.field_16246.field_16251.get(l);
			if (lv != null && method_20181(lv, i, j)) {
				lv.method_12224(arg2, arg3, k, bl);
			} else {
				if (!bl) {
					field_3679.warn("Ignoring chunk since we don't have complete data: {}, {}", i, j);
					return null;
				}

				lv = new class_2818(arg, new class_1923(i, j), new class_1959[256]);
				lv.method_12224(arg2, arg3, k, bl);
				this.field_16246.method_16031(l, lv);
			}

			class_2826[] lvs = lv.method_12006();
			class_3568 lv2 = this.method_12130();

			for (int m = 0; m < lvs.length; m++) {
				class_2826 lv3 = lvs[m];
				lv2.method_15551(class_4076.method_18676(i, m, j), class_2826.method_18090(lv3));
			}

			return lv;
		}
	}

	@Override
	public void method_12127(BooleanSupplier booleanSupplier) {
		this.method_16026();
	}

	private void method_16026() {
		this.field_16249 = class_3532.method_15357(this.field_16250.field_1724.field_5987) >> 4;
		this.field_16247 = class_3532.method_15357(this.field_16250.field_1724.field_6035) >> 4;
	}

	public void method_20180(int i) {
		this.method_16026();
		int j = this.field_16246.field_16253;
		int k = method_20230(i);
		if (j != k) {
			class_631.class_3681 lv = new class_631.class_3681(k);

			for (int l = this.field_16247 - j; l <= this.field_16247 + j; l++) {
				for (int m = this.field_16249 - j; m <= this.field_16249 + j; m++) {
					class_2818 lv2 = (class_2818)this.field_16246.field_16251.get(this.field_16246.method_16027(m, l));
					if (lv2 != null) {
						class_1923 lv3 = lv2.method_12004();
						if (lv.method_16034(lv3.field_9181, lv3.field_9180)) {
							lv.method_16031(lv.method_16027(lv3.field_9181, lv3.field_9180), lv2);
						}
					}
				}
			}

			this.field_16246 = lv;
		}
	}

	private static int method_20230(int i) {
		return Math.max(2, i) + 3;
	}

	@Override
	public String method_12122() {
		return "MultiplayerChunkCache: " + this.field_16246.field_16251.length() + ", " + this.method_20182();
	}

	@Override
	public class_2794<?> method_12129() {
		return null;
	}

	public int method_20182() {
		return this.field_16246.field_19143;
	}

	@Override
	public void method_12247(class_1944 arg, class_4076 arg2) {
		class_310.method_1551().field_1769.method_8571(arg2.method_18674(), arg2.method_18683(), arg2.method_18687());
	}

	@Environment(EnvType.CLIENT)
	final class class_3681 {
		private final AtomicReferenceArray<class_2818> field_16251;
		private final int field_16253;
		private final int field_16252;
		private int field_19143;

		private class_3681(int i) {
			this.field_16253 = i;
			this.field_16252 = i * 2 + 1;
			this.field_16251 = new AtomicReferenceArray(this.field_16252 * this.field_16252);
		}

		private int method_16027(int i, int j) {
			return Math.floorMod(j, this.field_16252) * this.field_16252 + Math.floorMod(i, this.field_16252);
		}

		protected void method_16031(int i, @Nullable class_2818 arg) {
			class_2818 lv = (class_2818)this.field_16251.getAndSet(i, arg);
			if (lv != null) {
				this.field_19143--;
				class_631.this.field_16525.method_18110(lv);
			}

			if (arg != null) {
				this.field_19143++;
			}
		}

		protected class_2818 method_20183(int i, class_2818 arg, @Nullable class_2818 arg2) {
			if (this.field_16251.compareAndSet(i, arg, arg2) && arg2 == null) {
				this.field_19143--;
			}

			class_631.this.field_16525.method_18110(arg);
			return arg;
		}

		private boolean method_16034(int i, int j) {
			return Math.abs(i - class_631.this.field_16249) <= this.field_16253 && Math.abs(j - class_631.this.field_16247) <= this.field_16253;
		}

		@Nullable
		protected class_2818 method_16033(int i) {
			return (class_2818)this.field_16251.get(i);
		}
	}
}
