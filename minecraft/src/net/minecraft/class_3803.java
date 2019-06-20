package net.minecraft;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class class_3803 extends class_1703 {
	private final class_1263 field_16773 = new class_1731();
	private final class_1263 field_16772 = new class_1277(2) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_3803.this.method_7609(this);
		}
	};
	private final class_3914 field_16775;

	public class_3803(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_3803(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17336, i);
		this.field_16775 = arg2;
		this.method_7621(new class_1735(this.field_16772, 0, 49, 19) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7963() || arg.method_7909() == class_1802.field_8598 || arg.method_7942();
			}
		});
		this.method_7621(new class_1735(this.field_16772, 1, 49, 40) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7963() || arg.method_7909() == class_1802.field_8598 || arg.method_7942();
			}
		});
		this.method_7621(new class_1735(this.field_16773, 2, 129, 34) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return false;
			}

			@Override
			public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
				arg2.method_17393((argx, arg2xxx) -> {
					int i = this.method_17416(argx);

					while (i > 0) {
						int j = class_1303.method_5918(i);
						i -= j;
						argx.method_8649(new class_1303(argx, (double)arg2xxx.method_10263(), (double)arg2xxx.method_10264() + 0.5, (double)arg2xxx.method_10260() + 0.5, j));
					}

					argx.method_20290(1042, arg2xxx, 0);
				});
				class_3803.this.field_16772.method_5447(0, class_1799.field_8037);
				class_3803.this.field_16772.method_5447(1, class_1799.field_8037);
				return arg2;
			}

			private int method_17416(class_1937 arg) {
				int i = 0;
				i += this.method_16696(class_3803.this.field_16772.method_5438(0));
				i += this.method_16696(class_3803.this.field_16772.method_5438(1));
				if (i > 0) {
					int j = (int)Math.ceil((double)i / 2.0);
					return j + arg.field_9229.nextInt(j);
				} else {
					return 0;
				}
			}

			private int method_16696(class_1799 arg) {
				int i = 0;
				Map<class_1887, Integer> map = class_1890.method_8222(arg);

				for (Entry<class_1887, Integer> entry : map.entrySet()) {
					class_1887 lv = (class_1887)entry.getKey();
					Integer integer = (Integer)entry.getValue();
					if (!lv.method_8195()) {
						i += lv.method_8182(integer);
					}
				}

				return i;
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
	public void method_7609(class_1263 arg) {
		super.method_7609(arg);
		if (arg == this.field_16772) {
			this.method_16695();
		}
	}

	private void method_16695() {
		class_1799 lv = this.field_16772.method_5438(0);
		class_1799 lv2 = this.field_16772.method_5438(1);
		boolean bl = !lv.method_7960() || !lv2.method_7960();
		boolean bl2 = !lv.method_7960() && !lv2.method_7960();
		if (!bl) {
			this.field_16773.method_5447(0, class_1799.field_8037);
		} else {
			boolean bl3 = !lv.method_7960() && lv.method_7909() != class_1802.field_8598 && !lv.method_7942()
				|| !lv2.method_7960() && lv2.method_7909() != class_1802.field_8598 && !lv2.method_7942();
			if (lv.method_7947() > 1 || lv2.method_7947() > 1 || !bl2 && bl3) {
				this.field_16773.method_5447(0, class_1799.field_8037);
				this.method_7623();
				return;
			}

			int i = 1;
			int m;
			class_1799 lv4;
			if (bl2) {
				if (lv.method_7909() != lv2.method_7909()) {
					this.field_16773.method_5447(0, class_1799.field_8037);
					this.method_7623();
					return;
				}

				class_1792 lv3 = lv.method_7909();
				int j = lv3.method_7841() - lv.method_7919();
				int k = lv3.method_7841() - lv2.method_7919();
				int l = j + k + lv3.method_7841() * 5 / 100;
				m = Math.max(lv3.method_7841() - l, 0);
				lv4 = this.method_20268(lv, lv2);
				if (!lv4.method_7963()) {
					if (!class_1799.method_7973(lv, lv2)) {
						this.field_16773.method_5447(0, class_1799.field_8037);
						this.method_7623();
						return;
					}

					i = 2;
				}
			} else {
				boolean bl4 = !lv.method_7960();
				m = bl4 ? lv.method_7919() : lv2.method_7919();
				lv4 = bl4 ? lv : lv2;
			}

			this.field_16773.method_5447(0, this.method_16693(lv4, m, i));
		}

		this.method_7623();
	}

	private class_1799 method_20268(class_1799 arg, class_1799 arg2) {
		class_1799 lv = arg.method_7972();
		Map<class_1887, Integer> map = class_1890.method_8222(arg2);

		for (Entry<class_1887, Integer> entry : map.entrySet()) {
			class_1887 lv2 = (class_1887)entry.getKey();
			if (!lv2.method_8195() || class_1890.method_8225(lv2, lv) == 0) {
				lv.method_7978(lv2, (Integer)entry.getValue());
			}
		}

		return lv;
	}

	private class_1799 method_16693(class_1799 arg, int i, int j) {
		class_1799 lv = arg.method_7972();
		lv.method_7983("Enchantments");
		lv.method_7983("StoredEnchantments");
		if (i > 0) {
			lv.method_7974(i);
		} else {
			lv.method_7983("Damage");
		}

		lv.method_7939(j);
		Map<class_1887, Integer> map = (Map<class_1887, Integer>)class_1890.method_8222(arg)
			.entrySet()
			.stream()
			.filter(entry -> ((class_1887)entry.getKey()).method_8195())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		class_1890.method_8214(map, lv);
		lv.method_7927(0);
		if (lv.method_7909() == class_1802.field_8598 && map.size() == 0) {
			lv = new class_1799(class_1802.field_8529);
			if (arg.method_7938()) {
				lv.method_7977(arg.method_7964());
			}
		}

		for (int k = 0; k < map.size(); k++) {
			lv.method_7927(class_1706.method_20398(lv.method_7928()));
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_16775.method_17393((arg2, arg3) -> this.method_7607(arg, arg2, this.field_16772));
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return method_17695(this.field_16775, arg, class_2246.field_16337);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			class_1799 lv4 = this.field_16772.method_5438(0);
			class_1799 lv5 = this.field_16772.method_5438(1);
			if (i == 2) {
				if (!this.method_7616(lv3, 3, 39, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i != 0 && i != 1) {
				if (!lv4.method_7960() && !lv5.method_7960()) {
					if (i >= 3 && i < 30) {
						if (!this.method_7616(lv3, 30, 39, false)) {
							return class_1799.field_8037;
						}
					} else if (i >= 30 && i < 39 && !this.method_7616(lv3, 3, 30, false)) {
						return class_1799.field_8037;
					}
				} else if (!this.method_7616(lv3, 0, 2, false)) {
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
}
