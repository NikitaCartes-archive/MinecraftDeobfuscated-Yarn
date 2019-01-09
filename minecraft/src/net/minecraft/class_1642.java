package net.minecraft;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1642 extends class_1588 {
	protected static final class_1320 field_7428 = new class_1329(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0).method_6222("Spawn Reinforcements Chance");
	private static final UUID field_7429 = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final class_1322 field_7430 = new class_1322(field_7429, "Baby speed boost", 0.5, class_1322.class_1323.field_6330);
	private static final class_2940<Boolean> field_7434 = class_2945.method_12791(class_1642.class, class_2943.field_13323);
	private static final class_2940<Integer> field_7427 = class_2945.method_12791(class_1642.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_7431 = class_2945.method_12791(class_1642.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_7425 = class_2945.method_12791(class_1642.class, class_2943.field_13323);
	private final class_1339 field_7433 = new class_1339(this);
	private boolean field_7432;
	private int field_7426;
	private int field_7424;
	private float field_7435 = -1.0F;
	private float field_7436;

	public class_1642(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5835(0.6F, 1.95F);
	}

	public class_1642(class_1937 arg) {
		this(class_1299.field_6051, arg);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(4, new class_1642.class_1643(class_2246.field_10195, this, 1.0, 3));
		this.field_6201.method_6277(5, new class_1370(this, 1.0));
		this.field_6201.method_6277(8, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.method_7208();
	}

	protected void method_7208() {
		this.field_6201.method_6277(2, new class_1396(this, 1.0, false));
		this.field_6201.method_6277(6, new class_1368(this, 1.0, false));
		this.field_6201.method_6277(7, new class_1394(this, 1.0));
		this.field_6185.method_6277(1, new class_1399(this).method_6318(class_1590.class));
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_1646.class, false));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
		this.field_6185.method_6277(5, new class_1400(this, class_1481.class, 10, true, false, class_1481.field_6921));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7365).method_6192(35.0);
		this.method_5996(class_1612.field_7357).method_6192(0.23F);
		this.method_5996(class_1612.field_7363).method_6192(3.0);
		this.method_5996(class_1612.field_7358).method_6192(2.0);
		this.method_6127().method_6208(field_7428).method_6192(this.field_5974.nextDouble() * 0.1F);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.method_5841().method_12784(field_7434, false);
		this.method_5841().method_12784(field_7427, 0);
		this.method_5841().method_12784(field_7431, false);
		this.method_5841().method_12784(field_7425, false);
	}

	public boolean method_7206() {
		return this.method_5841().method_12789(field_7425);
	}

	public void method_7106(boolean bl) {
		this.method_5841().method_12778(field_7431, bl);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6999() {
		return this.method_5841().method_12789(field_7431);
	}

	public boolean method_7211() {
		return this.field_7432;
	}

	public void method_7201(boolean bl) {
		if (this.method_7212()) {
			if (this.field_7432 != bl) {
				this.field_7432 = bl;
				((class_1409)this.method_5942()).method_6363(bl);
				if (bl) {
					this.field_6201.method_6277(1, this.field_7433);
				} else {
					this.field_6201.method_6280(this.field_7433);
				}
			}
		} else if (this.field_7432) {
			this.field_6201.method_6280(this.field_7433);
			this.field_7432 = false;
		}
	}

	protected boolean method_7212() {
		return true;
	}

	@Override
	public boolean method_6109() {
		return this.method_5841().method_12789(field_7434);
	}

	@Override
	protected int method_6110(class_1657 arg) {
		if (this.method_6109()) {
			this.field_6194 = (int)((float)this.field_6194 * 2.5F);
		}

		return super.method_6110(arg);
	}

	public void method_7217(boolean bl) {
		this.method_5841().method_12778(field_7434, bl);
		if (this.field_6002 != null && !this.field_6002.field_9236) {
			class_1324 lv = this.method_5996(class_1612.field_7357);
			lv.method_6202(field_7430);
			if (bl) {
				lv.method_6197(field_7430);
			}
		}

		this.method_7214(bl);
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7434.equals(arg)) {
			this.method_7214(this.method_6109());
		}

		super.method_5674(arg);
	}

	protected boolean method_7209() {
		return true;
	}

	@Override
	public void method_5773() {
		if (!this.field_6002.field_9236) {
			if (this.method_7206()) {
				this.field_7424--;
				if (this.field_7424 < 0) {
					this.method_7218();
				}
			} else if (this.method_7209()) {
				if (this.method_5777(class_3486.field_15517)) {
					this.field_7426++;
					if (this.field_7426 >= 600) {
						this.method_7213(300);
					}
				} else {
					this.field_7426 = -1;
				}
			}
		}

		super.method_5773();
	}

	@Override
	public void method_6007() {
		boolean bl = this.method_7216() && this.method_5972();
		if (bl) {
			class_1799 lv = this.method_6118(class_1304.field_6169);
			if (!lv.method_7960()) {
				if (lv.method_7963()) {
					lv.method_7974(lv.method_7919() + this.field_5974.nextInt(2));
					if (lv.method_7919() >= lv.method_7936()) {
						this.method_6045(lv);
						this.method_5673(class_1304.field_6169, class_1799.field_8037);
					}
				}

				bl = false;
			}

			if (bl) {
				this.method_5639(8);
			}
		}

		super.method_6007();
	}

	private void method_7213(int i) {
		this.field_7424 = i;
		this.method_5841().method_12778(field_7425, true);
	}

	protected void method_7218() {
		this.method_7200(new class_1551(this.field_6002));
		this.field_6002.method_8444(null, 1040, new class_2338((int)this.field_5987, (int)this.field_6010, (int)this.field_6035), 0);
	}

	protected void method_7200(class_1642 arg) {
		if (!this.field_6002.field_9236 && !this.field_5988) {
			arg.method_5719(this);
			arg.method_7202(this.method_5936(), this.method_7211(), this.method_6109(), this.method_5987());

			for (class_1304 lv : class_1304.values()) {
				class_1799 lv2 = this.method_6118(lv);
				if (!lv2.method_7960()) {
					arg.method_5673(lv, lv2);
					arg.method_5946(lv, this.method_5929(lv));
				}
			}

			if (this.method_16914()) {
				arg.method_5665(this.method_5797());
				arg.method_5880(this.method_5807());
			}

			this.field_6002.method_8649(arg);
			this.method_5650();
		}
	}

	protected boolean method_7216() {
		return true;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (super.method_5643(arg, f)) {
			class_1309 lv = this.method_5968();
			if (lv == null && arg.method_5529() instanceof class_1309) {
				lv = (class_1309)arg.method_5529();
			}

			if (lv != null
				&& this.field_6002.method_8407() == class_1267.field_5807
				&& (double)this.field_5974.nextFloat() < this.method_5996(field_7428).method_6194()
				&& this.field_6002.method_8450().method_8355("doMobSpawning")) {
				int i = class_3532.method_15357(this.field_5987);
				int j = class_3532.method_15357(this.field_6010);
				int k = class_3532.method_15357(this.field_6035);
				class_1642 lv2 = new class_1642(this.field_6002);

				for (int l = 0; l < 50; l++) {
					int m = i + class_3532.method_15395(this.field_5974, 7, 40) * class_3532.method_15395(this.field_5974, -1, 1);
					int n = j + class_3532.method_15395(this.field_5974, 7, 40) * class_3532.method_15395(this.field_5974, -1, 1);
					int o = k + class_3532.method_15395(this.field_5974, 7, 40) * class_3532.method_15395(this.field_5974, -1, 1);
					class_2338 lv3 = new class_2338(m, n - 1, o);
					if (this.field_6002.method_8320(lv3).method_11631(this.field_6002, lv3) && this.field_6002.method_8602(new class_2338(m, n, o)) < 10) {
						lv2.method_5814((double)m, (double)n, (double)o);
						if (!this.field_6002.method_8528((double)m, (double)n, (double)o, 7.0)
							&& this.field_6002.method_8606(lv2, lv2.method_5829())
							&& this.field_6002.method_8587(lv2, lv2.method_5829())
							&& !this.field_6002.method_8599(lv2.method_5829())) {
							this.field_6002.method_8649(lv2);
							lv2.method_5980(lv);
							lv2.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv2)), class_3730.field_16463, null, null);
							this.method_5996(field_7428).method_6197(new class_1322("Zombie reinforcement caller charge", -0.05F, class_1322.class_1323.field_6328));
							lv2.method_5996(field_7428).method_6197(new class_1322("Zombie reinforcement callee charge", -0.05F, class_1322.class_1323.field_6328));
							break;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		boolean bl = super.method_6121(arg);
		if (bl) {
			float f = this.field_6002.method_8404(new class_2338(this)).method_5457();
			if (this.method_6047().method_7960() && this.method_5809() && this.field_5974.nextFloat() < f * 0.3F) {
				arg.method_5639(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15174;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15088;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14930;
	}

	protected class_3414 method_7207() {
		return class_3417.field_14621;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(this.method_7207(), 0.15F, 1.0F);
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	protected void method_5964(class_1266 arg) {
		super.method_5964(arg);
		if (this.field_5974.nextFloat() < (this.field_6002.method_8407() == class_1267.field_5807 ? 0.05F : 0.01F)) {
			int i = this.field_5974.nextInt(3);
			if (i == 0) {
				this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8371));
			} else {
				this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8699));
			}
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.method_6109()) {
			arg.method_10556("IsBaby", true);
		}

		arg.method_10556("CanBreakDoors", this.method_7211());
		arg.method_10569("InWaterTime", this.method_5799() ? this.field_7426 : -1);
		arg.method_10569("DrownedConversionTime", this.method_7206() ? this.field_7424 : -1);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10577("IsBaby")) {
			this.method_7217(true);
		}

		this.method_7201(arg.method_10577("CanBreakDoors"));
		this.field_7426 = arg.method_10550("InWaterTime");
		if (arg.method_10573("DrownedConversionTime", 99) && arg.method_10550("DrownedConversionTime") > -1) {
			this.method_7213(arg.method_10550("DrownedConversionTime"));
		}
	}

	@Override
	public void method_5874(class_1309 arg) {
		super.method_5874(arg);
		if ((this.field_6002.method_8407() == class_1267.field_5802 || this.field_6002.method_8407() == class_1267.field_5807) && arg instanceof class_1646) {
			if (this.field_6002.method_8407() != class_1267.field_5807 && this.field_5974.nextBoolean()) {
				return;
			}

			class_1646 lv = (class_1646)arg;
			class_1641 lv2 = new class_1641(this.field_6002);
			lv2.method_5719(lv);
			this.field_6002.method_8463(lv);
			lv2.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv2)), class_3730.field_16468, new class_1642.class_1644(false), null);
			lv2.method_7195(lv.method_7231());
			lv2.method_16916(lv.method_8264().method_8268());
			lv2.method_7217(lv.method_6109());
			lv2.method_5977(lv.method_5987());
			if (lv.method_16914()) {
				lv2.method_5665(lv.method_5797());
				lv2.method_5880(lv.method_5807());
			}

			this.field_6002.method_8649(lv2);
			this.field_6002.method_8444(null, 1026, new class_2338(this), 0);
		}
	}

	@Override
	public float method_5751() {
		float f = 1.74F;
		if (this.method_6109()) {
			f = (float)((double)f - 0.81);
		}

		return f;
	}

	@Override
	protected boolean method_5939(class_1799 arg) {
		return arg.method_7909() == class_1802.field_8803 && this.method_6109() && this.method_5765() ? false : super.method_5939(arg);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		float f = arg2.method_5458();
		this.method_5952(this.field_5974.nextFloat() < 0.55F * f);
		if (arg4 == null) {
			arg4 = new class_1642.class_1644(arg.method_8409().nextFloat() < 0.05F);
		}

		if (arg4 instanceof class_1642.class_1644) {
			class_1642.class_1644 lv = (class_1642.class_1644)arg4;
			if (lv.field_7439) {
				this.method_7217(true);
				if ((double)arg.method_8409().nextFloat() < 0.05) {
					List<class_1428> list = arg.method_8390(class_1428.class, this.method_5829().method_1009(5.0, 3.0, 5.0), class_1301.field_6153);
					if (!list.isEmpty()) {
						class_1428 lv2 = (class_1428)list.get(0);
						lv2.method_6473(true);
						this.method_5804(lv2);
					}
				} else if ((double)arg.method_8409().nextFloat() < 0.05) {
					class_1428 lv3 = new class_1428(this.field_6002);
					lv3.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, 0.0F);
					lv3.method_5943(arg, arg2, class_3730.field_16460, null, null);
					lv3.method_6473(true);
					arg.method_8649(lv3);
					this.method_5804(lv3);
				}
			}

			this.method_7201(this.method_7212() && this.field_5974.nextFloat() < f * 0.1F);
			this.method_5964(arg2);
			this.method_5984(arg2);
		}

		if (this.method_6118(class_1304.field_6169).method_7960()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.field_5974.nextFloat() < 0.25F) {
				this.method_5673(class_1304.field_6169, new class_1799(this.field_5974.nextFloat() < 0.1F ? class_2246.field_10009 : class_2246.field_10147));
				this.field_6186[class_1304.field_6169.method_5927()] = 0.0F;
			}
		}

		this.method_7205(f);
		return arg4;
	}

	protected void method_7202(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.method_5952(bl);
		this.method_7201(this.method_7212() && bl2);
		this.method_7205(this.field_6002.method_8404(new class_2338(this)).method_5458());
		this.method_7217(bl3);
		this.method_5977(bl4);
	}

	protected void method_7205(float f) {
		this.method_5996(class_1612.field_7360)
			.method_6197(new class_1322("Random spawn bonus", this.field_5974.nextDouble() * 0.05F, class_1322.class_1323.field_6328));
		double d = this.field_5974.nextDouble() * 1.5 * (double)f;
		if (d > 1.0) {
			this.method_5996(class_1612.field_7365).method_6197(new class_1322("Random zombie-spawn bonus", d, class_1322.class_1323.field_6331));
		}

		if (this.field_5974.nextFloat() < f * 0.05F) {
			this.method_5996(field_7428).method_6197(new class_1322("Leader zombie bonus", this.field_5974.nextDouble() * 0.25 + 0.5, class_1322.class_1323.field_6328));
			this.method_5996(class_1612.field_7359)
				.method_6197(new class_1322("Leader zombie bonus", this.field_5974.nextDouble() * 3.0 + 1.0, class_1322.class_1323.field_6331));
			this.method_7201(this.method_7212());
		}
	}

	public void method_7214(boolean bl) {
		this.method_7219(bl ? 0.5F : 1.0F);
	}

	@Override
	protected final void method_5835(float f, float g) {
		boolean bl = this.field_7435 > 0.0F && this.field_7436 > 0.0F;
		this.field_7435 = f;
		this.field_7436 = g;
		if (!bl) {
			this.method_7219(1.0F);
		}
	}

	protected final void method_7219(float f) {
		super.method_5835(this.field_7435 * f, this.field_7436 * f);
	}

	@Override
	public double method_5678() {
		return this.method_6109() ? 0.0 : -0.45;
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_1297 lv = arg.method_5529();
		if (lv instanceof class_1548) {
			class_1548 lv2 = (class_1548)lv;
			if (lv2.method_7008()) {
				lv2.method_7002();
				class_1799 lv3 = this.method_7215();
				if (!lv3.method_7960()) {
					this.method_5775(lv3);
				}
			}
		}
	}

	protected class_1799 method_7215() {
		return new class_1799(class_1802.field_8470);
	}

	class class_1643 extends class_1382 {
		class_1643(class_2248 arg2, class_1314 arg3, double d, int i) {
			super(arg2, arg3, d, i);
		}

		@Override
		public void method_6307(class_1936 arg, class_2338 arg2) {
			arg.method_8396(null, arg2, class_3417.field_15023, class_3419.field_15251, 0.5F, 0.9F + class_1642.this.field_5974.nextFloat() * 0.2F);
		}

		@Override
		public void method_6309(class_1937 arg, class_2338 arg2) {
			arg.method_8396(null, arg2, class_3417.field_14687, class_3419.field_15245, 0.7F, 0.9F + arg.field_9229.nextFloat() * 0.2F);
		}

		@Override
		public double method_6291() {
			return 1.3;
		}
	}

	public class class_1644 implements class_1315 {
		public final boolean field_7439;

		private class_1644(boolean bl) {
			this.field_7439 = bl;
		}
	}
}
