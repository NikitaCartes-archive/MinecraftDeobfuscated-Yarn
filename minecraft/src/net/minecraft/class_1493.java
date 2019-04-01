package net.minecraft;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1493 extends class_1321 {
	private static final class_2940<Float> field_6948 = class_2945.method_12791(class_1493.class, class_2943.field_13320);
	private static final class_2940<Boolean> field_6946 = class_2945.method_12791(class_1493.class, class_2943.field_13323);
	private static final class_2940<Integer> field_6950 = class_2945.method_12791(class_1493.class, class_2943.field_13327);
	public static final Predicate<class_1309> field_18004 = arg -> {
		class_1299<?> lv = arg.method_5864();
		return lv == class_1299.field_6115 || lv == class_1299.field_6140 || lv == class_1299.field_17943;
	};
	private float field_6952;
	private float field_6949;
	private boolean field_6944;
	private boolean field_6951;
	private float field_6947;
	private float field_6945;

	public class_1493(class_1299<? extends class_1493> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_6173(false);
	}

	@Override
	protected void method_5959() {
		this.field_6321 = new class_1386(this);
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(2, this.field_6321);
		this.field_6201.method_6277(3, new class_1493.class_1494(this, class_1501.class, 24.0F, 1.5, 1.5));
		this.field_6201.method_6277(4, new class_1359(this, 0.4F));
		this.field_6201.method_6277(5, new class_1366(this, 1.0, true));
		this.field_6201.method_6277(6, new class_1350(this, 1.0, 10.0F, 2.0F));
		this.field_6201.method_6277(7, new class_1341(this, 1.0));
		this.field_6201.method_6277(8, new class_1394(this, 1.0));
		this.field_6201.method_6277(9, new class_1337(this, 8.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(10, new class_1376(this));
		this.field_6185.method_6277(1, new class_1403(this));
		this.field_6185.method_6277(2, new class_1406(this));
		this.field_6185.method_6277(3, new class_1399(this).method_6318());
		this.field_6185.method_6277(4, new class_1404(this, class_1429.class, false, field_18004));
		this.field_6185.method_6277(4, new class_1404(this, class_1481.class, false, class_1481.field_6921));
		this.field_6185.method_6277(5, new class_1400(this, class_1547.class, false));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
		if (this.method_6181()) {
			this.method_5996(class_1612.field_7359).method_6192(20.0);
		} else {
			this.method_5996(class_1612.field_7359).method_6192(8.0);
		}

		this.method_6127().method_6208(class_1612.field_7363).method_6192(2.0);
	}

	@Override
	public void method_5980(@Nullable class_1309 arg) {
		super.method_5980(arg);
		if (arg == null) {
			this.method_6718(false);
		} else if (!this.method_6181()) {
			this.method_6718(true);
		}
	}

	@Override
	protected void method_5958() {
		this.field_6011.method_12778(field_6948, this.method_6032());
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6948, this.method_6032());
		this.field_6011.method_12784(field_6946, false);
		this.field_6011.method_12784(field_6950, class_1767.field_7964.method_7789());
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14772, 0.15F, 1.0F);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("Angry", this.method_6709());
		arg.method_10567("CollarColor", (byte)this.method_6713().method_7789());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6718(arg.method_10577("Angry"));
		if (arg.method_10573("CollarColor", 99)) {
			this.method_6708(class_1767.method_7791(arg.method_10550("CollarColor")));
		}
	}

	@Override
	protected class_3414 method_5994() {
		if (this.method_6709()) {
			return class_3417.field_14575;
		} else if (this.field_5974.nextInt(3) == 0) {
			return this.method_6181() && this.field_6011.method_12789(field_6948) < 10.0F ? class_3417.field_14807 : class_3417.field_14922;
		} else {
			return class_3417.field_14724;
		}
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15218;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14659;
	}

	@Override
	protected float method_6107() {
		return 0.4F;
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (!this.field_6002.field_9236 && this.field_6944 && !this.field_6951 && !this.method_6150() && this.field_5952) {
			this.field_6951 = true;
			this.field_6947 = 0.0F;
			this.field_6945 = 0.0F;
			this.field_6002.method_8421(this, (byte)8);
		}

		if (!this.field_6002.field_9236 && this.method_5968() == null && this.method_6709()) {
			this.method_6718(false);
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.method_5805()) {
			this.field_6949 = this.field_6952;
			if (this.method_6710()) {
				this.field_6952 = this.field_6952 + (1.0F - this.field_6952) * 0.4F;
			} else {
				this.field_6952 = this.field_6952 + (0.0F - this.field_6952) * 0.4F;
			}

			if (this.method_5637()) {
				this.field_6944 = true;
				this.field_6951 = false;
				this.field_6947 = 0.0F;
				this.field_6945 = 0.0F;
			} else if ((this.field_6944 || this.field_6951) && this.field_6951) {
				if (this.field_6947 == 0.0F) {
					this.method_5783(class_3417.field_15042, this.method_6107(), (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
				}

				this.field_6945 = this.field_6947;
				this.field_6947 += 0.05F;
				if (this.field_6945 >= 2.0F) {
					this.field_6944 = false;
					this.field_6951 = false;
					this.field_6945 = 0.0F;
					this.field_6947 = 0.0F;
				}

				if (this.field_6947 > 0.4F) {
					float f = (float)this.method_5829().field_1322;
					int i = (int)(class_3532.method_15374((this.field_6947 - 0.4F) * (float) Math.PI) * 7.0F);
					class_243 lv = this.method_18798();

					for (int j = 0; j < i; j++) {
						float g = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.method_17681() * 0.5F;
						float h = (this.field_5974.nextFloat() * 2.0F - 1.0F) * this.method_17681() * 0.5F;
						this.field_6002
							.method_8406(
								class_2398.field_11202, this.field_5987 + (double)g, (double)(f + 0.8F), this.field_6035 + (double)h, lv.field_1352, lv.field_1351, lv.field_1350
							);
					}
				}
			}
		}
	}

	@Override
	public void method_6078(class_1282 arg) {
		this.field_6944 = false;
		this.field_6951 = false;
		this.field_6945 = 0.0F;
		this.field_6947 = 0.0F;
		super.method_6078(arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6711() {
		return this.field_6944;
	}

	@Environment(EnvType.CLIENT)
	public float method_6707(float f) {
		return 0.75F + class_3532.method_16439(f, this.field_6945, this.field_6947) / 2.0F * 0.25F;
	}

	@Environment(EnvType.CLIENT)
	public float method_6715(float f, float g) {
		float h = (class_3532.method_16439(f, this.field_6945, this.field_6947) + g) / 1.8F;
		if (h < 0.0F) {
			h = 0.0F;
		} else if (h > 1.0F) {
			h = 1.0F;
		}

		return class_3532.method_15374(h * (float) Math.PI) * class_3532.method_15374(h * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}

	@Environment(EnvType.CLIENT)
	public float method_6719(float f) {
		return class_3532.method_16439(f, this.field_6949, this.field_6952) * 0.15F * (float) Math.PI;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.8F;
	}

	@Override
	public int method_5978() {
		return this.method_6172() ? 20 : super.method_5978();
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			class_1297 lv = arg.method_5529();
			if (this.field_6321 != null) {
				this.field_6321.method_6311(false);
			}

			if (lv != null && !(lv instanceof class_1657) && !(lv instanceof class_1665)) {
				f = (f + 1.0F) / 2.0F;
			}

			return super.method_5643(arg, f);
		}
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		boolean bl = arg.method_5643(class_1282.method_5511(this), (float)((int)this.method_5996(class_1612.field_7363).method_6194()));
		if (bl) {
			this.method_5723(this, arg);
		}

		return bl;
	}

	@Override
	public void method_6173(boolean bl) {
		super.method_6173(bl);
		if (bl) {
			this.method_5996(class_1612.field_7359).method_6192(20.0);
		} else {
			this.method_5996(class_1612.field_7359).method_6192(8.0);
		}

		this.method_5996(class_1612.field_7363).method_6192(4.0);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		class_1792 lv2 = lv.method_7909();
		if (this.method_6181()) {
			if (!lv.method_7960()) {
				if (lv2.method_19263()) {
					if (lv2.method_19264().method_19232() && this.field_6011.method_12789(field_6948) < 20.0F) {
						if (!arg.field_7503.field_7477) {
							lv.method_7934(1);
						}

						this.method_6025((float)lv2.method_19264().method_19230());
						return true;
					}
				} else if (lv2 instanceof class_1769) {
					class_1767 lv3 = ((class_1769)lv2).method_7802();
					if (lv3 != this.method_6713()) {
						this.method_6708(lv3);
						if (!arg.field_7503.field_7477) {
							lv.method_7934(1);
						}

						return true;
					}
				}
			}

			if (this.method_6171(arg) && !this.field_6002.field_9236 && !this.method_6481(lv)) {
				this.field_6321.method_6311(!this.method_6172());
				this.field_6282 = false;
				this.field_6189.method_6340();
				this.method_5980(null);
			}
		} else if (lv2 == class_1802.field_8606 && !this.method_6709()) {
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			if (!this.field_6002.field_9236) {
				if (this.field_5974.nextInt(3) == 0) {
					this.method_6170(arg);
					this.field_6189.method_6340();
					this.method_5980(null);
					this.field_6321.method_6311(true);
					this.method_6033(20.0F);
					this.method_6180(true);
					this.field_6002.method_8421(this, (byte)7);
				} else {
					this.method_6180(false);
					this.field_6002.method_8421(this, (byte)6);
				}
			}

			return true;
		}

		return super.method_5992(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 8) {
			this.field_6951 = true;
			this.field_6947 = 0.0F;
			this.field_6945 = 0.0F;
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6714() {
		if (this.method_6709()) {
			return 1.5393804F;
		} else {
			return this.method_6181() ? (0.55F - (this.method_6063() - this.field_6011.method_12789(field_6948)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
		}
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		return lv.method_19263() && lv.method_19264().method_19232();
	}

	@Override
	public int method_5945() {
		return 8;
	}

	public boolean method_6709() {
		return (this.field_6011.method_12789(field_6322) & 2) != 0;
	}

	public void method_6718(boolean bl) {
		byte b = this.field_6011.method_12789(field_6322);
		if (bl) {
			this.field_6011.method_12778(field_6322, (byte)(b | 2));
		} else {
			this.field_6011.method_12778(field_6322, (byte)(b & -3));
		}
	}

	public class_1767 method_6713() {
		return class_1767.method_7791(this.field_6011.method_12789(field_6950));
	}

	public void method_6708(class_1767 arg) {
		this.field_6011.method_12778(field_6950, arg.method_7789());
	}

	public class_1493 method_6717(class_1296 arg) {
		class_1493 lv = class_1299.field_6055.method_5883(this.field_6002);
		UUID uUID = this.method_6139();
		if (uUID != null) {
			lv.method_6174(uUID);
			lv.method_6173(true);
		}

		return lv;
	}

	public void method_6712(boolean bl) {
		this.field_6011.method_12778(field_6946, bl);
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		if (arg == this) {
			return false;
		} else if (!this.method_6181()) {
			return false;
		} else if (!(arg instanceof class_1493)) {
			return false;
		} else {
			class_1493 lv = (class_1493)arg;
			if (!lv.method_6181()) {
				return false;
			} else {
				return lv.method_6172() ? false : this.method_6479() && lv.method_6479();
			}
		}
	}

	public boolean method_6710() {
		return this.field_6011.method_12789(field_6946);
	}

	@Override
	public boolean method_6178(class_1309 arg, class_1309 arg2) {
		if (!(arg instanceof class_1548) && !(arg instanceof class_1571)) {
			if (arg instanceof class_1493) {
				class_1493 lv = (class_1493)arg;
				if (lv.method_6181() && lv.method_6177() == arg2) {
					return false;
				}
			}

			if (arg instanceof class_1657 && arg2 instanceof class_1657 && !((class_1657)arg2).method_7256((class_1657)arg)) {
				return false;
			} else {
				return arg instanceof class_1496 && ((class_1496)arg).method_6727() ? false : !(arg instanceof class_1451) || !((class_1451)arg).method_6181();
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return !this.method_6709() && super.method_5931(arg);
	}

	class class_1494<T extends class_1309> extends class_1338<T> {
		private final class_1493 field_6954;

		public class_1494(class_1493 arg2, Class<T> class_, float f, double d, double e) {
			super(arg2, class_, f, d, e);
			this.field_6954 = arg2;
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && this.field_6390 instanceof class_1501
				? !this.field_6954.method_6181() && this.method_6720((class_1501)this.field_6390)
				: false;
		}

		private boolean method_6720(class_1501 arg) {
			return arg.method_6803() >= class_1493.this.field_5974.nextInt(5);
		}

		@Override
		public void method_6269() {
			class_1493.this.method_5980(null);
			super.method_6269();
		}

		@Override
		public void method_6268() {
			class_1493.this.method_5980(null);
			super.method_6268();
		}
	}
}
