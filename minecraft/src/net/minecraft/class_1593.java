package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1593 extends class_1307 implements class_1569 {
	private static final class_2940<Integer> field_7313 = class_2945.method_12791(class_1593.class, class_2943.field_13327);
	private class_243 field_7314 = class_243.field_1353;
	private class_2338 field_7312 = class_2338.field_10980;
	private class_1593.class_1594 field_7315 = class_1593.class_1594.field_7318;

	public class_1593(class_1937 arg) {
		super(class_1299.field_6078, arg);
		this.field_6194 = 5;
		this.method_5835(0.9F, 0.5F);
		this.field_6207 = new class_1593.class_1600(this);
		this.field_6206 = new class_1593.class_1599(this);
	}

	@Override
	protected class_1330 method_5963() {
		return new class_1593.class_1597(this);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1593.class_1596());
		this.field_6201.method_6277(2, new class_1593.class_1602());
		this.field_6201.method_6277(3, new class_1593.class_1598());
		this.field_6185.method_6277(1, new class_1593.class_1595());
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(class_1612.field_7363);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7313, 0);
	}

	public void method_7091(int i) {
		if (i < 0) {
			i = 0;
		} else if (i > 64) {
			i = 64;
		}

		this.field_6011.method_12778(field_7313, i);
		this.method_7097();
	}

	public void method_7097() {
		int i = this.field_6011.method_12789(field_7313);
		this.method_5835(0.9F + 0.2F * (float)i, 0.5F + 0.1F * (float)i);
		this.method_5996(class_1612.field_7363).method_6192((double)(6 + i));
	}

	public int method_7084() {
		return this.field_6011.method_12789(field_7313);
	}

	@Override
	public float method_5751() {
		return this.field_6019 * 0.35F;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7313.equals(arg)) {
			this.method_7097();
		}

		super.method_5674(arg);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236) {
			float f = class_3532.method_15362((float)(this.method_5628() * 3 + this.field_6012) * 0.13F + (float) Math.PI);
			float g = class_3532.method_15362((float)(this.method_5628() * 3 + this.field_6012 + 1) * 0.13F + (float) Math.PI);
			if (f > 0.0F && g <= 0.0F) {
				this.field_6002
					.method_8486(
						this.field_5987,
						this.field_6010,
						this.field_6035,
						class_3417.field_14869,
						this.method_5634(),
						0.95F + this.field_5974.nextFloat() * 0.05F,
						0.95F + this.field_5974.nextFloat() * 0.05F,
						false
					);
			}

			int i = this.method_7084();
			float h = class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float j = class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float k = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
			this.field_6002.method_8406(class_2398.field_11219, this.field_5987 + (double)h, this.field_6010 + (double)k, this.field_6035 + (double)j, 0.0, 0.0, 0.0);
			this.field_6002.method_8406(class_2398.field_11219, this.field_5987 - (double)h, this.field_6010 + (double)k, this.field_6035 - (double)j, 0.0, 0.0, 0.0);
		}

		if (!this.field_6002.field_9236 && this.field_6002.method_8407() == class_1267.field_5801) {
			this.method_5650();
		}
	}

	@Override
	public void method_6007() {
		if (this.method_5972()) {
			this.method_5639(8);
		}

		super.method_6007();
	}

	@Override
	protected void method_5958() {
		super.method_5958();
	}

	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.field_7312 = new class_2338(this).method_10086(5);
		this.method_7091(0);
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("AX")) {
			this.field_7312 = new class_2338(arg.method_10550("AX"), arg.method_10550("AY"), arg.method_10550("AZ"));
		}

		this.method_7091(arg.method_10550("Size"));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("AX", this.field_7312.method_10263());
		arg.method_10569("AY", this.field_7312.method_10264());
		arg.method_10569("AZ", this.field_7312.method_10260());
		arg.method_10569("Size", this.method_7084());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		return true;
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14813;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15149;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14974;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	protected float method_6107() {
		return 1.0F;
	}

	@Override
	public boolean method_5973(Class<? extends class_1309> class_) {
		return true;
	}

	static enum class_1594 {
		field_7318,
		field_7317;
	}

	class class_1595 extends class_1352 {
		private int field_7320 = 20;

		private class_1595() {
		}

		@Override
		public boolean method_6264() {
			if (this.field_7320 > 0) {
				this.field_7320--;
				return false;
			} else {
				this.field_7320 = 60;
				class_238 lv = class_1593.this.method_5829().method_1009(16.0, 64.0, 16.0);
				List<class_1657> list = class_1593.this.field_6002.method_8403(class_1657.class, lv);
				if (!list.isEmpty()) {
					list.sort((arg, arg2) -> arg.field_6010 > arg2.field_6010 ? -1 : 1);

					for (class_1657 lv2 : list) {
						if (class_1405.method_6327(class_1593.this, lv2, false, false)) {
							class_1593.this.method_5980(lv2);
							return true;
						}
					}
				}

				return false;
			}
		}

		@Override
		public boolean method_6266() {
			return class_1405.method_6327(class_1593.this, class_1593.this.method_5968(), false, false);
		}
	}

	class class_1596 extends class_1352 {
		private int field_7322;

		private class_1596() {
		}

		@Override
		public boolean method_6264() {
			return class_1405.method_6327(class_1593.this, class_1593.this.method_5968(), false, false);
		}

		@Override
		public void method_6269() {
			this.field_7322 = 10;
			class_1593.this.field_7315 = class_1593.class_1594.field_7318;
			this.method_7102();
		}

		@Override
		public void method_6270() {
			class_1593.this.field_7312 = class_1593.this.field_6002
				.method_8598(class_2902.class_2903.field_13197, class_1593.this.field_7312)
				.method_10086(10 + class_1593.this.field_5974.nextInt(20));
		}

		@Override
		public void method_6268() {
			if (class_1593.this.field_7315 == class_1593.class_1594.field_7318) {
				this.field_7322--;
				if (this.field_7322 <= 0) {
					class_1593.this.field_7315 = class_1593.class_1594.field_7317;
					this.method_7102();
					this.field_7322 = (8 + class_1593.this.field_5974.nextInt(4)) * 20;
					class_1593.this.method_5783(class_3417.field_15238, 10.0F, 0.95F + class_1593.this.field_5974.nextFloat() * 0.1F);
				}
			}
		}

		private void method_7102() {
			class_1593.this.field_7312 = new class_2338(class_1593.this.method_5968()).method_10086(20 + class_1593.this.field_5974.nextInt(20));
			if (class_1593.this.field_7312.method_10264() < class_1593.this.field_6002.method_8615()) {
				class_1593.this.field_7312 = new class_2338(
					class_1593.this.field_7312.method_10263(), class_1593.this.field_6002.method_8615() + 1, class_1593.this.field_7312.method_10260()
				);
			}
		}
	}

	class class_1597 extends class_1330 {
		public class_1597(class_1309 arg2) {
			super(arg2);
		}

		@Override
		public void method_6224() {
			class_1593.this.field_6241 = class_1593.this.field_6283;
			class_1593.this.field_6283 = class_1593.this.field_6031;
		}
	}

	class class_1598 extends class_1593.class_1601 {
		private float field_7328;
		private float field_7327;
		private float field_7326;
		private float field_7324;

		private class_1598() {
		}

		@Override
		public boolean method_6264() {
			return class_1593.this.method_5968() == null || class_1593.this.field_7315 == class_1593.class_1594.field_7318;
		}

		@Override
		public void method_6269() {
			this.field_7327 = 5.0F + class_1593.this.field_5974.nextFloat() * 10.0F;
			this.field_7326 = -4.0F + class_1593.this.field_5974.nextFloat() * 9.0F;
			this.field_7324 = class_1593.this.field_5974.nextBoolean() ? 1.0F : -1.0F;
			this.method_7103();
		}

		@Override
		public void method_6268() {
			if (class_1593.this.field_5974.nextInt(350) == 0) {
				this.field_7326 = -4.0F + class_1593.this.field_5974.nextFloat() * 9.0F;
			}

			if (class_1593.this.field_5974.nextInt(250) == 0) {
				this.field_7327++;
				if (this.field_7327 > 15.0F) {
					this.field_7327 = 5.0F;
					this.field_7324 = -this.field_7324;
				}
			}

			if (class_1593.this.field_5974.nextInt(450) == 0) {
				this.field_7328 = class_1593.this.field_5974.nextFloat() * 2.0F * (float) Math.PI;
				this.method_7103();
			}

			if (this.method_7104()) {
				this.method_7103();
			}

			if (class_1593.this.field_7314.field_1351 < class_1593.this.field_6010
				&& !class_1593.this.field_6002.method_8623(new class_2338(class_1593.this).method_10087(1))) {
				this.field_7326 = Math.max(1.0F, this.field_7326);
				this.method_7103();
			}

			if (class_1593.this.field_7314.field_1351 > class_1593.this.field_6010
				&& !class_1593.this.field_6002.method_8623(new class_2338(class_1593.this).method_10086(1))) {
				this.field_7326 = Math.min(-1.0F, this.field_7326);
				this.method_7103();
			}
		}

		private void method_7103() {
			if (class_2338.field_10980.equals(class_1593.this.field_7312)) {
				class_1593.this.field_7312 = new class_2338(class_1593.this);
			}

			this.field_7328 = this.field_7328 + this.field_7324 * 15.0F * (float) (Math.PI / 180.0);
			class_1593.this.field_7314 = new class_243(class_1593.this.field_7312)
				.method_1031(
					(double)(this.field_7327 * class_3532.method_15362(this.field_7328)),
					(double)(-4.0F + this.field_7326),
					(double)(this.field_7327 * class_3532.method_15374(this.field_7328))
				);
		}
	}

	class class_1599 extends class_1333 {
		public class_1599(class_1308 arg2) {
			super(arg2);
		}

		@Override
		public void method_6231() {
		}
	}

	class class_1600 extends class_1335 {
		private float field_7331 = 0.1F;

		public class_1600(class_1308 arg2) {
			super(arg2);
		}

		@Override
		public void method_6240() {
			if (class_1593.this.field_5976) {
				class_1593.this.field_6031 += 180.0F;
				this.field_7331 = 0.1F;
			}

			float f = (float)(class_1593.this.field_7314.field_1352 - class_1593.this.field_5987);
			float g = (float)(class_1593.this.field_7314.field_1351 - class_1593.this.field_6010);
			float h = (float)(class_1593.this.field_7314.field_1350 - class_1593.this.field_6035);
			double d = (double)class_3532.method_15355(f * f + h * h);
			double e = 1.0 - (double)class_3532.method_15379(g * 0.7F) / d;
			f = (float)((double)f * e);
			h = (float)((double)h * e);
			d = (double)class_3532.method_15355(f * f + h * h);
			double i = (double)class_3532.method_15355(f * f + h * h + g * g);
			float j = class_1593.this.field_6031;
			float k = (float)class_3532.method_15349((double)h, (double)f);
			float l = class_3532.method_15393(class_1593.this.field_6031 + 90.0F);
			float m = class_3532.method_15393(k * (180.0F / (float)Math.PI));
			class_1593.this.field_6031 = class_3532.method_15388(l, m, 4.0F) - 90.0F;
			class_1593.this.field_6283 = class_1593.this.field_6031;
			if (class_3532.method_15356(j, class_1593.this.field_6031) < 3.0F) {
				this.field_7331 = class_3532.method_15348(this.field_7331, 1.8F, 0.005F * (1.8F / this.field_7331));
			} else {
				this.field_7331 = class_3532.method_15348(this.field_7331, 0.2F, 0.025F);
			}

			float n = (float)(-(class_3532.method_15349((double)(-g), d) * 180.0F / (float)Math.PI));
			class_1593.this.field_5965 = n;
			float o = class_1593.this.field_6031 + 90.0F;
			double p = (double)(this.field_7331 * class_3532.method_15362(o * (float) (Math.PI / 180.0))) * Math.abs((double)f / i);
			double q = (double)(this.field_7331 * class_3532.method_15374(o * (float) (Math.PI / 180.0))) * Math.abs((double)h / i);
			double r = (double)(this.field_7331 * class_3532.method_15374(n * (float) (Math.PI / 180.0))) * Math.abs((double)g / i);
			class_1593.this.field_5967 = class_1593.this.field_5967 + (p - class_1593.this.field_5967) * 0.2;
			class_1593.this.field_5984 = class_1593.this.field_5984 + (r - class_1593.this.field_5984) * 0.2;
			class_1593.this.field_6006 = class_1593.this.field_6006 + (q - class_1593.this.field_6006) * 0.2;
		}
	}

	abstract class class_1601 extends class_1352 {
		public class_1601() {
			this.method_6265(1);
		}

		protected boolean method_7104() {
			return class_1593.this.field_7314.method_1028(class_1593.this.field_5987, class_1593.this.field_6010, class_1593.this.field_6035) < 4.0;
		}
	}

	class class_1602 extends class_1593.class_1601 {
		private class_1602() {
		}

		@Override
		public boolean method_6264() {
			return class_1593.this.method_5968() != null && class_1593.this.field_7315 == class_1593.class_1594.field_7317;
		}

		@Override
		public boolean method_6266() {
			class_1309 lv = class_1593.this.method_5968();
			if (lv == null) {
				return false;
			} else if (!lv.method_5805()) {
				return false;
			} else if (!(lv instanceof class_1657) || !((class_1657)lv).method_7325() && !((class_1657)lv).method_7337()) {
				if (!this.method_6264()) {
					return false;
				} else {
					if (class_1593.this.field_6012 % 20 == 0) {
						List<class_1451> list = class_1593.this.field_6002.method_8390(class_1451.class, class_1593.this.method_5829().method_1014(16.0), class_1301.field_6154);
						if (!list.isEmpty()) {
							for (class_1451 lv2 : list) {
								lv2.method_16089();
							}

							return false;
						}
					}

					return true;
				}
			} else {
				return false;
			}
		}

		@Override
		public void method_6269() {
		}

		@Override
		public void method_6270() {
			class_1593.this.method_5980(null);
			class_1593.this.field_7315 = class_1593.class_1594.field_7318;
		}

		@Override
		public void method_6268() {
			class_1309 lv = class_1593.this.method_5968();
			class_1593.this.field_7314 = new class_243(lv.field_5987, lv.field_6010 + (double)lv.field_6019 * 0.5, lv.field_6035);
			if (class_1593.this.method_5829().method_1014(0.2F).method_994(lv.method_5829())) {
				class_1593.this.method_6121(lv);
				class_1593.this.field_7315 = class_1593.class_1594.field_7318;
				class_1593.this.field_6002.method_8535(1039, new class_2338(class_1593.this), 0);
			} else if (class_1593.this.field_5976 || class_1593.this.field_6235 > 0) {
				class_1593.this.field_7315 = class_1593.class_1594.field_7318;
			}
		}
	}
}
