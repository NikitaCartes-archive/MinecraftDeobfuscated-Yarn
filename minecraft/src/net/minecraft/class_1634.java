package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1634 extends class_1588 {
	protected static final class_2940<Byte> field_7410 = class_2945.method_12791(class_1634.class, class_2943.field_13319);
	private class_1308 field_7411;
	@Nullable
	private class_2338 field_7407;
	private boolean field_7409;
	private int field_7408;

	public class_1634(class_1299<? extends class_1634> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1634.class_1637(this);
		this.field_6194 = 3;
	}

	@Override
	public void method_5784(class_1313 arg, class_243 arg2) {
		super.method_5784(arg, arg2);
		this.method_5852();
	}

	@Override
	public void method_5773() {
		this.field_5960 = true;
		super.method_5773();
		this.field_5960 = false;
		this.method_5875(true);
		if (this.field_7409 && --this.field_7408 <= 0) {
			this.field_7408 = 20;
			this.method_5643(class_1282.field_5852, 1.0F);
		}
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(4, new class_1634.class_1635());
		this.field_6201.method_6277(8, new class_1634.class_1638());
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
		this.field_6185.method_6277(1, new class_1399(this, class_3763.class).method_6318());
		this.field_6185.method_6277(2, new class_1634.class_1636(this));
		this.field_6185.method_6277(3, new class_1400(this, class_1657.class, true));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(14.0);
		this.method_5996(class_1612.field_7363).method_6192(4.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7410, (byte)0);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("BoundX")) {
			this.field_7407 = new class_2338(arg.method_10550("BoundX"), arg.method_10550("BoundY"), arg.method_10550("BoundZ"));
		}

		if (arg.method_10545("LifeTicks")) {
			this.method_7181(arg.method_10550("LifeTicks"));
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_7407 != null) {
			arg.method_10569("BoundX", this.field_7407.method_10263());
			arg.method_10569("BoundY", this.field_7407.method_10264());
			arg.method_10569("BoundZ", this.field_7407.method_10260());
		}

		if (this.field_7409) {
			arg.method_10569("LifeTicks", this.field_7408);
		}
	}

	public class_1308 method_7182() {
		return this.field_7411;
	}

	@Nullable
	public class_2338 method_7186() {
		return this.field_7407;
	}

	public void method_7188(@Nullable class_2338 arg) {
		this.field_7407 = arg;
	}

	private boolean method_7184(int i) {
		int j = this.field_6011.method_12789(field_7410);
		return (j & i) != 0;
	}

	private void method_7189(int i, boolean bl) {
		int j = this.field_6011.method_12789(field_7410);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.field_6011.method_12778(field_7410, (byte)(j & 0xFF));
	}

	public boolean method_7176() {
		return this.method_7184(1);
	}

	public void method_7177(boolean bl) {
		this.method_7189(1, bl);
	}

	public void method_7178(class_1308 arg) {
		this.field_7411 = arg;
	}

	public void method_7181(int i) {
		this.field_7409 = true;
		this.field_7408 = i;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14812;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14964;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15072;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_5964(arg2);
		this.method_5984(arg2);
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	protected void method_5964(class_1266 arg) {
		this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8371));
		this.method_5946(class_1304.field_6173, 0.0F);
	}

	class class_1635 extends class_1352 {
		public class_1635() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			return class_1634.this.method_5968() != null && !class_1634.this.method_5962().method_6241() && class_1634.this.field_5974.nextInt(7) == 0
				? class_1634.this.method_5858(class_1634.this.method_5968()) > 4.0
				: false;
		}

		@Override
		public boolean method_6266() {
			return class_1634.this.method_5962().method_6241()
				&& class_1634.this.method_7176()
				&& class_1634.this.method_5968() != null
				&& class_1634.this.method_5968().method_5805();
		}

		@Override
		public void method_6269() {
			class_1309 lv = class_1634.this.method_5968();
			class_243 lv2 = lv.method_5836(1.0F);
			class_1634.this.field_6207.method_6239(lv2.field_1352, lv2.field_1351, lv2.field_1350, 1.0);
			class_1634.this.method_7177(true);
			class_1634.this.method_5783(class_3417.field_14898, 1.0F, 1.0F);
		}

		@Override
		public void method_6270() {
			class_1634.this.method_7177(false);
		}

		@Override
		public void method_6268() {
			class_1309 lv = class_1634.this.method_5968();
			if (class_1634.this.method_5829().method_994(lv.method_5829())) {
				class_1634.this.method_6121(lv);
				class_1634.this.method_7177(false);
			} else {
				double d = class_1634.this.method_5858(lv);
				if (d < 9.0) {
					class_243 lv2 = lv.method_5836(1.0F);
					class_1634.this.field_6207.method_6239(lv2.field_1352, lv2.field_1351, lv2.field_1350, 1.0);
				}
			}
		}
	}

	class class_1636 extends class_1405 {
		private final class_4051 field_18132 = new class_4051().method_18422().method_18424();

		public class_1636(class_1314 arg2) {
			super(arg2, false);
		}

		@Override
		public boolean method_6264() {
			return class_1634.this.field_7411 != null
				&& class_1634.this.field_7411.method_5968() != null
				&& this.method_6328(class_1634.this.field_7411.method_5968(), this.field_18132);
		}

		@Override
		public void method_6269() {
			class_1634.this.method_5980(class_1634.this.field_7411.method_5968());
			super.method_6269();
		}
	}

	class class_1637 extends class_1335 {
		public class_1637(class_1634 arg2) {
			super(arg2);
		}

		@Override
		public void method_6240() {
			if (this.field_6374 == class_1335.class_1336.field_6378) {
				class_243 lv = new class_243(
					this.field_6370 - class_1634.this.field_5987, this.field_6369 - class_1634.this.field_6010, this.field_6367 - class_1634.this.field_6035
				);
				double d = lv.method_1033();
				if (d < class_1634.this.method_5829().method_995()) {
					this.field_6374 = class_1335.class_1336.field_6377;
					class_1634.this.method_18799(class_1634.this.method_18798().method_1021(0.5));
				} else {
					class_1634.this.method_18799(class_1634.this.method_18798().method_1019(lv.method_1021(this.field_6372 * 0.05 / d)));
					if (class_1634.this.method_5968() == null) {
						class_243 lv2 = class_1634.this.method_18798();
						class_1634.this.field_6031 = -((float)class_3532.method_15349(lv2.field_1352, lv2.field_1350)) * (180.0F / (float)Math.PI);
						class_1634.this.field_6283 = class_1634.this.field_6031;
					} else {
						double e = class_1634.this.method_5968().field_5987 - class_1634.this.field_5987;
						double f = class_1634.this.method_5968().field_6035 - class_1634.this.field_6035;
						class_1634.this.field_6031 = -((float)class_3532.method_15349(e, f)) * (180.0F / (float)Math.PI);
						class_1634.this.field_6283 = class_1634.this.field_6031;
					}
				}
			}
		}
	}

	class class_1638 extends class_1352 {
		public class_1638() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			return !class_1634.this.method_5962().method_6241() && class_1634.this.field_5974.nextInt(7) == 0;
		}

		@Override
		public boolean method_6266() {
			return false;
		}

		@Override
		public void method_6268() {
			class_2338 lv = class_1634.this.method_7186();
			if (lv == null) {
				lv = new class_2338(class_1634.this);
			}

			for (int i = 0; i < 3; i++) {
				class_2338 lv2 = lv.method_10069(
					class_1634.this.field_5974.nextInt(15) - 7, class_1634.this.field_5974.nextInt(11) - 5, class_1634.this.field_5974.nextInt(15) - 7
				);
				if (class_1634.this.field_6002.method_8623(lv2)) {
					class_1634.this.field_6207.method_6239((double)lv2.method_10263() + 0.5, (double)lv2.method_10264() + 0.5, (double)lv2.method_10260() + 0.5, 0.25);
					if (class_1634.this.method_5968() == null) {
						class_1634.this.method_5988()
							.method_6230((double)lv2.method_10263() + 0.5, (double)lv2.method_10264() + 0.5, (double)lv2.method_10260() + 0.5, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}
}
