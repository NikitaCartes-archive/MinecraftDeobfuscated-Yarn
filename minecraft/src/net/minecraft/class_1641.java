package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3851.class
	)})
public class class_1641 extends class_1642 implements class_3851 {
	private static final class_2940<Boolean> field_7423 = class_2945.method_12791(class_1641.class, class_2943.field_13323);
	private static final class_2940<class_3850> field_7420 = class_2945.method_12791(class_1641.class, class_2943.field_17207);
	private int field_7422;
	private UUID field_7421;
	private class_2487 field_17047;

	public class_1641(class_1937 arg) {
		super(class_1299.field_6054, arg);
		this.method_7195(this.method_7231().method_16921(class_2378.field_17167.method_10240(this.field_5974)));
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7423, false);
		this.field_6011.method_12784(field_7420, new class_3850(class_3854.field_17073, class_3852.field_17051, 1));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10566("VillagerData", this.method_7231().method_16923(class_2509.field_11560));
		if (this.field_17047 != null) {
			arg.method_10566("Offers", this.field_17047);
		}

		arg.method_10569("ConversionTime", this.method_7198() ? this.field_7422 : -1);
		if (this.field_7421 != null) {
			arg.method_10560("ConversionPlayer", this.field_7421);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("VillagerData", 10)) {
			this.method_7195(new class_3850(new Dynamic<>(class_2509.field_11560, arg.method_10580("VillagerData"))));
		}

		if (arg.method_10573("Offers", 10)) {
			this.field_17047 = arg.method_10562("Offers");
		}

		if (arg.method_10573("ConversionTime", 99) && arg.method_10550("ConversionTime") > -1) {
			this.method_7199(arg.method_10576("ConversionPlayer") ? arg.method_10584("ConversionPlayer") : null, arg.method_10550("ConversionTime"));
		}
	}

	@Override
	public void method_5773() {
		if (!this.field_6002.field_9236 && this.method_7198()) {
			int i = this.method_7194();
			this.field_7422 -= i;
			if (this.field_7422 <= 0) {
				this.method_7197();
			}
		}

		super.method_5773();
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8463 && this.method_6059(class_1294.field_5911)) {
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			if (!this.field_6002.field_9236) {
				this.method_7199(arg.method_5667(), this.field_5974.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_7198();
	}

	public boolean method_7198() {
		return this.method_5841().method_12789(field_7423);
	}

	protected void method_7199(@Nullable UUID uUID, int i) {
		this.field_7421 = uUID;
		this.field_7422 = i;
		this.method_5841().method_12778(field_7423, true);
		this.method_6016(class_1294.field_5911);
		this.method_6092(new class_1293(class_1294.field_5910, i, Math.min(this.field_6002.method_8407().method_5461() - 1, 0)));
		this.field_6002.method_8421(this, (byte)16);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 16) {
			if (!this.method_5701()) {
				this.field_6002
					.method_8486(
						this.field_5987 + 0.5,
						this.field_6010 + 0.5,
						this.field_6035 + 0.5,
						class_3417.field_14905,
						this.method_5634(),
						1.0F + this.field_5974.nextFloat(),
						this.field_5974.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.method_5711(b);
		}
	}

	protected void method_7197() {
		class_1646 lv = new class_1646(this.field_6002);
		lv.method_5719(this);
		lv.method_7221(this.method_7231());
		if (this.field_17047 != null) {
			lv.method_16917(new class_1916(this.field_17047));
		}

		lv.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv)), class_3730.field_16468, null, null);
		lv.method_7238();
		if (this.method_6109()) {
			lv.method_5614(-24000);
		}

		this.field_6002.method_8463(this);
		lv.method_5977(this.method_5987());
		if (this.method_16914()) {
			lv.method_5665(this.method_5797());
			lv.method_5880(this.method_5807());
		}

		this.field_6002.method_8649(lv);
		if (this.field_7421 != null) {
			class_1657 lv2 = this.field_6002.method_8420(this.field_7421);
			if (lv2 instanceof class_3222) {
				class_174.field_1210.method_8831((class_3222)lv2, this, lv);
			}
		}

		lv.method_6092(new class_1293(class_1294.field_5916, 200, 0));
		this.field_6002.method_8444(null, 1027, new class_2338((int)this.field_5987, (int)this.field_6010, (int)this.field_6035), 0);
	}

	protected int method_7194() {
		int i = 1;
		if (this.field_5974.nextFloat() < 0.01F) {
			int j = 0;
			class_2338.class_2339 lv = new class_2338.class_2339();

			for (int k = (int)this.field_5987 - 4; k < (int)this.field_5987 + 4 && j < 14; k++) {
				for (int l = (int)this.field_6010 - 4; l < (int)this.field_6010 + 4 && j < 14; l++) {
					for (int m = (int)this.field_6035 - 4; m < (int)this.field_6035 + 4 && j < 14; m++) {
						class_2248 lv2 = this.field_6002.method_8320(lv.method_10103(k, l, m)).method_11614();
						if (lv2 == class_2246.field_10576 || lv2 instanceof class_2244) {
							if (this.field_5974.nextFloat() < 0.3F) {
								i++;
							}

							j++;
						}
					}
				}
			}
		}

		return i;
	}

	@Override
	protected float method_6017() {
		return this.method_6109()
			? (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 2.0F
			: (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public class_3414 method_5994() {
		return class_3417.field_15056;
	}

	@Override
	public class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14728;
	}

	@Override
	public class_3414 method_6002() {
		return class_3417.field_14996;
	}

	@Override
	public class_3414 method_7207() {
		return class_3417.field_14841;
	}

	@Override
	protected class_1799 method_7215() {
		return class_1799.field_8037;
	}

	public void method_16916(class_2487 arg) {
		this.field_17047 = arg;
	}

	public void method_7195(class_3850 arg) {
		class_3850 lv = this.method_7231();
		if (lv.method_16924() != arg.method_16924()) {
			this.field_17047 = null;
		}

		this.field_6011.method_12778(field_7420, arg);
	}

	@Override
	public class_3850 method_7231() {
		return this.field_6011.method_12789(field_7420);
	}
}
