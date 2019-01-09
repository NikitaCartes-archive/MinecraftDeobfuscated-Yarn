package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1708 extends class_1703 {
	private final class_1263 field_7788;
	private final class_3913 field_17292;
	private final class_1735 field_7787;

	public class_1708(int i, class_1661 arg) {
		this(i, arg, new class_1277(5), new class_3919(2));
	}

	public class_1708(int i, class_1661 arg, class_1263 arg2, class_3913 arg3) {
		super(i);
		method_17359(arg2, 5);
		method_17361(arg3, 2);
		this.field_7788 = arg2;
		this.field_17292 = arg3;
		this.method_7621(new class_1708.class_1711(arg2, 0, 56, 51));
		this.method_7621(new class_1708.class_1711(arg2, 1, 79, 58));
		this.method_7621(new class_1708.class_1711(arg2, 2, 102, 51));
		this.field_7787 = this.method_7621(new class_1708.class_1710(arg2, 3, 79, 17));
		this.method_7621(new class_1708.class_1709(arg2, 4, 17, 17));
		this.method_17360(arg3);

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
	public class_3917<class_1708> method_17358() {
		return class_3917.field_17332;
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7788.method_5443(arg);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if ((i < 0 || i > 2) && i != 3 && i != 4) {
				if (this.field_7787.method_7680(lv3)) {
					if (!this.method_7616(lv3, 3, 4, false)) {
						return class_1799.field_8037;
					}
				} else if (class_1708.class_1711.method_7631(lv) && lv.method_7947() == 1) {
					if (!this.method_7616(lv3, 0, 3, false)) {
						return class_1799.field_8037;
					}
				} else if (class_1708.class_1709.method_7630(lv)) {
					if (!this.method_7616(lv3, 4, 5, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 5 && i < 32) {
					if (!this.method_7616(lv3, 32, 41, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 32 && i < 41) {
					if (!this.method_7616(lv3, 5, 32, false)) {
						return class_1799.field_8037;
					}
				} else if (!this.method_7616(lv3, 5, 41, false)) {
					return class_1799.field_8037;
				}
			} else {
				if (!this.method_7616(lv3, 5, 41, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
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

	@Environment(EnvType.CLIENT)
	public int method_17377() {
		return this.field_17292.method_17390(1);
	}

	@Environment(EnvType.CLIENT)
	public int method_17378() {
		return this.field_17292.method_17390(0);
	}

	static class class_1709 extends class_1735 {
		public class_1709(class_1263 arg, int i, int j, int k) {
			super(arg, i, j, k);
		}

		@Override
		public boolean method_7680(class_1799 arg) {
			return method_7630(arg);
		}

		public static boolean method_7630(class_1799 arg) {
			return arg.method_7909() == class_1802.field_8183;
		}

		@Override
		public int method_7675() {
			return 64;
		}
	}

	static class class_1710 extends class_1735 {
		public class_1710(class_1263 arg, int i, int j, int k) {
			super(arg, i, j, k);
		}

		@Override
		public boolean method_7680(class_1799 arg) {
			return class_1845.method_8077(arg);
		}

		@Override
		public int method_7675() {
			return 64;
		}
	}

	static class class_1711 extends class_1735 {
		public class_1711(class_1263 arg, int i, int j, int k) {
			super(arg, i, j, k);
		}

		@Override
		public boolean method_7680(class_1799 arg) {
			return method_7631(arg);
		}

		@Override
		public int method_7675() {
			return 1;
		}

		@Override
		public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
			class_1842 lv = class_1844.method_8063(arg2);
			if (arg instanceof class_3222) {
				class_174.field_1213.method_8784((class_3222)arg, lv);
			}

			super.method_7667(arg, arg2);
			return arg2;
		}

		public static boolean method_7631(class_1799 arg) {
			class_1792 lv = arg.method_7909();
			return lv == class_1802.field_8574 || lv == class_1802.field_8436 || lv == class_1802.field_8150 || lv == class_1802.field_8469;
		}
	}
}
