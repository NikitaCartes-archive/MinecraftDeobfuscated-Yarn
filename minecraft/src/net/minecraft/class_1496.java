package net.minecraft;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1496 extends class_1429 implements class_1265, class_1316 {
	private static final Predicate<class_1309> field_6956 = arg -> arg instanceof class_1496 && ((class_1496)arg).method_6744();
	private static final class_4051 field_18118 = new class_4051().method_18418(16.0).method_18417().method_18421().method_18422().method_18420(field_6956);
	protected static final class_1320 field_6974 = new class_1329(null, "horse.jumpStrength", 0.7, 0.0, 2.0).method_6222("Jump Strength").method_6212(true);
	private static final class_2940<Byte> field_6959 = class_2945.method_12791(class_1496.class, class_2943.field_13319);
	private static final class_2940<Optional<UUID>> field_6972 = class_2945.method_12791(class_1496.class, class_2943.field_13313);
	private int field_6971;
	private int field_6973;
	private int field_6970;
	public int field_6957;
	public int field_6958;
	protected boolean field_6968;
	protected class_1277 field_6962;
	protected int field_6955;
	protected float field_6976;
	private boolean field_6960;
	private float field_6969;
	private float field_6966;
	private float field_6967;
	private float field_6963;
	private float field_6965;
	private float field_6961;
	protected boolean field_6964 = true;
	protected int field_6975;

	protected class_1496(class_1299<? extends class_1496> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6013 = 1.0F;
		this.method_6721();
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1374(this, 1.2));
		this.field_6201.method_6277(1, new class_1387(this, 1.2));
		this.field_6201.method_6277(2, new class_1341(this, 1.0, class_1496.class));
		this.field_6201.method_6277(4, new class_1353(this, 1.0));
		this.field_6201.method_6277(6, new class_1394(this, 0.7));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.method_6764();
	}

	protected void method_6764() {
		this.field_6201.method_6277(0, new class_1347(this));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6959, (byte)0);
		this.field_6011.method_12784(field_6972, Optional.empty());
	}

	protected boolean method_6730(int i) {
		return (this.field_6011.method_12789(field_6959) & i) != 0;
	}

	protected void method_6769(int i, boolean bl) {
		byte b = this.field_6011.method_12789(field_6959);
		if (bl) {
			this.field_6011.method_12778(field_6959, (byte)(b | i));
		} else {
			this.field_6011.method_12778(field_6959, (byte)(b & ~i));
		}
	}

	public boolean method_6727() {
		return this.method_6730(2);
	}

	@Nullable
	public UUID method_6768() {
		return (UUID)this.field_6011.method_12789(field_6972).orElse(null);
	}

	public void method_6732(@Nullable UUID uUID) {
		this.field_6011.method_12778(field_6972, Optional.ofNullable(uUID));
	}

	public boolean method_6763() {
		return this.field_6968;
	}

	public void method_6766(boolean bl) {
		this.method_6769(2, bl);
	}

	public void method_6758(boolean bl) {
		this.field_6968 = bl;
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return super.method_5931(arg) && this.method_6046() != class_1310.field_6289;
	}

	@Override
	protected void method_6142(float f) {
		if (f > 6.0F && this.method_6724()) {
			this.method_6740(false);
		}
	}

	public boolean method_6724() {
		return this.method_6730(16);
	}

	public boolean method_6736() {
		return this.method_6730(32);
	}

	public boolean method_6744() {
		return this.method_6730(8);
	}

	public void method_6751(boolean bl) {
		this.method_6769(8, bl);
	}

	public void method_6753(boolean bl) {
		this.method_6769(4, bl);
	}

	public int method_6729() {
		return this.field_6955;
	}

	public void method_6749(int i) {
		this.field_6955 = i;
	}

	public int method_6745(int i) {
		int j = class_3532.method_15340(this.method_6729() + i, 0, this.method_6755());
		this.method_6749(j);
		return j;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		class_1297 lv = arg.method_5529();
		return this.method_5782() && lv != null && this.method_5821(lv) ? false : super.method_5643(arg, f);
	}

	@Override
	public boolean method_5810() {
		return !this.method_5782();
	}

	private void method_6733() {
		this.method_6738();
		if (!this.method_5701()) {
			this.field_6002
				.method_8465(
					null,
					this.field_5987,
					this.field_6010,
					this.field_6035,
					class_3417.field_15099,
					this.method_5634(),
					1.0F,
					1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F
				);
		}
	}

	@Override
	public void method_5747(float f, float g) {
		if (f > 1.0F) {
			this.method_5783(class_3417.field_14783, 0.4F, 1.0F);
		}

		int i = class_3532.method_15386((f * 0.5F - 3.0F) * g);
		if (i > 0) {
			this.method_5643(class_1282.field_5868, (float)i);
			if (this.method_5782()) {
				for (class_1297 lv : this.method_5736()) {
					lv.method_5643(class_1282.field_5868, (float)i);
				}
			}

			class_2680 lv2 = this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 - 0.2 - (double)this.field_5982, this.field_6035));
			if (!lv2.method_11588() && !this.method_5701()) {
				class_2498 lv3 = lv2.method_11638();
				this.field_6002
					.method_8465(
						null, this.field_5987, this.field_6010, this.field_6035, lv3.method_10594(), this.method_5634(), lv3.method_10597() * 0.5F, lv3.method_10599() * 0.75F
					);
			}
		}
	}

	protected int method_6750() {
		return 2;
	}

	protected void method_6721() {
		class_1277 lv = this.field_6962;
		this.field_6962 = new class_1277(this.method_6750());
		if (lv != null) {
			lv.method_5488(this);
			int i = Math.min(lv.method_5439(), this.field_6962.method_5439());

			for (int j = 0; j < i; j++) {
				class_1799 lv2 = lv.method_5438(j);
				if (!lv2.method_7960()) {
					this.field_6962.method_5447(j, lv2.method_7972());
				}
			}
		}

		this.field_6962.method_5489(this);
		this.method_6731();
	}

	protected void method_6731() {
		if (!this.field_6002.field_9236) {
			this.method_6753(!this.field_6962.method_5438(0).method_7960() && this.method_6765());
		}
	}

	@Override
	public void method_5453(class_1263 arg) {
		boolean bl = this.method_6725();
		this.method_6731();
		if (this.field_6012 > 20 && !bl && this.method_6725()) {
			this.method_5783(class_3417.field_14704, 0.5F, 1.0F);
		}
	}

	public double method_6771() {
		return this.method_5996(field_6974).method_6194();
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return null;
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		if (this.field_5974.nextInt(3) == 0) {
			this.method_6748();
		}

		return null;
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		if (this.field_5974.nextInt(10) == 0 && !this.method_6062()) {
			this.method_6748();
		}

		return null;
	}

	public boolean method_6765() {
		return true;
	}

	public boolean method_6725() {
		return this.method_6730(4);
	}

	@Nullable
	protected class_3414 method_6747() {
		this.method_6748();
		return null;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		if (!arg2.method_11620().method_15797()) {
			class_2680 lv = this.field_6002.method_8320(arg.method_10084());
			class_2498 lv2 = arg2.method_11638();
			if (lv.method_11614() == class_2246.field_10477) {
				lv2 = lv.method_11638();
			}

			if (this.method_5782() && this.field_6964) {
				this.field_6975++;
				if (this.field_6975 > 5 && this.field_6975 % 3 == 0) {
					this.method_6761(lv2);
				} else if (this.field_6975 <= 5) {
					this.method_5783(class_3417.field_15061, lv2.method_10597() * 0.15F, lv2.method_10599());
				}
			} else if (lv2 == class_2498.field_11547) {
				this.method_5783(class_3417.field_15061, lv2.method_10597() * 0.15F, lv2.method_10599());
			} else {
				this.method_5783(class_3417.field_14613, lv2.method_10597() * 0.15F, lv2.method_10599());
			}
		}
	}

	protected void method_6761(class_2498 arg) {
		this.method_5783(class_3417.field_14987, arg.method_10597() * 0.15F, arg.method_10599());
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(field_6974);
		this.method_5996(class_1612.field_7359).method_6192(53.0);
		this.method_5996(class_1612.field_7357).method_6192(0.225F);
	}

	@Override
	public int method_5945() {
		return 6;
	}

	public int method_6755() {
		return 100;
	}

	@Override
	protected float method_6107() {
		return 0.8F;
	}

	@Override
	public int method_5970() {
		return 400;
	}

	public void method_6722(class_1657 arg) {
		if (!this.field_6002.field_9236 && (!this.method_5782() || this.method_5626(arg)) && this.method_6727()) {
			arg.method_7291(this, this.field_6962);
		}
	}

	protected boolean method_6742(class_1657 arg, class_1799 arg2) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		class_1792 lv = arg2.method_7909();
		if (lv == class_1802.field_8861) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (lv == class_1802.field_8479) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (lv == class_2246.field_10359.method_8389()) {
			f = 20.0F;
			i = 180;
		} else if (lv == class_1802.field_8279) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (lv == class_1802.field_8071) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (this.method_6727() && this.method_5618() == 0 && !this.method_6479()) {
				bl = true;
				this.method_6480(arg);
			}
		} else if (lv == class_1802.field_8463 || lv == class_1802.field_8367) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (this.method_6727() && this.method_5618() == 0 && !this.method_6479()) {
				bl = true;
				this.method_6480(arg);
			}
		}

		if (this.method_6032() < this.method_6063() && f > 0.0F) {
			this.method_6025(f);
			bl = true;
		}

		if (this.method_6109() && i > 0) {
			this.field_6002
				.method_8406(
					class_2398.field_11211,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					0.0,
					0.0,
					0.0
				);
			if (!this.field_6002.field_9236) {
				this.method_5615(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.method_6727()) && this.method_6729() < this.method_6755()) {
			bl = true;
			if (!this.field_6002.field_9236) {
				this.method_6745(j);
			}
		}

		if (bl) {
			this.method_6733();
		}

		return bl;
	}

	protected void method_6726(class_1657 arg) {
		this.method_6740(false);
		this.method_6737(false);
		if (!this.field_6002.field_9236) {
			arg.field_6031 = this.field_6031;
			arg.field_5965 = this.field_5965;
			arg.method_5804(this);
		}
	}

	@Override
	protected boolean method_6062() {
		return super.method_6062() && this.method_5782() && this.method_6725() || this.method_6724() || this.method_6736();
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return false;
	}

	private void method_6759() {
		this.field_6957 = 1;
	}

	@Override
	protected void method_16078() {
		super.method_16078();
		if (this.field_6962 != null) {
			for (int i = 0; i < this.field_6962.method_5439(); i++) {
				class_1799 lv = this.field_6962.method_5438(i);
				if (!lv.method_7960()) {
					this.method_5775(lv);
				}
			}
		}
	}

	@Override
	public void method_6007() {
		if (this.field_5974.nextInt(200) == 0) {
			this.method_6759();
		}

		super.method_6007();
		if (!this.field_6002.field_9236 && this.method_5805()) {
			if (this.field_5974.nextInt(900) == 0 && this.field_6213 == 0) {
				this.method_6025(1.0F);
			}

			if (this.method_6762()) {
				if (!this.method_6724()
					&& !this.method_5782()
					&& this.field_5974.nextInt(300) == 0
					&& this.field_6002.method_8320(new class_2338(this).method_10074()).method_11614() == class_2246.field_10219) {
					this.method_6740(true);
				}

				if (this.method_6724() && ++this.field_6971 > 50) {
					this.field_6971 = 0;
					this.method_6740(false);
				}
			}

			this.method_6746();
		}
	}

	protected void method_6746() {
		if (this.method_6744() && this.method_6109() && !this.method_6724()) {
			class_1309 lv = this.field_6002
				.method_18465(class_1496.class, field_18118, this, this.field_5987, this.field_6010, this.field_6035, this.method_5829().method_1014(16.0));
			if (lv != null && this.method_5858(lv) > 4.0) {
				this.field_6189.method_6349(lv);
			}
		}
	}

	public boolean method_6762() {
		return true;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6973 > 0 && ++this.field_6973 > 30) {
			this.field_6973 = 0;
			this.method_6769(64, false);
		}

		if ((this.method_5787() || this.method_6034()) && this.field_6970 > 0 && ++this.field_6970 > 20) {
			this.field_6970 = 0;
			this.method_6737(false);
		}

		if (this.field_6957 > 0 && ++this.field_6957 > 8) {
			this.field_6957 = 0;
		}

		if (this.field_6958 > 0) {
			this.field_6958++;
			if (this.field_6958 > 300) {
				this.field_6958 = 0;
			}
		}

		this.field_6966 = this.field_6969;
		if (this.method_6724()) {
			this.field_6969 = this.field_6969 + (1.0F - this.field_6969) * 0.4F + 0.05F;
			if (this.field_6969 > 1.0F) {
				this.field_6969 = 1.0F;
			}
		} else {
			this.field_6969 = this.field_6969 + ((0.0F - this.field_6969) * 0.4F - 0.05F);
			if (this.field_6969 < 0.0F) {
				this.field_6969 = 0.0F;
			}
		}

		this.field_6963 = this.field_6967;
		if (this.method_6736()) {
			this.field_6969 = 0.0F;
			this.field_6966 = this.field_6969;
			this.field_6967 = this.field_6967 + (1.0F - this.field_6967) * 0.4F + 0.05F;
			if (this.field_6967 > 1.0F) {
				this.field_6967 = 1.0F;
			}
		} else {
			this.field_6960 = false;
			this.field_6967 = this.field_6967 + ((0.8F * this.field_6967 * this.field_6967 * this.field_6967 - this.field_6967) * 0.6F - 0.05F);
			if (this.field_6967 < 0.0F) {
				this.field_6967 = 0.0F;
			}
		}

		this.field_6961 = this.field_6965;
		if (this.method_6730(64)) {
			this.field_6965 = this.field_6965 + (1.0F - this.field_6965) * 0.7F + 0.05F;
			if (this.field_6965 > 1.0F) {
				this.field_6965 = 1.0F;
			}
		} else {
			this.field_6965 = this.field_6965 + ((0.0F - this.field_6965) * 0.7F - 0.05F);
			if (this.field_6965 < 0.0F) {
				this.field_6965 = 0.0F;
			}
		}
	}

	private void method_6738() {
		if (!this.field_6002.field_9236) {
			this.field_6973 = 1;
			this.method_6769(64, true);
		}
	}

	public void method_6740(boolean bl) {
		this.method_6769(16, bl);
	}

	public void method_6737(boolean bl) {
		if (bl) {
			this.method_6740(false);
		}

		this.method_6769(32, bl);
	}

	private void method_6748() {
		if (this.method_5787() || this.method_6034()) {
			this.field_6970 = 1;
			this.method_6737(true);
		}
	}

	public void method_6757() {
		this.method_6748();
		class_3414 lv = this.method_6747();
		if (lv != null) {
			this.method_5783(lv, this.method_6107(), this.method_6017());
		}
	}

	public boolean method_6752(class_1657 arg) {
		this.method_6732(arg.method_5667());
		this.method_6766(true);
		if (arg instanceof class_3222) {
			class_174.field_1201.method_9132((class_3222)arg, this);
		}

		this.field_6002.method_8421(this, (byte)7);
		return true;
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_5805()) {
			if (this.method_5782() && this.method_5956() && this.method_6725()) {
				class_1309 lv = (class_1309)this.method_5642();
				this.field_6031 = lv.field_6031;
				this.field_5982 = this.field_6031;
				this.field_5965 = lv.field_5965 * 0.5F;
				this.method_5710(this.field_6031, this.field_5965);
				this.field_6283 = this.field_6031;
				this.field_6241 = this.field_6283;
				float f = lv.field_6212 * 0.5F;
				float g = lv.field_6250;
				if (g <= 0.0F) {
					g *= 0.25F;
					this.field_6975 = 0;
				}

				if (this.field_5952 && this.field_6976 == 0.0F && this.method_6736() && !this.field_6960) {
					f = 0.0F;
					g = 0.0F;
				}

				if (this.field_6976 > 0.0F && !this.method_6763() && this.field_5952) {
					double d = this.method_6771() * (double)this.field_6976;
					double e;
					if (this.method_6059(class_1294.field_5913)) {
						e = d + (double)((float)(this.method_6112(class_1294.field_5913).method_5578() + 1) * 0.1F);
					} else {
						e = d;
					}

					class_243 lv2 = this.method_18798();
					this.method_18800(lv2.field_1352, e, lv2.field_1350);
					this.method_6758(true);
					this.field_6007 = true;
					if (g > 0.0F) {
						float h = class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0));
						float i = class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0));
						this.method_18799(this.method_18798().method_1031((double)(-0.4F * h * this.field_6976), 0.0, (double)(0.4F * i * this.field_6976)));
						this.method_6723();
					}

					this.field_6976 = 0.0F;
				}

				this.field_6281 = this.method_6029() * 0.1F;
				if (this.method_5787()) {
					this.method_6125((float)this.method_5996(class_1612.field_7357).method_6194());
					super.method_6091(new class_243((double)f, arg.field_1351, (double)g));
				} else if (lv instanceof class_1657) {
					this.method_18799(class_243.field_1353);
				}

				if (this.field_5952) {
					this.field_6976 = 0.0F;
					this.method_6758(false);
				}

				this.field_6211 = this.field_6225;
				double dx = this.field_5987 - this.field_6014;
				double ex = this.field_6035 - this.field_5969;
				float j = class_3532.method_15368(dx * dx + ex * ex) * 4.0F;
				if (j > 1.0F) {
					j = 1.0F;
				}

				this.field_6225 = this.field_6225 + (j - this.field_6225) * 0.4F;
				this.field_6249 = this.field_6249 + this.field_6225;
			} else {
				this.field_6281 = 0.02F;
				super.method_6091(arg);
			}
		}
	}

	protected void method_6723() {
		this.method_5783(class_3417.field_14831, 0.4F, 1.0F);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("EatingHaystack", this.method_6724());
		arg.method_10556("Bred", this.method_6744());
		arg.method_10569("Temper", this.method_6729());
		arg.method_10556("Tame", this.method_6727());
		if (this.method_6768() != null) {
			arg.method_10582("OwnerUUID", this.method_6768().toString());
		}

		if (!this.field_6962.method_5438(0).method_7960()) {
			arg.method_10566("SaddleItem", this.field_6962.method_5438(0).method_7953(new class_2487()));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6740(arg.method_10577("EatingHaystack"));
		this.method_6751(arg.method_10577("Bred"));
		this.method_6749(arg.method_10550("Temper"));
		this.method_6766(arg.method_10577("Tame"));
		String string;
		if (arg.method_10573("OwnerUUID", 8)) {
			string = arg.method_10558("OwnerUUID");
		} else {
			String string2 = arg.method_10558("Owner");
			string = class_3321.method_14546(this.method_5682(), string2);
		}

		if (!string.isEmpty()) {
			this.method_6732(UUID.fromString(string));
		}

		class_1324 lv = this.method_6127().method_6207("Speed");
		if (lv != null) {
			this.method_5996(class_1612.field_7357).method_6192(lv.method_6201() * 0.25);
		}

		if (arg.method_10573("SaddleItem", 10)) {
			class_1799 lv2 = class_1799.method_7915(arg.method_10562("SaddleItem"));
			if (lv2.method_7909() == class_1802.field_8175) {
				this.field_6962.method_5447(0, lv2);
			}
		}

		this.method_6731();
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		return false;
	}

	protected boolean method_6734() {
		return !this.method_5782()
			&& !this.method_5765()
			&& this.method_6727()
			&& !this.method_6109()
			&& this.method_6032() >= this.method_6063()
			&& this.method_6479();
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return null;
	}

	protected void method_6743(class_1296 arg, class_1496 arg2) {
		double d = this.method_5996(class_1612.field_7359).method_6201() + arg.method_5996(class_1612.field_7359).method_6201() + (double)this.method_6754();
		arg2.method_5996(class_1612.field_7359).method_6192(d / 3.0);
		double e = this.method_5996(field_6974).method_6201() + arg.method_5996(field_6974).method_6201() + this.method_6774();
		arg2.method_5996(field_6974).method_6192(e / 3.0);
		double f = this.method_5996(class_1612.field_7357).method_6201() + arg.method_5996(class_1612.field_7357).method_6201() + this.method_6728();
		arg2.method_5996(class_1612.field_7357).method_6192(f / 3.0);
	}

	@Override
	public boolean method_5956() {
		return this.method_5642() instanceof class_1309;
	}

	@Environment(EnvType.CLIENT)
	public float method_6739(float f) {
		return class_3532.method_16439(f, this.field_6966, this.field_6969);
	}

	@Environment(EnvType.CLIENT)
	public float method_6767(float f) {
		return class_3532.method_16439(f, this.field_6963, this.field_6967);
	}

	@Environment(EnvType.CLIENT)
	public float method_6772(float f) {
		return class_3532.method_16439(f, this.field_6961, this.field_6965);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_6154(int i) {
		if (this.method_6725()) {
			if (i < 0) {
				i = 0;
			} else {
				this.field_6960 = true;
				this.method_6748();
			}

			if (i >= 90) {
				this.field_6976 = 1.0F;
			} else {
				this.field_6976 = 0.4F + 0.4F * (float)i / 90.0F;
			}
		}
	}

	@Override
	public boolean method_6153() {
		return this.method_6725();
	}

	@Override
	public void method_6155(int i) {
		this.field_6960 = true;
		this.method_6748();
	}

	@Override
	public void method_6156() {
	}

	@Environment(EnvType.CLIENT)
	protected void method_6760(boolean bl) {
		class_2394 lv = bl ? class_2398.field_11201 : class_2398.field_11251;

		for (int i = 0; i < 7; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					lv,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					d,
					e,
					f
				);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 7) {
			this.method_6760(true);
		} else if (b == 6) {
			this.method_6760(false);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void method_5865(class_1297 arg) {
		super.method_5865(arg);
		if (arg instanceof class_1308) {
			class_1308 lv = (class_1308)arg;
			this.field_6283 = lv.field_6283;
		}

		if (this.field_6963 > 0.0F) {
			float f = class_3532.method_15374(this.field_6283 * (float) (Math.PI / 180.0));
			float g = class_3532.method_15362(this.field_6283 * (float) (Math.PI / 180.0));
			float h = 0.7F * this.field_6963;
			float i = 0.15F * this.field_6963;
			arg.method_5814(this.field_5987 + (double)(h * f), this.field_6010 + this.method_5621() + arg.method_5678() + (double)i, this.field_6035 - (double)(h * g));
			if (arg instanceof class_1309) {
				((class_1309)arg).field_6283 = this.field_6283;
			}
		}
	}

	protected float method_6754() {
		return 15.0F + (float)this.field_5974.nextInt(8) + (float)this.field_5974.nextInt(9);
	}

	protected double method_6774() {
		return 0.4F + this.field_5974.nextDouble() * 0.2 + this.field_5974.nextDouble() * 0.2 + this.field_5974.nextDouble() * 0.2;
	}

	protected double method_6728() {
		return (0.45F + this.field_5974.nextDouble() * 0.3 + this.field_5974.nextDouble() * 0.3 + this.field_5974.nextDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean method_6101() {
		return false;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.95F;
	}

	public boolean method_6735() {
		return false;
	}

	public boolean method_6773(class_1799 arg) {
		return false;
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		int j = i - 400;
		if (j >= 0 && j < 2 && j < this.field_6962.method_5439()) {
			if (j == 0 && arg.method_7909() != class_1802.field_8175) {
				return false;
			} else if (j != 1 || this.method_6735() && this.method_6773(arg)) {
				this.field_6962.method_5447(j, arg);
				this.method_6731();
				return true;
			} else {
				return false;
			}
		} else {
			int k = i - 500 + 2;
			if (k >= 2 && k < this.field_6962.method_5439()) {
				this.field_6962.method_5447(k, arg);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public class_1297 method_5642() {
		return this.method_5685().isEmpty() ? null : (class_1297)this.method_5685().get(0);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (this.field_5974.nextInt(5) == 0) {
			this.method_5614(-24000);
		}

		return arg4;
	}
}
