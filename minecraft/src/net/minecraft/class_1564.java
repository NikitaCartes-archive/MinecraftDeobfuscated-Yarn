package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1564 extends class_1617 {
	private class_1472 field_7264;

	public class_1564(class_1937 arg) {
		super(class_1299.field_6090, arg);
		this.method_5835(0.6F, 1.95F);
		this.field_6194 = 10;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1564.class_1566());
		this.field_6201.method_6277(2, new class_1338(this, class_1657.class, 8.0F, 0.6, 1.0));
		this.field_6201.method_6277(4, new class_1564.class_1567());
		this.field_6201.method_6277(5, new class_1564.class_1565());
		this.field_6201.method_6277(6, new class_1564.class_1568());
		this.field_6201.method_6277(8, new class_1379(this, 0.6));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
		this.field_6185.method_6277(1, new class_1399(this, class_1543.class).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true).method_6330(300));
		this.field_6185.method_6277(3, new class_1400(this, class_1646.class, false).method_6330(300));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, false));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.5);
		this.method_5996(class_1612.field_7365).method_6192(12.0);
		this.method_5996(class_1612.field_7359).method_6192(24.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
	}

	@Override
	protected void method_5958() {
		super.method_5958();
	}

	@Override
	public void method_5773() {
		super.method_5773();
	}

	@Override
	public boolean method_5722(class_1297 arg) {
		if (arg == null) {
			return false;
		} else if (arg == this) {
			return true;
		} else if (super.method_5722(arg)) {
			return true;
		} else if (arg instanceof class_1634) {
			return this.method_5722(((class_1634)arg).method_7182());
		} else {
			return arg instanceof class_1309 && ((class_1309)arg).method_6046() == class_1310.field_6291
				? this.method_5781() == null && arg.method_5781() == null
				: false;
		}
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14782;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14599;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15111;
	}

	private void method_7036(@Nullable class_1472 arg) {
		this.field_7264 = arg;
	}

	@Nullable
	private class_1472 method_7039() {
		return this.field_7264;
	}

	@Override
	protected class_3414 method_7142() {
		return class_3417.field_14858;
	}

	@Override
	public void method_16484(int i, boolean bl) {
	}

	class class_1565 extends class_1617.class_1620 {
		private class_1565() {
		}

		@Override
		protected int method_7149() {
			return 40;
		}

		@Override
		protected int method_7151() {
			return 100;
		}

		@Override
		protected void method_7148() {
			class_1309 lv = class_1564.this.method_5968();
			double d = Math.min(lv.field_6010, class_1564.this.field_6010);
			double e = Math.max(lv.field_6010, class_1564.this.field_6010) + 1.0;
			float f = (float)class_3532.method_15349(lv.field_6035 - class_1564.this.field_6035, lv.field_5987 - class_1564.this.field_5987);
			if (class_1564.this.method_5858(lv) < 9.0) {
				for (int i = 0; i < 5; i++) {
					float g = f + (float)i * (float) Math.PI * 0.4F;
					this.method_7044(
						class_1564.this.field_5987 + (double)class_3532.method_15362(g) * 1.5, class_1564.this.field_6035 + (double)class_3532.method_15374(g) * 1.5, d, e, g, 0
					);
				}

				for (int i = 0; i < 8; i++) {
					float g = f + (float)i * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
					this.method_7044(
						class_1564.this.field_5987 + (double)class_3532.method_15362(g) * 2.5, class_1564.this.field_6035 + (double)class_3532.method_15374(g) * 2.5, d, e, g, 3
					);
				}
			} else {
				for (int i = 0; i < 16; i++) {
					double h = 1.25 * (double)(i + 1);
					int j = 1 * i;
					this.method_7044(
						class_1564.this.field_5987 + (double)class_3532.method_15362(f) * h, class_1564.this.field_6035 + (double)class_3532.method_15374(f) * h, d, e, f, j
					);
				}
			}
		}

		private void method_7044(double d, double e, double f, double g, float h, int i) {
			class_2338 lv = new class_2338(d, g, e);
			boolean bl = false;
			double j = 0.0;

			do {
				if (!class_1564.this.field_6002.method_8515(lv) && class_1564.this.field_6002.method_8515(lv.method_10074())) {
					if (!class_1564.this.field_6002.method_8623(lv)) {
						class_2680 lv2 = class_1564.this.field_6002.method_8320(lv);
						class_265 lv3 = lv2.method_11628(class_1564.this.field_6002, lv);
						if (!lv3.method_1110()) {
							j = lv3.method_1105(class_2350.class_2351.field_11052);
						}
					}

					bl = true;
					break;
				}

				lv = lv.method_10074();
			} while (lv.method_10264() >= class_3532.method_15357(f) - 1);

			if (bl) {
				class_1669 lv4 = new class_1669(class_1564.this.field_6002, d, (double)lv.method_10264() + j, e, h, i, class_1564.this);
				class_1564.this.field_6002.method_8649(lv4);
			}
		}

		@Override
		protected class_3414 method_7150() {
			return class_3417.field_14908;
		}

		@Override
		protected class_1617.class_1618 method_7147() {
			return class_1617.class_1618.field_7380;
		}
	}

	class class_1566 extends class_1617.class_1619 {
		private class_1566() {
		}

		@Override
		public void method_6268() {
			if (class_1564.this.method_5968() != null) {
				class_1564.this.method_5988().method_6226(class_1564.this.method_5968(), (float)class_1564.this.method_5986(), (float)class_1564.this.method_5978());
			} else if (class_1564.this.method_7039() != null) {
				class_1564.this.method_5988().method_6226(class_1564.this.method_7039(), (float)class_1564.this.method_5986(), (float)class_1564.this.method_5978());
			}
		}
	}

	class class_1567 extends class_1617.class_1620 {
		private class_1567() {
		}

		@Override
		public boolean method_6264() {
			if (!super.method_6264()) {
				return false;
			} else {
				int i = class_1564.this.field_6002.method_8403(class_1634.class, class_1564.this.method_5829().method_1014(16.0)).size();
				return class_1564.this.field_5974.nextInt(8) + 1 > i;
			}
		}

		@Override
		protected int method_7149() {
			return 100;
		}

		@Override
		protected int method_7151() {
			return 340;
		}

		@Override
		protected void method_7148() {
			for (int i = 0; i < 3; i++) {
				class_2338 lv = new class_2338(class_1564.this).method_10069(-2 + class_1564.this.field_5974.nextInt(5), 1, -2 + class_1564.this.field_5974.nextInt(5));
				class_1634 lv2 = new class_1634(class_1564.this.field_6002);
				lv2.method_5725(lv, 0.0F, 0.0F);
				lv2.method_5943(class_1564.this.field_6002, class_1564.this.field_6002.method_8404(lv), class_3730.field_16471, null, null);
				lv2.method_7178(class_1564.this);
				lv2.method_7188(lv);
				lv2.method_7181(20 * (30 + class_1564.this.field_5974.nextInt(90)));
				class_1564.this.field_6002.method_8649(lv2);
			}
		}

		@Override
		protected class_3414 method_7150() {
			return class_3417.field_15193;
		}

		@Override
		protected class_1617.class_1618 method_7147() {
			return class_1617.class_1618.field_7379;
		}
	}

	public class class_1568 extends class_1617.class_1620 {
		private final Predicate<class_1472> field_7269 = argx -> argx.method_6633() == class_1767.field_7966;

		@Override
		public boolean method_6264() {
			if (class_1564.this.method_5968() != null) {
				return false;
			} else if (class_1564.this.method_7137()) {
				return false;
			} else if (class_1564.this.field_6012 < this.field_7384) {
				return false;
			} else if (!class_1564.this.field_6002.method_8450().method_8355("mobGriefing")) {
				return false;
			} else {
				List<class_1472> list = class_1564.this.field_6002
					.method_8390(class_1472.class, class_1564.this.method_5829().method_1009(16.0, 4.0, 16.0), this.field_7269);
				if (list.isEmpty()) {
					return false;
				} else {
					class_1564.this.method_7036((class_1472)list.get(class_1564.this.field_5974.nextInt(list.size())));
					return true;
				}
			}
		}

		@Override
		public boolean method_6266() {
			return class_1564.this.method_7039() != null && this.field_7385 > 0;
		}

		@Override
		public void method_6270() {
			super.method_6270();
			class_1564.this.method_7036(null);
		}

		@Override
		protected void method_7148() {
			class_1472 lv = class_1564.this.method_7039();
			if (lv != null && lv.method_5805()) {
				lv.method_6631(class_1767.field_7964);
			}
		}

		@Override
		protected int method_7146() {
			return 40;
		}

		@Override
		protected int method_7149() {
			return 60;
		}

		@Override
		protected int method_7151() {
			return 140;
		}

		@Override
		protected class_3414 method_7150() {
			return class_3417.field_15058;
		}

		@Override
		protected class_1617.class_1618 method_7147() {
			return class_1617.class_1618.field_7381;
		}
	}
}
