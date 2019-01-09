package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1542 extends class_1297 {
	private static final class_2940<class_1799> field_7199 = class_2945.method_12791(class_1542.class, class_2943.field_13322);
	private int field_7204;
	private int field_7202;
	private int field_7201 = 5;
	private UUID field_7200;
	private UUID field_7205;
	public final float field_7203 = (float)(Math.random() * Math.PI * 2.0);

	public class_1542(class_1937 arg) {
		super(class_1299.field_6052, arg);
		this.method_5835(0.25F, 0.25F);
	}

	public class_1542(class_1937 arg, double d, double e, double f) {
		this(arg);
		this.method_5814(d, e, f);
		this.field_6031 = (float)(Math.random() * 360.0);
		this.field_5967 = (double)((float)(Math.random() * 0.2F - 0.1F));
		this.field_5984 = 0.2F;
		this.field_6006 = (double)((float)(Math.random() * 0.2F - 0.1F));
	}

	public class_1542(class_1937 arg, double d, double e, double f, class_1799 arg2) {
		this(arg, d, e, f);
		this.method_6979(arg2);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_7199, class_1799.field_8037);
	}

	@Override
	public void method_5773() {
		if (this.method_6983().method_7960()) {
			this.method_5650();
		} else {
			super.method_5773();
			if (this.field_7202 > 0 && this.field_7202 != 32767) {
				this.field_7202--;
			}

			this.field_6014 = this.field_5987;
			this.field_6036 = this.field_6010;
			this.field_5969 = this.field_6035;
			double d = this.field_5967;
			double e = this.field_5984;
			double f = this.field_6006;
			if (this.method_5777(class_3486.field_15517)) {
				this.method_6974();
			} else if (!this.method_5740()) {
				this.field_5984 -= 0.04F;
			}

			if (this.field_6002.field_9236) {
				this.field_5960 = false;
			} else {
				this.field_5960 = this.method_5632(this.field_5987, (this.method_5829().field_1322 + this.method_5829().field_1325) / 2.0, this.field_6035);
			}

			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			boolean bl = (int)this.field_6014 != (int)this.field_5987 || (int)this.field_6036 != (int)this.field_6010 || (int)this.field_5969 != (int)this.field_6035;
			if (bl || this.field_6012 % 25 == 0) {
				if (this.field_6002.method_8316(new class_2338(this)).method_15767(class_3486.field_15518)) {
					this.field_5984 = 0.2F;
					this.field_5967 = (double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F);
					this.field_6006 = (double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F);
					this.method_5783(class_3417.field_14821, 0.4F, 2.0F + this.field_5974.nextFloat() * 0.4F);
				}

				if (!this.field_6002.field_9236) {
					this.method_6973();
				}
			}

			float g = 0.98F;
			if (this.field_5952) {
				g = this.field_6002
						.method_8320(
							new class_2338(
								class_3532.method_15357(this.field_5987), class_3532.method_15357(this.method_5829().field_1322) - 1, class_3532.method_15357(this.field_6035)
							)
						)
						.method_11614()
						.method_9499()
					* 0.98F;
			}

			this.field_5967 *= (double)g;
			this.field_5984 *= 0.98F;
			this.field_6006 *= (double)g;
			if (this.field_5952) {
				this.field_5984 *= -0.5;
			}

			if (this.field_7204 != -32768) {
				this.field_7204++;
			}

			this.field_6007 = this.field_6007 | this.method_5713();
			if (!this.field_6002.field_9236) {
				double h = this.field_5967 - d;
				double i = this.field_5984 - e;
				double j = this.field_6006 - f;
				double k = h * h + i * i + j * j;
				if (k > 0.01) {
					this.field_6007 = true;
				}
			}

			if (!this.field_6002.field_9236 && this.field_7204 >= 6000) {
				this.method_5650();
			}
		}
	}

	private void method_6974() {
		if (this.field_5984 < 0.06F) {
			this.field_5984 += 5.0E-4F;
		}

		this.field_5967 *= 0.99F;
		this.field_6006 *= 0.99F;
	}

	private void method_6973() {
		for (class_1542 lv : this.field_6002.method_8403(class_1542.class, this.method_5829().method_1009(0.5, 0.0, 0.5))) {
			this.method_6972(lv);
		}
	}

	private boolean method_6972(class_1542 arg) {
		if (arg == this) {
			return false;
		} else if (arg.method_5805() && this.method_5805()) {
			class_1799 lv = this.method_6983();
			class_1799 lv2 = arg.method_6983().method_7972();
			if (this.field_7202 == 32767 || arg.field_7202 == 32767) {
				return false;
			} else if (this.field_7204 != -32768 && arg.field_7204 != -32768) {
				if (lv2.method_7909() != lv.method_7909()) {
					return false;
				} else if (lv2.method_7985() ^ lv.method_7985()) {
					return false;
				} else if (lv2.method_7985() && !lv2.method_7969().equals(lv.method_7969())) {
					return false;
				} else if (lv2.method_7909() == null) {
					return false;
				} else if (lv2.method_7947() < lv.method_7947()) {
					return arg.method_6972(this);
				} else if (lv2.method_7947() + lv.method_7947() > lv2.method_7914()) {
					return false;
				} else {
					lv2.method_7933(lv.method_7947());
					arg.field_7202 = Math.max(arg.field_7202, this.field_7202);
					arg.field_7204 = Math.min(arg.field_7204, this.field_7204);
					arg.method_6979(lv2);
					this.method_5650();
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void method_6980() {
		this.field_7204 = 4800;
	}

	@Override
	protected void method_5714(int i) {
		this.method_5643(class_1282.field_5867, (float)i);
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (!this.method_6983().method_7960() && this.method_6983().method_7909() == class_1802.field_8137 && arg.method_5535()) {
			return false;
		} else {
			this.method_5785();
			this.field_7201 = (int)((float)this.field_7201 - f);
			if (this.field_7201 <= 0) {
				this.method_5650();
			}

			return false;
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10575("Health", (short)this.field_7201);
		arg.method_10575("Age", (short)this.field_7204);
		arg.method_10575("PickupDelay", (short)this.field_7202);
		if (this.method_6978() != null) {
			arg.method_10566("Thrower", class_2512.method_10689(this.method_6978()));
		}

		if (this.method_6986() != null) {
			arg.method_10566("Owner", class_2512.method_10689(this.method_6986()));
		}

		if (!this.method_6983().method_7960()) {
			arg.method_10566("Item", this.method_6983().method_7953(new class_2487()));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7201 = arg.method_10568("Health");
		this.field_7204 = arg.method_10568("Age");
		if (arg.method_10545("PickupDelay")) {
			this.field_7202 = arg.method_10568("PickupDelay");
		}

		if (arg.method_10573("Owner", 10)) {
			this.field_7205 = class_2512.method_10690(arg.method_10562("Owner"));
		}

		if (arg.method_10573("Thrower", 10)) {
			this.field_7200 = class_2512.method_10690(arg.method_10562("Thrower"));
		}

		class_2487 lv = arg.method_10562("Item");
		this.method_6979(class_1799.method_7915(lv));
		if (this.method_6983().method_7960()) {
			this.method_5650();
		}
	}

	@Override
	public void method_5694(class_1657 arg) {
		if (!this.field_6002.field_9236) {
			class_1799 lv = this.method_6983();
			class_1792 lv2 = lv.method_7909();
			int i = lv.method_7947();
			if (this.field_7202 == 0
				&& (this.field_7205 == null || 6000 - this.field_7204 <= 200 || this.field_7205.equals(arg.method_5667()))
				&& arg.field_7514.method_7394(lv)) {
				arg.method_6103(this, i);
				if (lv.method_7960()) {
					this.method_5650();
					lv.method_7939(i);
				}

				arg.method_7342(class_3468.field_15392.method_14956(lv2), i);
			}
		}
	}

	@Override
	public class_2561 method_5477() {
		class_2561 lv = this.method_5797();
		return (class_2561)(lv != null ? lv : new class_2588(this.method_6983().method_7922()));
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Nullable
	@Override
	public class_1297 method_5731(class_2874 arg) {
		class_1297 lv = super.method_5731(arg);
		if (!this.field_6002.field_9236 && lv instanceof class_1542) {
			((class_1542)lv).method_6973();
		}

		return lv;
	}

	public class_1799 method_6983() {
		return this.method_5841().method_12789(field_7199);
	}

	public void method_6979(class_1799 arg) {
		this.method_5841().method_12778(field_7199, arg);
	}

	@Nullable
	public UUID method_6986() {
		return this.field_7205;
	}

	public void method_6984(@Nullable UUID uUID) {
		this.field_7205 = uUID;
	}

	@Nullable
	public UUID method_6978() {
		return this.field_7200;
	}

	public void method_6981(@Nullable UUID uUID) {
		this.field_7200 = uUID;
	}

	@Environment(EnvType.CLIENT)
	public int method_6985() {
		return this.field_7204;
	}

	public void method_6988() {
		this.field_7202 = 10;
	}

	public void method_6975() {
		this.field_7202 = 0;
	}

	public void method_6989() {
		this.field_7202 = 32767;
	}

	public void method_6982(int i) {
		this.field_7202 = i;
	}

	public boolean method_6977() {
		return this.field_7202 > 0;
	}

	public void method_6976() {
		this.field_7204 = -6000;
	}

	public void method_6987() {
		this.method_6989();
		this.field_7204 = 5999;
	}
}
