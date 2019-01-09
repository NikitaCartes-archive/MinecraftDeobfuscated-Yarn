package net.minecraft;

public class class_3910 extends class_1703 {
	private final class_3914 field_17294;
	private boolean field_17295;
	private class_1799 field_17296 = class_1799.field_8037;
	private class_1799 field_17297 = class_1799.field_8037;
	public final class_1263 field_17293 = new class_1277(3) {
		@Override
		public void method_5431() {
			class_3910.this.method_7609(this);
			super.method_5431();
		}
	};

	public class_3910(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_3910(int i, class_1661 arg, class_3914 arg2) {
		super(i);
		this.field_17294 = arg2;
		this.method_7621(new class_1735(this.field_17293, 0, 15, 15) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() == class_1802.field_8204;
			}
		});
		this.method_7621(new class_1735(this.field_17293, 1, 15, 52) {
			@Override
			public boolean method_7680(class_1799 arg) {
				class_1792 lv = arg.method_7909();
				return lv == class_1802.field_8407 || lv == class_1802.field_8895 || lv == class_1802.field_8141;
			}
		});
		this.method_7621(new class_1735(this.field_17293, 2, 145, 39) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return false;
			}

			@Override
			public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
				class_1799 lv = (class_1799)arg2.method_17395((arg2xxx, arg3) -> {
					if (!class_3910.this.field_17295 && class_3910.this.field_17293.method_5438(1).method_7909() == class_1802.field_8141) {
						class_1799 lvx = class_1806.method_17442(arg2xxx, class_3910.this.field_17296);
						if (lvx != null) {
							return lvx;
						}
					}

					return arg2;
				}).orElse(arg2);
				this.field_7871.method_5434(0, 1);
				this.field_7871.method_5434(1, 1);
				arg.method_7396(lv);
				lv.method_7909().method_7843(lv, arg.field_6002, arg);
				arg2.method_17393((argxx, arg2xxx) -> argxx.method_8396(null, arg2xxx, class_3417.field_17484, class_3419.field_15245, 1.0F, 1.0F));
				return super.method_7667(arg, lv);
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
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_17294
			.method_17396(
				(arg2, arg3) -> arg2.method_8320(arg3).method_11614() != class_2246.field_16336
						? false
						: arg.method_5649((double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 0.5, (double)arg3.method_10260() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public void method_7609(class_1263 arg) {
		class_1799 lv = this.field_17293.method_5438(0);
		class_1799 lv2 = this.field_17293.method_5438(1);
		boolean bl = !class_1799.method_7973(lv, this.field_17296);
		boolean bl2 = !class_1799.method_7973(lv2, this.field_17297);
		this.field_17296 = lv.method_7972();
		this.field_17297 = lv2.method_7972();
		if (bl || bl2) {
			class_1799 lv3 = this.field_17293.method_5438(2);
			if (lv3.method_7960() || !lv.method_7960() && !lv2.method_7960()) {
				if (!lv.method_7960() && !lv2.method_7960()) {
					this.method_17381(lv, lv2, lv3);
				}
			} else {
				this.field_17293.method_5441(2);
			}
		}
	}

	private void method_17381(class_1799 arg, class_1799 arg2, class_1799 arg3) {
		this.field_17294.method_17393((arg4, arg5) -> {
			class_1792 lv = arg2.method_7909();
			class_22 lv2 = class_1806.method_17441(arg, arg4);
			if (lv2 != null) {
				class_1799 lv3;
				if (lv == class_1802.field_8407 && !lv2.field_17403 && lv2.field_119 < 4) {
					lv3 = arg.method_7972();
					lv3.method_7939(1);
					lv3.method_7948().method_10569("map_scale_direction", 1);
				} else if (lv == class_1802.field_8141 && !lv2.field_17403) {
					lv3 = arg.method_7972();
					lv3.method_7939(1);
				} else {
					if (lv != class_1802.field_8895) {
						this.field_17293.method_5441(2);
						this.method_7623();
						return;
					}

					lv3 = arg.method_7972();
					lv3.method_7939(2);
				}

				if (!class_1799.method_7973(lv3, arg3)) {
					this.field_17293.method_5447(2, lv3);
					this.method_7623();
				}
			}
		});
	}

	@Override
	public class_3917<?> method_17358() {
		return class_3917.field_17343;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			class_1799 lv4 = lv3;
			class_1792 lv5 = lv3.method_7909();
			lv = lv3.method_7972();
			if (i == 2) {
				if (this.field_17293.method_5438(1).method_7909() == class_1802.field_8141) {
					lv4 = (class_1799)this.field_17294.method_17395((arg2, arg3) -> {
						class_1799 lvx = class_1806.method_17442(arg2, this.field_17296);
						return lvx != null ? lvx : lv3;
					}).orElse(lv3);
				}

				lv5.method_7843(lv4, arg.field_6002, arg);
				if (!this.method_7616(lv4, 3, 39, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv4, lv);
			} else if (i != 1 && i != 0) {
				if (lv5 == class_1802.field_8204) {
					if (!this.method_7616(lv3, 0, 1, false)) {
						return class_1799.field_8037;
					}
				} else if (lv5 != class_1802.field_8407 && lv5 != class_1802.field_8895 && lv5 != class_1802.field_8141) {
					if (i >= 3 && i < 30) {
						if (!this.method_7616(lv3, 30, 39, false)) {
							return class_1799.field_8037;
						}
					} else if (i >= 30 && i < 39 && !this.method_7616(lv3, 3, 30, false)) {
						return class_1799.field_8037;
					}
				} else if (!this.method_7616(lv3, 1, 2, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 3, 39, false)) {
				return class_1799.field_8037;
			}

			if (lv4.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			}

			lv2.method_7668();
			if (lv4.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			this.field_17295 = true;
			lv2.method_7667(arg, lv4);
			this.field_17295 = false;
			this.method_7623();
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_17293.method_5441(2);
		this.field_17294.method_17393((arg2, arg3) -> this.method_7607(arg, arg.field_6002, this.field_17293));
	}
}
