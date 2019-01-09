package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1617 extends class_1543 {
	private static final class_2940<Byte> field_7373 = class_2945.method_12791(class_1617.class, class_2943.field_13319);
	protected int field_7372;
	private class_1617.class_1618 field_7371 = class_1617.class_1618.field_7377;

	protected class_1617(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7373, (byte)0);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7372 = arg.method_10550("SpellTicks");
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("SpellTicks", this.field_7372);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1543.class_1544 method_6990() {
		return this.method_7137() ? class_1543.class_1544.field_7212 : class_1543.class_1544.field_7207;
	}

	public boolean method_7137() {
		return this.field_6002.field_9236 ? this.field_6011.method_12789(field_7373) > 0 : this.field_7372 > 0;
	}

	public void method_7138(class_1617.class_1618 arg) {
		this.field_7371 = arg;
		this.field_6011.method_12778(field_7373, (byte)arg.field_7375);
	}

	protected class_1617.class_1618 method_7140() {
		return !this.field_6002.field_9236 ? this.field_7371 : class_1617.class_1618.method_7144(this.field_6011.method_12789(field_7373));
	}

	@Override
	protected void method_5958() {
		super.method_5958();
		if (this.field_7372 > 0) {
			this.field_7372--;
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236 && this.method_7137()) {
			class_1617.class_1618 lv = this.method_7140();
			double d = lv.field_7374[0];
			double e = lv.field_7374[1];
			double f = lv.field_7374[2];
			float g = this.field_6283 * (float) (Math.PI / 180.0) + class_3532.method_15362((float)this.field_6012 * 0.6662F) * 0.25F;
			float h = class_3532.method_15362(g);
			float i = class_3532.method_15374(g);
			this.field_6002.method_8406(class_2398.field_11226, this.field_5987 + (double)h * 0.6, this.field_6010 + 1.8, this.field_6035 + (double)i * 0.6, d, e, f);
			this.field_6002.method_8406(class_2398.field_11226, this.field_5987 - (double)h * 0.6, this.field_6010 + 1.8, this.field_6035 - (double)i * 0.6, d, e, f);
		}
	}

	protected int method_7139() {
		return this.field_7372;
	}

	protected abstract class_3414 method_7142();

	public static enum class_1618 {
		field_7377(0, 0.0, 0.0, 0.0),
		field_7379(1, 0.7, 0.7, 0.8),
		field_7380(2, 0.4, 0.3, 0.35),
		field_7381(3, 0.7, 0.5, 0.2),
		field_7382(4, 0.3, 0.3, 0.8),
		field_7378(5, 0.1, 0.1, 0.2);

		private final int field_7375;
		private final double[] field_7374;

		private class_1618(int j, double d, double e, double f) {
			this.field_7375 = j;
			this.field_7374 = new double[]{d, e, f};
		}

		public static class_1617.class_1618 method_7144(int i) {
			for (class_1617.class_1618 lv : values()) {
				if (i == lv.field_7375) {
					return lv;
				}
			}

			return field_7377;
		}
	}

	public class class_1619 extends class_1352 {
		public class_1619() {
			this.method_6265(3);
		}

		@Override
		public boolean method_6264() {
			return class_1617.this.method_7139() > 0;
		}

		@Override
		public void method_6269() {
			super.method_6269();
			class_1617.this.field_6189.method_6340();
		}

		@Override
		public void method_6270() {
			super.method_6270();
			class_1617.this.method_7138(class_1617.class_1618.field_7377);
		}

		@Override
		public void method_6268() {
			if (class_1617.this.method_5968() != null) {
				class_1617.this.method_5988().method_6226(class_1617.this.method_5968(), (float)class_1617.this.method_5986(), (float)class_1617.this.method_5978());
			}
		}
	}

	public abstract class class_1620 extends class_1352 {
		protected int field_7385;
		protected int field_7384;

		protected class_1620() {
		}

		@Override
		public boolean method_6264() {
			if (class_1617.this.method_5968() == null) {
				return false;
			} else {
				return class_1617.this.method_7137() ? false : class_1617.this.field_6012 >= this.field_7384;
			}
		}

		@Override
		public boolean method_6266() {
			return class_1617.this.method_5968() != null && this.field_7385 > 0;
		}

		@Override
		public void method_6269() {
			this.field_7385 = this.method_7146();
			class_1617.this.field_7372 = this.method_7149();
			this.field_7384 = class_1617.this.field_6012 + this.method_7151();
			class_3414 lv = this.method_7150();
			if (lv != null) {
				class_1617.this.method_5783(lv, 1.0F, 1.0F);
			}

			class_1617.this.method_7138(this.method_7147());
		}

		@Override
		public void method_6268() {
			this.field_7385--;
			if (this.field_7385 == 0) {
				this.method_7148();
				class_1617.this.method_5783(class_1617.this.method_7142(), 1.0F, 1.0F);
			}
		}

		protected abstract void method_7148();

		protected int method_7146() {
			return 20;
		}

		protected abstract int method_7149();

		protected abstract int method_7151();

		@Nullable
		protected abstract class_3414 method_7150();

		protected abstract class_1617.class_1618 method_7147();
	}
}
