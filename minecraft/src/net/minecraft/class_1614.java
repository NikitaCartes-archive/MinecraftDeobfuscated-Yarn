package net.minecraft;

import java.util.EnumSet;
import java.util.Random;

public class class_1614 extends class_1588 {
	private static final class_4051 field_18131 = new class_4051().method_18418(5.0).method_18424();
	private class_1614.class_1616 field_7366;

	public class_1614(class_1299<? extends class_1614> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		this.field_7366 = new class_1614.class_1616(this);
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(3, this.field_7366);
		this.field_6201.method_6277(4, new class_1366(this, 1.0, false));
		this.field_6201.method_6277(5, new class_1614.class_1615(this));
		this.field_6185.method_6277(1, new class_1399(this).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
	}

	@Override
	public double method_5678() {
		return 0.1;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.1F;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(8.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
		this.method_5996(class_1612.field_7363).method_6192(1.0);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14786;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14593;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14673;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_15084, 0.15F, 1.0F);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			if ((arg instanceof class_1285 || arg == class_1282.field_5846) && this.field_7366 != null) {
				this.field_7366.method_7136();
			}

			return super.method_5643(arg, f);
		}
	}

	@Override
	public void method_5773() {
		this.field_6283 = this.field_6031;
		super.method_5773();
	}

	@Override
	public void method_5636(float f) {
		this.field_6031 = f;
		super.method_5636(f);
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return class_2384.method_10269(arg2.method_8320(arg.method_10074())) ? 10.0F : super.method_6144(arg, arg2);
	}

	public static boolean method_20684(class_1299<class_1614> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		if (method_20681(arg, arg2, arg3, arg4, random)) {
			class_1657 lv = arg2.method_18461(field_18131, (double)arg4.method_10263() + 0.5, (double)arg4.method_10264() + 0.5, (double)arg4.method_10260() + 0.5);
			return lv == null;
		} else {
			return false;
		}
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6293;
	}

	static class class_1615 extends class_1379 {
		private class_2350 field_7368;
		private boolean field_7367;

		public class_1615(class_1614 arg) {
			super(arg, 1.0, 10);
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			if (this.field_6566.method_5968() != null) {
				return false;
			} else if (!this.field_6566.method_5942().method_6357()) {
				return false;
			} else {
				Random random = this.field_6566.method_6051();
				if (this.field_6566.field_6002.method_8450().method_8355(class_1928.field_19388) && random.nextInt(10) == 0) {
					this.field_7368 = class_2350.method_10162(random);
					class_2338 lv = new class_2338(this.field_6566.field_5987, this.field_6566.field_6010 + 0.5, this.field_6566.field_6035).method_10093(this.field_7368);
					class_2680 lv2 = this.field_6566.field_6002.method_8320(lv);
					if (class_2384.method_10269(lv2)) {
						this.field_7367 = true;
						return true;
					}
				}

				this.field_7367 = false;
				return super.method_6264();
			}
		}

		@Override
		public boolean method_6266() {
			return this.field_7367 ? false : super.method_6266();
		}

		@Override
		public void method_6269() {
			if (!this.field_7367) {
				super.method_6269();
			} else {
				class_1936 lv = this.field_6566.field_6002;
				class_2338 lv2 = new class_2338(this.field_6566.field_5987, this.field_6566.field_6010 + 0.5, this.field_6566.field_6035).method_10093(this.field_7368);
				class_2680 lv3 = lv.method_8320(lv2);
				if (class_2384.method_10269(lv3)) {
					lv.method_8652(lv2, class_2384.method_10270(lv3.method_11614()), 3);
					this.field_6566.method_5990();
					this.field_6566.method_5650();
				}
			}
		}
	}

	static class class_1616 extends class_1352 {
		private final class_1614 field_7370;
		private int field_7369;

		public class_1616(class_1614 arg) {
			this.field_7370 = arg;
		}

		public void method_7136() {
			if (this.field_7369 == 0) {
				this.field_7369 = 20;
			}
		}

		@Override
		public boolean method_6264() {
			return this.field_7369 > 0;
		}

		@Override
		public void method_6268() {
			this.field_7369--;
			if (this.field_7369 <= 0) {
				class_1937 lv = this.field_7370.field_6002;
				Random random = this.field_7370.method_6051();
				class_2338 lv2 = new class_2338(this.field_7370);

				for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
					for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
						for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
							class_2338 lv3 = lv2.method_10069(j, i, k);
							class_2680 lv4 = lv.method_8320(lv3);
							class_2248 lv5 = lv4.method_11614();
							if (lv5 instanceof class_2384) {
								if (lv.method_8450().method_8355(class_1928.field_19388)) {
									lv.method_8651(lv3, true);
								} else {
									lv.method_8652(lv3, ((class_2384)lv5).method_10271().method_9564(), 3);
								}

								if (random.nextBoolean()) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}
