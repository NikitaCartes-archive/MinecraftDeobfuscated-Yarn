package net.minecraft;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_1560 extends class_1588 {
	private static final UUID field_7256 = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final class_1322 field_7252 = new class_1322(field_7256, "Attacking speed boost", 0.15F, class_1322.class_1323.field_6328).method_6187(false);
	private static final class_2940<Optional<class_2680>> field_7257 = class_2945.method_12791(class_1560.class, class_2943.field_13312);
	private static final class_2940<Boolean> field_7255 = class_2945.method_12791(class_1560.class, class_2943.field_13323);
	private int field_7253;
	private int field_7254;

	public class_1560(class_1937 arg) {
		super(class_1299.field_6091, arg);
		this.method_5835(0.6F, 2.9F);
		this.field_6013 = 1.0F;
		this.method_5941(class_7.field_18, -1.0F);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(2, new class_1366(this, 1.0, false));
		this.field_6201.method_6277(7, new class_1394(this, 1.0, 0.0F));
		this.field_6201.method_6277(8, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6201.method_6277(10, new class_1560.class_1561(this));
		this.field_6201.method_6277(11, new class_1560.class_1563(this));
		this.field_6185.method_6277(1, new class_1560.class_1562(this));
		this.field_6185.method_6277(2, new class_1399(this));
		this.field_6185.method_6277(3, new class_1400(this, class_1559.class, 10, true, false, class_1559::method_7023));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(40.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
		this.method_5996(class_1612.field_7363).method_6192(7.0);
		this.method_5996(class_1612.field_7365).method_6192(64.0);
	}

	@Override
	public void method_5980(@Nullable class_1309 arg) {
		super.method_5980(arg);
		class_1324 lv = this.method_5996(class_1612.field_7357);
		if (arg == null) {
			this.field_7254 = 0;
			this.field_6011.method_12778(field_7255, false);
			lv.method_6202(field_7252);
		} else {
			this.field_7254 = this.field_6012;
			this.field_6011.method_12778(field_7255, true);
			if (!lv.method_6196(field_7252)) {
				lv.method_6197(field_7252);
			}
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7257, Optional.empty());
		this.field_6011.method_12784(field_7255, false);
	}

	public void method_7030() {
		if (this.field_6012 >= this.field_7253 + 400) {
			this.field_7253 = this.field_6012;
			if (!this.method_5701()) {
				this.field_6002
					.method_8486(this.field_5987, this.field_6010 + (double)this.method_5751(), this.field_6035, class_3417.field_14967, this.method_5634(), 2.5F, 1.0F, false);
			}
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7255.equals(arg) && this.method_7028() && this.field_6002.field_9236) {
			this.method_7030();
		}

		super.method_5674(arg);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_2680 lv = this.method_7027();
		if (lv != null) {
			arg.method_10566("carriedBlockState", class_2512.method_10686(lv));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_2680 lv = null;
		if (arg.method_10573("carriedBlockState", 10)) {
			lv = class_2512.method_10681(arg.method_10562("carriedBlockState"));
			if (lv.method_11588()) {
				lv = null;
			}
		}

		this.method_7032(lv);
	}

	private boolean method_7026(class_1657 arg) {
		class_1799 lv = arg.field_7514.field_7548.get(3);
		if (lv.method_7909() == class_2246.field_10147.method_8389()) {
			return false;
		} else {
			class_243 lv2 = arg.method_5828(1.0F).method_1029();
			class_243 lv3 = new class_243(
				this.field_5987 - arg.field_5987,
				this.method_5829().field_1322 + (double)this.method_5751() - (arg.field_6010 + (double)arg.method_5751()),
				this.field_6035 - arg.field_6035
			);
			double d = lv3.method_1033();
			lv3 = lv3.method_1029();
			double e = lv2.method_1026(lv3);
			return e > 1.0 - 0.025 / d ? arg.method_6057(this) : false;
		}
	}

	@Override
	public float method_5751() {
		return 2.55F;
	}

	@Override
	public void method_6007() {
		if (this.field_6002.field_9236) {
			for (int i = 0; i < 2; i++) {
				this.field_6002
					.method_8406(
						class_2398.field_11214,
						this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						this.field_6010 + this.field_5974.nextDouble() * (double)this.field_6019 - 0.25,
						this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.field_5998,
						(this.field_5974.nextDouble() - 0.5) * 2.0,
						-this.field_5974.nextDouble(),
						(this.field_5974.nextDouble() - 0.5) * 2.0
					);
			}
		}

		this.field_6282 = false;
		super.method_6007();
	}

	@Override
	protected void method_5958() {
		if (this.method_5637()) {
			this.method_5643(class_1282.field_5859, 1.0F);
		}

		if (this.field_6002.method_8530() && this.field_6012 >= this.field_7254 + 600) {
			float f = this.method_5718();
			if (f > 0.5F && this.field_6002.method_8311(new class_2338(this)) && this.field_5974.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.method_5980(null);
				this.method_7029();
			}
		}

		super.method_5958();
	}

	protected boolean method_7029() {
		double d = this.field_5987 + (this.field_5974.nextDouble() - 0.5) * 64.0;
		double e = this.field_6010 + (double)(this.field_5974.nextInt(64) - 32);
		double f = this.field_6035 + (this.field_5974.nextDouble() - 0.5) * 64.0;
		return this.method_7024(d, e, f);
	}

	protected boolean method_7025(class_1297 arg) {
		class_243 lv = new class_243(
			this.field_5987 - arg.field_5987,
			this.method_5829().field_1322 + (double)(this.field_6019 / 2.0F) - arg.field_6010 + (double)arg.method_5751(),
			this.field_6035 - arg.field_6035
		);
		lv = lv.method_1029();
		double d = 16.0;
		double e = this.field_5987 + (this.field_5974.nextDouble() - 0.5) * 8.0 - lv.field_1352 * 16.0;
		double f = this.field_6010 + (double)(this.field_5974.nextInt(16) - 8) - lv.field_1351 * 16.0;
		double g = this.field_6035 + (this.field_5974.nextDouble() - 0.5) * 8.0 - lv.field_1350 * 16.0;
		return this.method_7024(e, f, g);
	}

	private boolean method_7024(double d, double e, double f) {
		boolean bl = this.method_6082(d, e, f, true);
		if (bl) {
			this.field_6002.method_8465(null, this.field_6014, this.field_6036, this.field_5969, class_3417.field_14879, this.method_5634(), 1.0F, 1.0F);
			this.method_5783(class_3417.field_14879, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_7028() ? class_3417.field_14713 : class_3417.field_14696;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14797;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14608;
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_2680 lv = this.method_7027();
		if (lv != null) {
			this.method_5706(lv.method_11614());
		}
	}

	public void method_7032(@Nullable class_2680 arg) {
		this.field_6011.method_12778(field_7257, Optional.ofNullable(arg));
	}

	@Nullable
	public class_2680 method_7027() {
		return (class_2680)this.field_6011.method_12789(field_7257).orElse(null);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (arg instanceof class_1284) {
			for (int i = 0; i < 64; i++) {
				if (this.method_7029()) {
					return true;
				}
			}

			return false;
		} else {
			boolean bl = super.method_5643(arg, f);
			if (arg.method_5537() && this.field_5974.nextInt(10) != 0) {
				this.method_7029();
			}

			return bl;
		}
	}

	public boolean method_7028() {
		return this.field_6011.method_12789(field_7255);
	}

	static class class_1561 extends class_1352 {
		private final class_1560 field_7258;

		public class_1561(class_1560 arg) {
			this.field_7258 = arg;
		}

		@Override
		public boolean method_6264() {
			if (this.field_7258.method_7027() == null) {
				return false;
			} else {
				return !this.field_7258.field_6002.method_8450().method_8355("mobGriefing") ? false : this.field_7258.method_6051().nextInt(2000) == 0;
			}
		}

		@Override
		public void method_6268() {
			Random random = this.field_7258.method_6051();
			class_1936 lv = this.field_7258.field_6002;
			int i = class_3532.method_15357(this.field_7258.field_5987 - 1.0 + random.nextDouble() * 2.0);
			int j = class_3532.method_15357(this.field_7258.field_6010 + random.nextDouble() * 2.0);
			int k = class_3532.method_15357(this.field_7258.field_6035 - 1.0 + random.nextDouble() * 2.0);
			class_2338 lv2 = new class_2338(i, j, k);
			class_2680 lv3 = lv.method_8320(lv2);
			class_2338 lv4 = lv2.method_10074();
			class_2680 lv5 = lv.method_8320(lv4);
			class_2680 lv6 = this.field_7258.method_7027();
			if (lv6 != null && this.method_7033(lv, lv2, lv6, lv3, lv5, lv4)) {
				lv.method_8652(lv2, lv6, 3);
				this.field_7258.method_7032(null);
			}
		}

		private boolean method_7033(class_1941 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4, class_2680 arg5, class_2338 arg6) {
			return arg4.method_11588() && !arg5.method_11588() && arg5.method_11604(arg, arg6) && arg3.method_11591(arg, arg2);
		}
	}

	static class class_1562 extends class_1400<class_1657> {
		private final class_1560 field_7260;
		private class_1657 field_7259;
		private int field_7262;
		private int field_7261;

		public class_1562(class_1560 arg) {
			super(arg, class_1657.class, false);
			this.field_7260 = arg;
		}

		@Override
		public boolean method_6264() {
			double d = this.method_6326();
			this.field_7259 = this.field_7260
				.field_6002
				.method_8439(
					this.field_7260.field_5987, this.field_7260.field_6010, this.field_7260.field_6035, d, d, null, arg -> arg != null && this.field_7260.method_7026(arg)
				);
			return this.field_7259 != null;
		}

		@Override
		public void method_6269() {
			this.field_7262 = 5;
			this.field_7261 = 0;
		}

		@Override
		public void method_6270() {
			this.field_7259 = null;
			super.method_6270();
		}

		@Override
		public boolean method_6266() {
			if (this.field_7259 != null) {
				if (!this.field_7260.method_7026(this.field_7259)) {
					return false;
				} else {
					this.field_7260.method_5951(this.field_7259, 10.0F, 10.0F);
					return true;
				}
			} else {
				return this.field_6644 != null && this.field_6644.method_5805() ? true : super.method_6266();
			}
		}

		@Override
		public void method_6268() {
			if (this.field_7259 != null) {
				if (--this.field_7262 <= 0) {
					this.field_6644 = this.field_7259;
					this.field_7259 = null;
					super.method_6269();
				}
			} else {
				if (this.field_6644 != null) {
					if (this.field_7260.method_7026(this.field_6644)) {
						if (this.field_6644.method_5858(this.field_7260) < 16.0) {
							this.field_7260.method_7029();
						}

						this.field_7261 = 0;
					} else if (this.field_6644.method_5858(this.field_7260) > 256.0 && this.field_7261++ >= 30 && this.field_7260.method_7025(this.field_6644)) {
						this.field_7261 = 0;
					}
				}

				super.method_6268();
			}
		}
	}

	static class class_1563 extends class_1352 {
		private final class_1560 field_7263;

		public class_1563(class_1560 arg) {
			this.field_7263 = arg;
		}

		@Override
		public boolean method_6264() {
			if (this.field_7263.method_7027() != null) {
				return false;
			} else {
				return !this.field_7263.field_6002.method_8450().method_8355("mobGriefing") ? false : this.field_7263.method_6051().nextInt(20) == 0;
			}
		}

		@Override
		public void method_6268() {
			Random random = this.field_7263.method_6051();
			class_1937 lv = this.field_7263.field_6002;
			int i = class_3532.method_15357(this.field_7263.field_5987 - 2.0 + random.nextDouble() * 4.0);
			int j = class_3532.method_15357(this.field_7263.field_6010 + random.nextDouble() * 3.0);
			int k = class_3532.method_15357(this.field_7263.field_6035 - 2.0 + random.nextDouble() * 4.0);
			class_2338 lv2 = new class_2338(i, j, k);
			class_2680 lv3 = lv.method_8320(lv2);
			class_2248 lv4 = lv3.method_11614();
			class_239 lv5 = lv.method_8531(
				new class_243(
					(double)((float)class_3532.method_15357(this.field_7263.field_5987) + 0.5F),
					(double)((float)j + 0.5F),
					(double)((float)class_3532.method_15357(this.field_7263.field_6035) + 0.5F)
				),
				new class_243((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F)),
				class_242.field_1348,
				true,
				false
			);
			boolean bl = lv5 != null && lv5.method_1015().equals(lv2);
			if (lv4.method_9525(class_3481.field_15460) && bl) {
				this.field_7263.method_7032(lv3);
				lv.method_8650(lv2);
			}
		}
	}
}
