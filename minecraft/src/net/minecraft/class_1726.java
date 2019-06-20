package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1726 extends class_1703 {
	private final class_3914 field_17316;
	private final class_3915 field_17317 = class_3915.method_17403();
	private Runnable field_17318 = () -> {
	};
	private final class_1735 field_17319;
	private final class_1735 field_17320;
	private final class_1735 field_17321;
	private final class_1735 field_17322;
	private final class_1263 field_7850 = new class_1277(3) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_1726.this.method_7609(this);
			class_1726.this.field_17318.run();
		}
	};
	private final class_1263 field_17323 = new class_1277(1) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_1726.this.field_17318.run();
		}
	};

	public class_1726(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_1726(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17339, i);
		this.field_17316 = arg2;
		this.field_17319 = this.method_7621(new class_1735(this.field_7850, 0, 13, 26) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() instanceof class_1746;
			}
		});
		this.field_17320 = this.method_7621(new class_1735(this.field_7850, 1, 33, 26) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() instanceof class_1769;
			}
		});
		this.field_17321 = this.method_7621(new class_1735(this.field_7850, 2, 23, 45) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() instanceof class_1745;
			}
		});
		this.field_17322 = this.method_7621(new class_1735(this.field_17323, 0, 143, 58) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return false;
			}

			@Override
			public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
				class_1726.this.field_17319.method_7671(1);
				class_1726.this.field_17320.method_7671(1);
				if (!class_1726.this.field_17319.method_7681() || !class_1726.this.field_17320.method_7681()) {
					class_1726.this.field_17317.method_17404(0);
				}

				arg2.method_17393((argx, arg2xxx) -> argx.method_8396(null, arg2xxx, class_3417.field_15096, class_3419.field_15245, 1.0F, 1.0F));
				return super.method_7667(arg, arg2);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}

		this.method_17362(this.field_17317);
	}

	@Environment(EnvType.CLIENT)
	public int method_7647() {
		return this.field_17317.method_17407();
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return method_17695(this.field_17316, arg, class_2246.field_10083);
	}

	@Override
	public boolean method_7604(class_1657 arg, int i) {
		if (i > 0 && i <= class_2582.field_18283) {
			this.field_17317.method_17404(i);
			this.method_7648();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_7609(class_1263 arg) {
		class_1799 lv = this.field_17319.method_7677();
		class_1799 lv2 = this.field_17320.method_7677();
		class_1799 lv3 = this.field_17321.method_7677();
		class_1799 lv4 = this.field_17322.method_7677();
		if (lv4.method_7960()
			|| !lv.method_7960()
				&& !lv2.method_7960()
				&& this.field_17317.method_17407() > 0
				&& (this.field_17317.method_17407() < class_2582.field_11846 - 5 || !lv3.method_7960())) {
			if (!lv3.method_7960() && lv3.method_7909() instanceof class_1745) {
				class_2487 lv5 = lv.method_7911("BlockEntityTag");
				boolean bl = lv5.method_10573("Patterns", 9) && !lv.method_7960() && lv5.method_10554("Patterns", 10).size() >= 6;
				if (bl) {
					this.field_17317.method_17404(0);
				} else {
					this.field_17317.method_17404(((class_1745)lv3.method_7909()).method_7704().ordinal());
				}
			}
		} else {
			this.field_17322.method_7673(class_1799.field_8037);
			this.field_17317.method_17404(0);
		}

		this.method_7648();
		this.method_7623();
	}

	@Environment(EnvType.CLIENT)
	public void method_17423(Runnable runnable) {
		this.field_17318 = runnable;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == this.field_17322.field_7874) {
				if (!this.method_7616(lv3, 4, 40, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i != this.field_17320.field_7874 && i != this.field_17319.field_7874 && i != this.field_17321.field_7874) {
				if (lv3.method_7909() instanceof class_1746) {
					if (!this.method_7616(lv3, this.field_17319.field_7874, this.field_17319.field_7874 + 1, false)) {
						return class_1799.field_8037;
					}
				} else if (lv3.method_7909() instanceof class_1769) {
					if (!this.method_7616(lv3, this.field_17320.field_7874, this.field_17320.field_7874 + 1, false)) {
						return class_1799.field_8037;
					}
				} else if (lv3.method_7909() instanceof class_1745) {
					if (!this.method_7616(lv3, this.field_17321.field_7874, this.field_17321.field_7874 + 1, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 4 && i < 31) {
					if (!this.method_7616(lv3, 31, 40, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 31 && i < 40 && !this.method_7616(lv3, 4, 31, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 4, 40, false)) {
				return class_1799.field_8037;
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			} else {
				lv2.method_7668();
			}

			if (lv3.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			lv2.method_7667(arg, lv3);
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_17316.method_17393((arg2, arg3) -> this.method_7607(arg, arg.field_6002, this.field_7850));
	}

	private void method_7648() {
		if (this.field_17317.method_17407() > 0) {
			class_1799 lv = this.field_17319.method_7677();
			class_1799 lv2 = this.field_17320.method_7677();
			class_1799 lv3 = class_1799.field_8037;
			if (!lv.method_7960() && !lv2.method_7960()) {
				lv3 = lv.method_7972();
				lv3.method_7939(1);
				class_2582 lv4 = class_2582.values()[this.field_17317.method_17407()];
				class_1767 lv5 = ((class_1769)lv2.method_7909()).method_7802();
				class_2487 lv6 = lv3.method_7911("BlockEntityTag");
				class_2499 lv7;
				if (lv6.method_10573("Patterns", 9)) {
					lv7 = lv6.method_10554("Patterns", 10);
				} else {
					lv7 = new class_2499();
					lv6.method_10566("Patterns", lv7);
				}

				class_2487 lv8 = new class_2487();
				lv8.method_10582("Pattern", lv4.method_10945());
				lv8.method_10569("Color", lv5.method_7789());
				lv7.add(lv8);
			}

			if (!class_1799.method_7973(lv3, this.field_17322.method_7677())) {
				this.field_17322.method_7673(lv3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public class_1735 method_17428() {
		return this.field_17319;
	}

	@Environment(EnvType.CLIENT)
	public class_1735 method_17429() {
		return this.field_17320;
	}

	@Environment(EnvType.CLIENT)
	public class_1735 method_17430() {
		return this.field_17321;
	}

	@Environment(EnvType.CLIENT)
	public class_1735 method_17431() {
		return this.field_17322;
	}
}
