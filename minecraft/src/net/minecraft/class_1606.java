package net.minecraft;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1606 extends class_1427 implements class_1569 {
	private static final UUID field_7341 = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	private static final class_1322 field_7342 = new class_1322(field_7341, "Covered armor bonus", 20.0, class_1322.class_1323.field_6328).method_6187(false);
	protected static final class_2940<class_2350> field_7344 = class_2945.method_12791(class_1606.class, class_2943.field_13321);
	protected static final class_2940<Optional<class_2338>> field_7338 = class_2945.method_12791(class_1606.class, class_2943.field_13315);
	protected static final class_2940<Byte> field_7346 = class_2945.method_12791(class_1606.class, class_2943.field_13319);
	protected static final class_2940<Byte> field_7343 = class_2945.method_12791(class_1606.class, class_2943.field_13319);
	private float field_7339;
	private float field_7337;
	private class_2338 field_7345;
	private int field_7340;

	public class_1606(class_1937 arg) {
		super(class_1299.field_6109, arg);
		this.method_5835(1.0F, 1.0F);
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.field_5977 = true;
		this.field_7345 = null;
		this.field_6194 = 5;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.field_6283 = 180.0F;
		this.field_6220 = 180.0F;
		this.field_6031 = 180.0F;
		this.field_5982 = 180.0F;
		this.field_6241 = 180.0F;
		this.field_6259 = 180.0F;
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(4, new class_1606.class_1607());
		this.field_6201.method_6277(7, new class_1606.class_1611());
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this).method_6318());
		this.field_6185.method_6277(2, new class_1606.class_1610(this));
		this.field_6185.method_6277(3, new class_1606.class_1609(this));
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14690;
	}

	@Override
	public void method_5966() {
		if (!this.method_7124()) {
			super.method_5966();
		}
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15160;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_7124() ? class_3417.field_15135 : class_3417.field_15229;
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7344, class_2350.field_11033);
		this.field_6011.method_12784(field_7338, Optional.empty());
		this.field_6011.method_12784(field_7346, (byte)0);
		this.field_6011.method_12784(field_7343, (byte)16);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(30.0);
	}

	@Override
	protected class_1330 method_5963() {
		return new class_1606.class_1608(this);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_6011.method_12778(field_7344, class_2350.method_10143(arg.method_10571("AttachFace")));
		this.field_6011.method_12778(field_7346, arg.method_10571("Peek"));
		this.field_6011.method_12778(field_7343, arg.method_10571("Color"));
		if (arg.method_10545("APX")) {
			int i = arg.method_10550("APX");
			int j = arg.method_10550("APY");
			int k = arg.method_10550("APZ");
			this.field_6011.method_12778(field_7338, Optional.of(new class_2338(i, j, k)));
		} else {
			this.field_6011.method_12778(field_7338, Optional.empty());
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10567("AttachFace", (byte)this.field_6011.method_12789(field_7344).method_10146());
		arg.method_10567("Peek", this.field_6011.method_12789(field_7346));
		arg.method_10567("Color", this.field_6011.method_12789(field_7343));
		class_2338 lv = this.method_7123();
		if (lv != null) {
			arg.method_10569("APX", lv.method_10263());
			arg.method_10569("APY", lv.method_10264());
			arg.method_10569("APZ", lv.method_10260());
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		class_2338 lv = (class_2338)this.field_6011.method_12789(field_7338).orElse(null);
		if (lv == null && !this.field_6002.field_9236) {
			lv = new class_2338(this);
			this.field_6011.method_12778(field_7338, Optional.of(lv));
		}

		if (this.method_5765()) {
			lv = null;
			float f = this.method_5854().field_6031;
			this.field_6031 = f;
			this.field_6283 = f;
			this.field_6220 = f;
			this.field_7340 = 0;
		} else if (!this.field_6002.field_9236) {
			class_2680 lv2 = this.field_6002.method_8320(lv);
			if (!lv2.method_11588()) {
				if (lv2.method_11614() == class_2246.field_10008) {
					class_2350 lv3 = lv2.method_11654(class_2665.field_10927);
					if (this.field_6002.method_8623(lv.method_10093(lv3))) {
						lv = lv.method_10093(lv3);
						this.field_6011.method_12778(field_7338, Optional.of(lv));
					} else {
						this.method_7127();
					}
				} else if (lv2.method_11614() == class_2246.field_10379) {
					class_2350 lv3 = lv2.method_11654(class_2671.field_10927);
					if (this.field_6002.method_8623(lv.method_10093(lv3))) {
						lv = lv.method_10093(lv3);
						this.field_6011.method_12778(field_7338, Optional.of(lv));
					} else {
						this.method_7127();
					}
				} else {
					this.method_7127();
				}
			}

			class_2338 lv4 = lv.method_10093(this.method_7119());
			if (!this.field_6002.method_8515(lv4)) {
				boolean bl = false;

				for (class_2350 lv5 : class_2350.values()) {
					lv4 = lv.method_10093(lv5);
					if (this.field_6002.method_8515(lv4)) {
						this.field_6011.method_12778(field_7344, lv5);
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.method_7127();
				}
			}

			class_2338 lv6 = lv.method_10093(this.method_7119().method_10153());
			if (this.field_6002.method_8515(lv6)) {
				this.method_7127();
			}
		}

		float f = (float)this.method_7115() * 0.01F;
		this.field_7339 = this.field_7337;
		if (this.field_7337 > f) {
			this.field_7337 = class_3532.method_15363(this.field_7337 - 0.05F, f, 1.0F);
		} else if (this.field_7337 < f) {
			this.field_7337 = class_3532.method_15363(this.field_7337 + 0.05F, 0.0F, f);
		}

		if (lv != null) {
			if (this.field_6002.field_9236) {
				if (this.field_7340 > 0 && this.field_7345 != null) {
					this.field_7340--;
				} else {
					this.field_7345 = lv;
				}
			}

			this.field_5987 = (double)lv.method_10263() + 0.5;
			this.field_6010 = (double)lv.method_10264();
			this.field_6035 = (double)lv.method_10260() + 0.5;
			this.field_6014 = this.field_5987;
			this.field_6036 = this.field_6010;
			this.field_5969 = this.field_6035;
			this.field_6038 = this.field_5987;
			this.field_5971 = this.field_6010;
			this.field_5989 = this.field_6035;
			double d = 0.5 - (double)class_3532.method_15374((0.5F + this.field_7337) * (float) Math.PI) * 0.5;
			double e = 0.5 - (double)class_3532.method_15374((0.5F + this.field_7339) * (float) Math.PI) * 0.5;
			double g = d - e;
			double h = 0.0;
			double i = 0.0;
			double j = 0.0;
			class_2350 lv7 = this.method_7119();
			switch (lv7) {
				case field_11033:
					this.method_5857(
						new class_238(this.field_5987 - 0.5, this.field_6010, this.field_6035 - 0.5, this.field_5987 + 0.5, this.field_6010 + 1.0 + d, this.field_6035 + 0.5)
					);
					i = g;
					break;
				case field_11036:
					this.method_5857(
						new class_238(this.field_5987 - 0.5, this.field_6010 - d, this.field_6035 - 0.5, this.field_5987 + 0.5, this.field_6010 + 1.0, this.field_6035 + 0.5)
					);
					i = -g;
					break;
				case field_11043:
					this.method_5857(
						new class_238(this.field_5987 - 0.5, this.field_6010, this.field_6035 - 0.5, this.field_5987 + 0.5, this.field_6010 + 1.0, this.field_6035 + 0.5 + d)
					);
					j = g;
					break;
				case field_11035:
					this.method_5857(
						new class_238(this.field_5987 - 0.5, this.field_6010, this.field_6035 - 0.5 - d, this.field_5987 + 0.5, this.field_6010 + 1.0, this.field_6035 + 0.5)
					);
					j = -g;
					break;
				case field_11039:
					this.method_5857(
						new class_238(this.field_5987 - 0.5, this.field_6010, this.field_6035 - 0.5, this.field_5987 + 0.5 + d, this.field_6010 + 1.0, this.field_6035 + 0.5)
					);
					h = g;
					break;
				case field_11034:
					this.method_5857(
						new class_238(this.field_5987 - 0.5 - d, this.field_6010, this.field_6035 - 0.5, this.field_5987 + 0.5, this.field_6010 + 1.0, this.field_6035 + 0.5)
					);
					h = -g;
			}

			if (g > 0.0) {
				List<class_1297> list = this.field_6002.method_8335(this, this.method_5829());
				if (!list.isEmpty()) {
					for (class_1297 lv8 : list) {
						if (!(lv8 instanceof class_1606) && !lv8.field_5960) {
							lv8.method_5784(class_1313.field_6309, h, i, j);
						}
					}
				}
			}
		}
	}

	@Override
	public void method_5784(class_1313 arg, double d, double e, double f) {
		if (arg == class_1313.field_6306) {
			this.method_7127();
		} else {
			super.method_5784(arg, d, e, f);
		}
	}

	@Override
	public void method_5814(double d, double e, double f) {
		super.method_5814(d, e, f);
		if (this.field_6011 != null && this.field_6012 != 0) {
			Optional<class_2338> optional = this.field_6011.method_12789(field_7338);
			Optional<class_2338> optional2 = Optional.of(new class_2338(d, e, f));
			if (!optional2.equals(optional)) {
				this.field_6011.method_12778(field_7338, optional2);
				this.field_6011.method_12778(field_7346, (byte)0);
				this.field_6007 = true;
			}
		}
	}

	protected boolean method_7127() {
		if (!this.method_5987() && this.method_5805()) {
			class_2338 lv = new class_2338(this);

			for (int i = 0; i < 5; i++) {
				class_2338 lv2 = lv.method_10069(8 - this.field_5974.nextInt(17), 8 - this.field_5974.nextInt(17), 8 - this.field_5974.nextInt(17));
				if (lv2.method_10264() > 0
					&& this.field_6002.method_8623(lv2)
					&& this.field_6002.method_8625(this)
					&& this.field_6002.method_8587(this, new class_238(lv2))) {
					boolean bl = false;

					for (class_2350 lv3 : class_2350.values()) {
						if (this.field_6002.method_8515(lv2.method_10093(lv3))) {
							this.field_6011.method_12778(field_7344, lv3);
							bl = true;
							break;
						}
					}

					if (bl) {
						this.method_5783(class_3417.field_14915, 1.0F, 1.0F);
						this.field_6011.method_12778(field_7338, Optional.of(lv2));
						this.field_6011.method_12778(field_7346, (byte)0);
						this.method_5980(null);
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
	}

	@Override
	public void method_6007() {
		super.method_6007();
		this.field_5967 = 0.0;
		this.field_5984 = 0.0;
		this.field_6006 = 0.0;
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.field_6031 = 180.0F;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7338.equals(arg) && this.field_6002.field_9236 && !this.method_5765()) {
			class_2338 lv = this.method_7123();
			if (lv != null) {
				if (this.field_7345 == null) {
					this.field_7345 = lv;
				} else {
					this.field_7340 = 6;
				}

				this.field_5987 = (double)lv.method_10263() + 0.5;
				this.field_6010 = (double)lv.method_10264();
				this.field_6035 = (double)lv.method_10260() + 0.5;
				this.field_6014 = this.field_5987;
				this.field_6036 = this.field_6010;
				this.field_5969 = this.field_6035;
				this.field_6038 = this.field_5987;
				this.field_5971 = this.field_6010;
				this.field_5989 = this.field_6035;
			}
		}

		super.method_5674(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6210 = 0;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_7124()) {
			class_1297 lv = arg.method_5526();
			if (lv instanceof class_1665) {
				return false;
			}
		}

		if (super.method_5643(arg, f)) {
			if ((double)this.method_6032() < (double)this.method_6063() * 0.5 && this.field_5974.nextInt(4) == 0) {
				this.method_7127();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean method_7124() {
		return this.method_7115() == 0;
	}

	@Nullable
	@Override
	public class_238 method_5827() {
		return this.method_5805() ? this.method_5829() : null;
	}

	public class_2350 method_7119() {
		return this.field_6011.method_12789(field_7344);
	}

	@Nullable
	public class_2338 method_7123() {
		return (class_2338)this.field_6011.method_12789(field_7338).orElse(null);
	}

	public void method_7125(@Nullable class_2338 arg) {
		this.field_6011.method_12778(field_7338, Optional.ofNullable(arg));
	}

	public int method_7115() {
		return this.field_6011.method_12789(field_7346);
	}

	public void method_7122(int i) {
		if (!this.field_6002.field_9236) {
			this.method_5996(class_1612.field_7358).method_6202(field_7342);
			if (i == 0) {
				this.method_5996(class_1612.field_7358).method_6197(field_7342);
				this.method_5783(class_3417.field_15050, 1.0F, 1.0F);
			} else {
				this.method_5783(class_3417.field_15017, 1.0F, 1.0F);
			}
		}

		this.field_6011.method_12778(field_7346, (byte)i);
	}

	@Environment(EnvType.CLIENT)
	public float method_7116(float f) {
		return class_3532.method_16439(f, this.field_7339, this.field_7337);
	}

	@Environment(EnvType.CLIENT)
	public int method_7113() {
		return this.field_7340;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_7120() {
		return this.field_7345;
	}

	@Override
	public float method_5751() {
		return 0.5F;
	}

	@Override
	public int method_5978() {
		return 180;
	}

	@Override
	public int method_5986() {
		return 180;
	}

	@Override
	public void method_5697(class_1297 arg) {
	}

	@Override
	public float method_5871() {
		return 0.0F;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7117() {
		return this.field_7345 != null && this.method_7123() != null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1767 method_7121() {
		Byte byte_ = this.field_6011.method_12789(field_7343);
		return byte_ != 16 && byte_ <= 15 ? class_1767.method_7791(byte_) : null;
	}

	class class_1607 extends class_1352 {
		private int field_7347;

		public class_1607() {
			this.method_6265(3);
		}

		@Override
		public boolean method_6264() {
			class_1309 lv = class_1606.this.method_5968();
			return lv != null && lv.method_5805() ? class_1606.this.field_6002.method_8407() != class_1267.field_5801 : false;
		}

		@Override
		public void method_6269() {
			this.field_7347 = 20;
			class_1606.this.method_7122(100);
		}

		@Override
		public void method_6270() {
			class_1606.this.method_7122(0);
		}

		@Override
		public void method_6268() {
			if (class_1606.this.field_6002.method_8407() != class_1267.field_5801) {
				this.field_7347--;
				class_1309 lv = class_1606.this.method_5968();
				class_1606.this.method_5988().method_6226(lv, 180.0F, 180.0F);
				double d = class_1606.this.method_5858(lv);
				if (d < 400.0) {
					if (this.field_7347 <= 0) {
						this.field_7347 = 20 + class_1606.this.field_5974.nextInt(10) * 20 / 2;
						class_1678 lv2 = new class_1678(class_1606.this.field_6002, class_1606.this, lv, class_1606.this.method_7119().method_10166());
						class_1606.this.field_6002.method_8649(lv2);
						class_1606.this.method_5783(class_3417.field_15000, 2.0F, (class_1606.this.field_5974.nextFloat() - class_1606.this.field_5974.nextFloat()) * 0.2F + 1.0F);
					}
				} else {
					class_1606.this.method_5980(null);
				}

				super.method_6268();
			}
		}
	}

	class class_1608 extends class_1330 {
		public class_1608(class_1309 arg2) {
			super(arg2);
		}

		@Override
		public void method_6224() {
		}
	}

	static class class_1609 extends class_1400<class_1309> {
		public class_1609(class_1606 arg) {
			super(arg, class_1309.class, 10, true, false, argx -> argx instanceof class_1569);
		}

		@Override
		public boolean method_6264() {
			return this.field_6660.method_5781() == null ? false : super.method_6264();
		}

		@Override
		protected class_238 method_6321(double d) {
			class_2350 lv = ((class_1606)this.field_6660).method_7119();
			if (lv.method_10166() == class_2350.class_2351.field_11048) {
				return this.field_6660.method_5829().method_1009(4.0, d, d);
			} else {
				return lv.method_10166() == class_2350.class_2351.field_11051
					? this.field_6660.method_5829().method_1009(d, d, 4.0)
					: this.field_6660.method_5829().method_1009(d, 4.0, d);
			}
		}
	}

	class class_1610 extends class_1400<class_1657> {
		public class_1610(class_1606 arg2) {
			super(arg2, class_1657.class, true);
		}

		@Override
		public boolean method_6264() {
			return class_1606.this.field_6002.method_8407() == class_1267.field_5801 ? false : super.method_6264();
		}

		@Override
		protected class_238 method_6321(double d) {
			class_2350 lv = ((class_1606)this.field_6660).method_7119();
			if (lv.method_10166() == class_2350.class_2351.field_11048) {
				return this.field_6660.method_5829().method_1009(4.0, d, d);
			} else {
				return lv.method_10166() == class_2350.class_2351.field_11051
					? this.field_6660.method_5829().method_1009(d, d, 4.0)
					: this.field_6660.method_5829().method_1009(d, 4.0, d);
			}
		}
	}

	class class_1611 extends class_1352 {
		private int field_7352;

		private class_1611() {
		}

		@Override
		public boolean method_6264() {
			return class_1606.this.method_5968() == null && class_1606.this.field_5974.nextInt(40) == 0;
		}

		@Override
		public boolean method_6266() {
			return class_1606.this.method_5968() == null && this.field_7352 > 0;
		}

		@Override
		public void method_6269() {
			this.field_7352 = 20 * (1 + class_1606.this.field_5974.nextInt(3));
			class_1606.this.method_7122(30);
		}

		@Override
		public void method_6270() {
			if (class_1606.this.method_5968() == null) {
				class_1606.this.method_7122(0);
			}
		}

		@Override
		public void method_6268() {
			this.field_7352--;
		}
	}
}
