package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1533 extends class_1530 {
	private static final Logger field_7131 = LogManager.getLogger();
	private static final class_2940<class_1799> field_7130 = class_2945.method_12791(class_1533.class, class_2943.field_13322);
	private static final class_2940<Integer> field_7132 = class_2945.method_12791(class_1533.class, class_2943.field_13327);
	private float field_7129 = 1.0F;

	public class_1533(class_1299<? extends class_1533> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1533(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		super(class_1299.field_6043, arg, arg2);
		this.method_6892(arg3);
	}

	@Override
	protected float method_18378(class_4050 arg, class_4048 arg2) {
		return 0.0F;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_7130, class_1799.field_8037);
		this.method_5841().method_12784(field_7132, 0);
	}

	@Override
	protected void method_6892(class_2350 arg) {
		Validate.notNull(arg);
		this.field_7099 = arg;
		if (arg.method_10166().method_10179()) {
			this.field_5965 = 0.0F;
			this.field_6031 = (float)(this.field_7099.method_10161() * 90);
		} else {
			this.field_5965 = (float)(-90 * arg.method_10171().method_10181());
			this.field_6031 = 0.0F;
		}

		this.field_6004 = this.field_5965;
		this.field_5982 = this.field_6031;
		this.method_6895();
	}

	@Override
	protected void method_6895() {
		if (this.field_7099 != null) {
			double d = 0.46875;
			this.field_5987 = (double)this.field_7100.method_10263() + 0.5 - (double)this.field_7099.method_10148() * 0.46875;
			this.field_6010 = (double)this.field_7100.method_10264() + 0.5 - (double)this.field_7099.method_10164() * 0.46875;
			this.field_6035 = (double)this.field_7100.method_10260() + 0.5 - (double)this.field_7099.method_10165() * 0.46875;
			double e = (double)this.method_6897();
			double f = (double)this.method_6891();
			double g = (double)this.method_6897();
			class_2350.class_2351 lv = this.field_7099.method_10166();
			switch (lv) {
				case field_11048:
					e = 1.0;
					break;
				case field_11052:
					f = 1.0;
					break;
				case field_11051:
					g = 1.0;
			}

			e /= 32.0;
			f /= 32.0;
			g /= 32.0;
			this.method_5857(new class_238(this.field_5987 - e, this.field_6010 - f, this.field_6035 - g, this.field_5987 + e, this.field_6010 + f, this.field_6035 + g));
		}
	}

	@Override
	public boolean method_6888() {
		if (!this.field_6002.method_17892(this)) {
			return false;
		} else {
			class_2680 lv = this.field_6002.method_8320(this.field_7100.method_10093(this.field_7099.method_10153()));
			return lv.method_11620().method_15799() || this.field_7099.method_10166().method_10179() && class_2312.method_9999(lv)
				? this.field_6002.method_8333(this, this.method_5829(), field_7098).isEmpty()
				: false;
		}
	}

	@Override
	public float method_5871() {
		return 0.0F;
	}

	@Override
	public void method_5768() {
		this.method_6937(this.method_6940());
		super.method_5768();
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (!arg.method_5535() && !this.method_6940().method_7960()) {
			if (!this.field_6002.field_9236) {
				this.method_6936(arg.method_5529(), false);
				this.method_5783(class_3417.field_14770, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.method_5643(arg, f);
		}
	}

	@Override
	public int method_6897() {
		return 12;
	}

	@Override
	public int method_6891() {
		return 12;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = 16.0;
		e *= 64.0 * method_5824();
		return d < e * e;
	}

	@Override
	public void method_6889(@Nullable class_1297 arg) {
		this.method_5783(class_3417.field_14585, 1.0F, 1.0F);
		this.method_6936(arg, true);
	}

	@Override
	public void method_6894() {
		this.method_5783(class_3417.field_14844, 1.0F, 1.0F);
	}

	private void method_6936(@Nullable class_1297 arg, boolean bl) {
		if (!this.field_6002.method_8450().method_8355(class_1928.field_19393)) {
			if (arg == null) {
				this.method_6937(this.method_6940());
			}
		} else {
			class_1799 lv = this.method_6940();
			this.method_6935(class_1799.field_8037);
			if (arg instanceof class_1657) {
				class_1657 lv2 = (class_1657)arg;
				if (lv2.field_7503.field_7477) {
					this.method_6937(lv);
					return;
				}
			}

			if (bl) {
				this.method_5706(class_1802.field_8143);
			}

			if (!lv.method_7960()) {
				lv = lv.method_7972();
				this.method_6937(lv);
				if (this.field_5974.nextFloat() < this.field_7129) {
					this.method_5775(lv);
				}
			}
		}
	}

	private void method_6937(class_1799 arg) {
		if (arg.method_7909() == class_1802.field_8204) {
			class_22 lv = class_1806.method_8001(arg, this.field_6002);
			lv.method_104(this.field_7100, this.method_5628());
			lv.method_78(true);
		}

		arg.method_7943(null);
	}

	public class_1799 method_6940() {
		return this.method_5841().method_12789(field_7130);
	}

	public void method_6935(class_1799 arg) {
		this.method_6933(arg, true);
	}

	public void method_6933(class_1799 arg, boolean bl) {
		if (!arg.method_7960()) {
			arg = arg.method_7972();
			arg.method_7939(1);
			arg.method_7943(this);
		}

		this.method_5841().method_12778(field_7130, arg);
		if (!arg.method_7960()) {
			this.method_5783(class_3417.field_14667, 1.0F, 1.0F);
		}

		if (bl && this.field_7100 != null) {
			this.field_6002.method_8455(this.field_7100, class_2246.field_10124);
		}
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (i == 0) {
			this.method_6935(arg);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (arg.equals(field_7130)) {
			class_1799 lv = this.method_6940();
			if (!lv.method_7960() && lv.method_7945() != this) {
				lv.method_7943(this);
			}
		}
	}

	public int method_6934() {
		return this.method_5841().method_12789(field_7132);
	}

	public void method_6939(int i) {
		this.method_6941(i, true);
	}

	private void method_6941(int i, boolean bl) {
		this.method_5841().method_12778(field_7132, i % 8);
		if (bl && this.field_7100 != null) {
			this.field_6002.method_8455(this.field_7100, class_2246.field_10124);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (!this.method_6940().method_7960()) {
			arg.method_10566("Item", this.method_6940().method_7953(new class_2487()));
			arg.method_10567("ItemRotation", (byte)this.method_6934());
			arg.method_10548("ItemDropChance", this.field_7129);
		}

		arg.method_10567("Facing", (byte)this.field_7099.method_10146());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_2487 lv = arg.method_10562("Item");
		if (lv != null && !lv.isEmpty()) {
			class_1799 lv2 = class_1799.method_7915(lv);
			if (lv2.method_7960()) {
				field_7131.warn("Unable to load item from: {}", lv);
			}

			class_1799 lv3 = this.method_6940();
			if (!lv3.method_7960() && !class_1799.method_7973(lv2, lv3)) {
				this.method_6937(lv3);
			}

			this.method_6933(lv2, false);
			this.method_6941(arg.method_10571("ItemRotation"), false);
			if (arg.method_10573("ItemDropChance", 99)) {
				this.field_7129 = arg.method_10583("ItemDropChance");
			}
		}

		this.method_6892(class_2350.method_10143(arg.method_10571("Facing")));
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (!this.field_6002.field_9236) {
			if (this.method_6940().method_7960()) {
				if (!lv.method_7960()) {
					this.method_6935(lv);
					if (!arg.field_7503.field_7477) {
						lv.method_7934(1);
					}
				}
			} else {
				this.method_5783(class_3417.field_15038, 1.0F, 1.0F);
				this.method_6939(this.method_6934() + 1);
			}
		}

		return true;
	}

	public int method_6938() {
		return this.method_6940().method_7960() ? 0 : this.method_6934() % 8 + 1;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this, this.method_5864(), this.field_7099.method_10146(), this.method_6896());
	}
}
