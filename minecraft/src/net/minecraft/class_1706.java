package net.minecraft;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1706 extends class_1703 {
	private static final Logger field_7771 = LogManager.getLogger();
	private final class_1263 field_7769 = new class_1731();
	private final class_1263 field_7773 = new class_1277(2) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_1706.this.method_7609(this);
		}
	};
	private final class_3915 field_7770 = class_3915.method_17403();
	private final class_3914 field_7777;
	private int field_7776;
	private String field_7774;
	private final class_1657 field_7775;

	public class_1706(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_1706(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17329, i);
		this.field_7777 = arg2;
		this.field_7775 = arg.field_7546;
		this.method_17362(this.field_7770);
		this.method_7621(new class_1735(this.field_7773, 0, 27, 47));
		this.method_7621(new class_1735(this.field_7773, 1, 76, 47));
		this.method_7621(
			new class_1735(this.field_7769, 2, 134, 47) {
				@Override
				public boolean method_7680(class_1799 arg) {
					return false;
				}

				@Override
				public boolean method_7674(class_1657 arg) {
					return (arg.field_7503.field_7477 || arg.field_7520 >= class_1706.this.field_7770.method_17407())
						&& class_1706.this.field_7770.method_17407() > 0
						&& this.method_7681();
				}

				@Override
				public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
					if (!arg.field_7503.field_7477) {
						arg.method_7316(-class_1706.this.field_7770.method_17407());
					}

					class_1706.this.field_7773.method_5447(0, class_1799.field_8037);
					if (class_1706.this.field_7776 > 0) {
						class_1799 lv = class_1706.this.field_7773.method_5438(1);
						if (!lv.method_7960() && lv.method_7947() > class_1706.this.field_7776) {
							lv.method_7934(class_1706.this.field_7776);
							class_1706.this.field_7773.method_5447(1, lv);
						} else {
							class_1706.this.field_7773.method_5447(1, class_1799.field_8037);
						}
					} else {
						class_1706.this.field_7773.method_5447(1, class_1799.field_8037);
					}

					class_1706.this.field_7770.method_17404(0);
					arg2.method_17393((arg2xxx, arg3) -> {
						class_2680 lvx = arg2xxx.method_8320(arg3);
						if (!arg.field_7503.field_7477 && lvx.method_11602(class_3481.field_15486) && arg.method_6051().nextFloat() < 0.12F) {
							class_2680 lv2 = class_2199.method_9346(lvx);
							if (lv2 == null) {
								arg2xxx.method_8650(arg3, false);
								arg2xxx.method_20290(1029, arg3, 0);
							} else {
								arg2xxx.method_8652(arg3, lv2, 2);
								arg2xxx.method_20290(1030, arg3, 0);
							}
						} else {
							arg2xxx.method_20290(1030, arg3, 0);
						}
					});
					return arg2;
				}
			}
		);

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
	public void method_7609(class_1263 arg) {
		super.method_7609(arg);
		if (arg == this.field_7773) {
			this.method_7628();
		}
	}

	public void method_7628() {
		class_1799 lv = this.field_7773.method_5438(0);
		this.field_7770.method_17404(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (lv.method_7960()) {
			this.field_7769.method_5447(0, class_1799.field_8037);
			this.field_7770.method_17404(0);
		} else {
			class_1799 lv2 = lv.method_7972();
			class_1799 lv3 = this.field_7773.method_5438(1);
			Map<class_1887, Integer> map = class_1890.method_8222(lv2);
			j += lv.method_7928() + (lv3.method_7960() ? 0 : lv3.method_7928());
			this.field_7776 = 0;
			if (!lv3.method_7960()) {
				boolean bl = lv3.method_7909() == class_1802.field_8598 && !class_1772.method_7806(lv3).isEmpty();
				if (lv2.method_7963() && lv2.method_7909().method_7878(lv, lv3)) {
					int l = Math.min(lv2.method_7919(), lv2.method_7936() / 4);
					if (l <= 0) {
						this.field_7769.method_5447(0, class_1799.field_8037);
						this.field_7770.method_17404(0);
						return;
					}

					int m;
					for (m = 0; l > 0 && m < lv3.method_7947(); m++) {
						int n = lv2.method_7919() - l;
						lv2.method_7974(n);
						i++;
						l = Math.min(lv2.method_7919(), lv2.method_7936() / 4);
					}

					this.field_7776 = m;
				} else {
					if (!bl && (lv2.method_7909() != lv3.method_7909() || !lv2.method_7963())) {
						this.field_7769.method_5447(0, class_1799.field_8037);
						this.field_7770.method_17404(0);
						return;
					}

					if (lv2.method_7963() && !bl) {
						int lx = lv.method_7936() - lv.method_7919();
						int m = lv3.method_7936() - lv3.method_7919();
						int n = m + lv2.method_7936() * 12 / 100;
						int o = lx + n;
						int p = lv2.method_7936() - o;
						if (p < 0) {
							p = 0;
						}

						if (p < lv2.method_7919()) {
							lv2.method_7974(p);
							i += 2;
						}
					}

					Map<class_1887, Integer> map2 = class_1890.method_8222(lv3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (class_1887 lv4 : map2.keySet()) {
						if (lv4 != null) {
							int q = map.containsKey(lv4) ? (Integer)map.get(lv4) : 0;
							int r = (Integer)map2.get(lv4);
							r = q == r ? r + 1 : Math.max(r, q);
							boolean bl4 = lv4.method_8192(lv);
							if (this.field_7775.field_7503.field_7477 || lv.method_7909() == class_1802.field_8598) {
								bl4 = true;
							}

							for (class_1887 lv5 : map.keySet()) {
								if (lv5 != lv4 && !lv4.method_8188(lv5)) {
									bl4 = false;
									i++;
								}
							}

							if (!bl4) {
								bl3 = true;
							} else {
								bl2 = true;
								if (r > lv4.method_8183()) {
									r = lv4.method_8183();
								}

								map.put(lv4, r);
								int s = 0;
								switch (lv4.method_8186()) {
									case field_9087:
										s = 1;
										break;
									case field_9090:
										s = 2;
										break;
									case field_9088:
										s = 4;
										break;
									case field_9091:
										s = 8;
								}

								if (bl) {
									s = Math.max(1, s / 2);
								}

								i += s * r;
								if (lv.method_7947() > 1) {
									i = 40;
								}
							}
						}
					}

					if (bl3 && !bl2) {
						this.field_7769.method_5447(0, class_1799.field_8037);
						this.field_7770.method_17404(0);
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.field_7774)) {
				if (lv.method_7938()) {
					k = 1;
					i += k;
					lv2.method_7925();
				}
			} else if (!this.field_7774.equals(lv.method_7964().getString())) {
				k = 1;
				i += k;
				lv2.method_7977(new class_2585(this.field_7774));
			}

			this.field_7770.method_17404(j + i);
			if (i <= 0) {
				lv2 = class_1799.field_8037;
			}

			if (k == i && k > 0 && this.field_7770.method_17407() >= 40) {
				this.field_7770.method_17404(39);
			}

			if (this.field_7770.method_17407() >= 40 && !this.field_7775.field_7503.field_7477) {
				lv2 = class_1799.field_8037;
			}

			if (!lv2.method_7960()) {
				int t = lv2.method_7928();
				if (!lv3.method_7960() && t < lv3.method_7928()) {
					t = lv3.method_7928();
				}

				if (k != i || k == 0) {
					t = method_20398(t);
				}

				lv2.method_7927(t);
				class_1890.method_8214(map, lv2);
			}

			this.field_7769.method_5447(0, lv2);
			this.method_7623();
		}
	}

	public static int method_20398(int i) {
		return i * 2 + 1;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7777.method_17393((arg2, arg3) -> this.method_7607(arg, arg2, this.field_7773));
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7777
			.method_17396(
				(arg2, arg3) -> !arg2.method_8320(arg3).method_11602(class_3481.field_15486)
						? false
						: arg.method_5649((double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 0.5, (double)arg3.method_10260() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 2) {
				if (!this.method_7616(lv3, 3, 39, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 39 && !this.method_7616(lv3, 0, 2, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 3, 39, false)) {
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

	public void method_7625(String string) {
		this.field_7774 = string;
		if (this.method_7611(2).method_7681()) {
			class_1799 lv = this.method_7611(2).method_7677();
			if (StringUtils.isBlank(string)) {
				lv.method_7925();
			} else {
				lv.method_7977(new class_2585(this.field_7774));
			}
		}

		this.method_7628();
	}

	@Environment(EnvType.CLIENT)
	public int method_17369() {
		return this.field_7770.method_17407();
	}
}
