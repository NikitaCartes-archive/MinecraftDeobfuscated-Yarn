package net.minecraft;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3851.class
	)})
public class class_1646 extends class_1296 implements class_3758, class_1655, class_3851, class_1915 {
	private static final class_2940<class_3850> field_7445 = class_2945.method_12791(class_1646.class, class_2943.field_17207);
	private int field_7446;
	private boolean field_7457;
	private boolean field_7455;
	private class_1415 field_7451;
	@Nullable
	private class_1657 field_7461;
	@Nullable
	private class_1916 field_7448;
	private int field_7444;
	private boolean field_7453;
	private boolean field_7452;
	private String field_7454;
	private boolean field_7449;
	private boolean field_7447;
	private final class_1277 field_7458 = new class_1277(8);

	public class_1646(class_1937 arg) {
		this(arg, class_3854.field_17073);
	}

	public class_1646(class_1937 arg, class_3854 arg2) {
		super(class_1299.field_6077, arg);
		this.method_5835(0.6F, 1.95F);
		((class_1409)this.method_5942()).method_6363(true);
		this.method_5952(true);
		this.method_7221(this.method_7231().method_16921(class_2378.field_17167.method_10240(this.field_5974)));
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1338(this, class_1642.class, 8.0F, 0.6, 0.6));
		this.field_6201.method_6277(1, new class_1338(this, class_1564.class, 12.0F, 0.8, 0.8));
		this.field_6201.method_6277(1, new class_1338(this, class_1632.class, 8.0F, 0.8, 0.8));
		this.field_6201.method_6277(1, new class_1338(this, class_1634.class, 8.0F, 0.6, 0.6));
		this.field_6201.method_6277(1, new class_1338(this, class_1604.class, 15.0F, 0.6, 0.6));
		this.field_6201.method_6277(1, new class_1338(this, class_1581.class, 12.0F, 0.6, 0.6));
		this.field_6201.method_6277(1, new class_1390(this));
		this.field_6201.method_6277(1, new class_1364(this));
		this.field_6201.method_6277(2, new class_1365(this));
		this.field_6201.method_6277(3, new class_1385(this));
		this.field_6201.method_6277(4, new class_1375(this, true));
		this.field_6201.method_6277(5, new class_1370(this, 0.6));
		this.field_6201.method_6277(6, new class_1363(this));
		this.field_6201.method_6277(7, new class_1388(this));
		this.field_6201.method_6277(9, new class_1358(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(9, new class_1392(this));
		this.field_6201.method_6277(9, new class_1394(this, 0.6));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
	}

	private void method_7230() {
		if (!this.field_7447) {
			this.field_7447 = true;
			if (this.method_6109()) {
				this.field_6201.method_6277(8, new class_1377(this, 0.32));
			} else if (this.method_7231().method_16924() == class_3852.field_17056) {
				this.field_6201.method_6277(6, new class_1354(this, 0.6));
			}
		}
	}

	@Override
	protected void method_5619() {
		if (this.method_7231().method_16924() == class_3852.field_17056) {
			this.field_6201.method_6277(8, new class_1354(this, 0.6));
		}

		super.method_5619();
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.5);
	}

	@Override
	protected void method_5958() {
		if (--this.field_7446 <= 0) {
			class_2338 lv = new class_2338(this);
			this.field_6002.method_8557().method_6435(lv);
			this.field_7446 = 70 + this.field_5974.nextInt(50);
			this.field_7451 = this.field_6002.method_8557().method_6438(lv, 32);
			if (this.field_7451 == null) {
				this.method_6147();
			} else {
				class_2338 lv2 = this.field_7451.method_6382();
				this.method_6145(lv2, this.field_7451.method_6403());
				if (this.field_7449) {
					this.field_7449 = false;
					this.field_7451.method_6401(5);
				}
			}
		}

		if (!this.method_7235() && this.field_7444 > 0) {
			this.field_7444--;
			if (this.field_7444 <= 0) {
				if (this.field_7453) {
					for (class_1914 lv3 : this.method_8264()) {
						if (lv3.method_8255()) {
							lv3.method_8245(this.field_5974.nextInt(6) + this.field_5974.nextInt(6) + 2);
						}
					}

					this.method_16918();
					this.field_7453 = false;
					if (this.field_7451 != null && this.field_7454 != null) {
						this.field_6002.method_8421(this, (byte)14);
						this.field_7451.method_6393(this.field_7454, 1);
					}
				}

				this.method_6092(new class_1293(class_1294.field_5924, 200, 0));
			}
		}

		super.method_5958();
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		boolean bl = lv.method_7909() == class_1802.field_8448;
		if (bl) {
			lv.method_7920(arg, this, arg2);
			return true;
		} else if (lv.method_7909() != class_1802.field_8086 && this.method_5805() && !this.method_7235() && !this.method_6109()) {
			if (arg2 == class_1268.field_5808) {
				arg.method_7281(class_3468.field_15384);
			}

			if (this.method_8264().isEmpty()) {
				return super.method_5992(arg, arg2);
			} else {
				if (!this.field_6002.field_9236 && !this.field_7448.isEmpty()) {
					if (this.field_7451 != null && this.field_7451.method_16469() != null && this.field_7451.method_16469().method_16832()) {
						this.field_6002.method_8421(this, (byte)42);
					} else {
						this.method_8259(arg);
						this.method_17449(arg, this.method_5476());
					}
				}

				return true;
			}
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7445, new class_3850(class_3854.field_17073, class_3852.field_17051, 1));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10566("VillagerData", this.method_7231().method_16923(class_2509.field_11560));
		arg.method_10556("Willing", this.field_7452);
		class_1916 lv = this.method_8264();
		if (!lv.isEmpty()) {
			arg.method_10566("Offers", lv.method_8268());
		}

		class_2499 lv2 = new class_2499();

		for (int i = 0; i < this.field_7458.method_5439(); i++) {
			class_1799 lv3 = this.field_7458.method_5438(i);
			if (!lv3.method_7960()) {
				lv2.method_10606(lv3.method_7953(new class_2487()));
			}
		}

		arg.method_10566("Inventory", lv2);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("VillagerData", 10)) {
			this.method_7221(new class_3850(new Dynamic<>(class_2509.field_11560, arg.method_10580("VillagerData"))));
		}

		if (arg.method_10573("Offers", 10)) {
			this.field_7448 = new class_1916(arg.method_10562("Offers"));
		}

		this.field_7452 = arg.method_10577("Willing");
		class_2499 lv = arg.method_10554("Inventory", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = class_1799.method_7915(lv.method_10602(i));
			if (!lv2.method_7960()) {
				this.field_7458.method_5491(lv2);
			}
		}

		this.method_5952(true);
		this.method_7230();
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_7235() ? class_3417.field_14933 : class_3417.field_15175;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15139;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15225;
	}

	public void method_7221(class_3850 arg) {
		class_3850 lv = this.method_7231();
		if (lv.method_16924() != arg.method_16924()) {
			this.field_7448 = null;
		}

		this.field_6011.method_12778(field_7445, arg);
	}

	@Override
	public class_3850 method_7231() {
		return this.field_6011.method_12789(field_7445);
	}

	public boolean method_7241() {
		return this.field_7457;
	}

	public void method_7226(boolean bl) {
		this.field_7457 = bl;
	}

	public void method_7220(boolean bl) {
		this.field_7455 = bl;
	}

	public boolean method_7236() {
		return this.field_7455;
	}

	@Nullable
	@Override
	public class_1415 method_7232() {
		return this.field_7451;
	}

	@Override
	public void method_6015(@Nullable class_1309 arg) {
		super.method_6015(arg);
		if (this.field_7451 != null && arg != null) {
			this.field_7451.method_6404(arg);
			if (arg instanceof class_1657) {
				int i = -1;
				if (this.method_6109()) {
					i = -3;
				}

				this.field_7451.method_6393(((class_1657)arg).method_7334().getName(), i);
				if (this.method_5805()) {
					this.field_6002.method_8421(this, (byte)13);
				}
			}
		}
	}

	@Override
	public void method_6078(class_1282 arg) {
		if (this.field_7451 != null) {
			class_1297 lv = arg.method_5529();
			if (lv != null) {
				if (lv instanceof class_1657) {
					this.field_7451.method_6393(((class_1657)lv).method_7334().getName(), -2);
				} else if (lv instanceof class_1569) {
					this.field_7451.method_6398();
				}
			} else {
				class_1657 lv2 = this.field_6002.method_8614(this, 16.0);
				if (lv2 != null) {
					this.field_7451.method_6398();
				}
			}
		}

		super.method_6078(arg);
	}

	@Override
	public void method_8259(@Nullable class_1657 arg) {
		this.field_7461 = arg;
	}

	@Nullable
	@Override
	public class_1657 method_8257() {
		return this.field_7461;
	}

	public boolean method_7235() {
		return this.field_7461 != null;
	}

	public boolean method_7245(boolean bl) {
		if (!this.field_7452 && bl && this.method_7224()) {
			boolean bl2 = false;

			for (int i = 0; i < this.field_7458.method_5439(); i++) {
				class_1799 lv = this.field_7458.method_5438(i);
				if (!lv.method_7960()) {
					if (lv.method_7909() == class_1802.field_8229 && lv.method_7947() >= 3) {
						bl2 = true;
						this.field_7458.method_5434(i, 3);
					} else if ((lv.method_7909() == class_1802.field_8567 || lv.method_7909() == class_1802.field_8179) && lv.method_7947() >= 12) {
						bl2 = true;
						this.field_7458.method_5434(i, 12);
					}
				}

				if (bl2) {
					this.field_6002.method_8421(this, (byte)18);
					this.field_7452 = true;
					break;
				}
			}
		}

		return this.field_7452;
	}

	public void method_7243(boolean bl) {
		this.field_7452 = bl;
	}

	@Override
	public void method_8262(class_1914 arg) {
		arg.method_8244();
		this.field_6191 = -this.method_5970();
		this.method_5783(class_3417.field_14815, this.method_6107(), this.method_6017());
		int i = 3 + this.field_5974.nextInt(4);
		if (arg.method_8249() == 1 || this.field_5974.nextInt(5) == 0) {
			this.field_7444 = 40;
			this.field_7453 = true;
			this.field_7452 = true;
			this.field_7454 = this.field_7461 == null ? null : this.field_7461.method_7334().getName();
			i += 5;
		}

		if (arg.method_8256()) {
			this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010 + 0.5, this.field_6035, i));
		}

		if (this.field_7461 instanceof class_3222) {
			class_174.field_1206.method_9146((class_3222)this.field_7461, this, arg.method_8250());
		}
	}

	@Override
	public void method_8258(class_1799 arg) {
		if (!this.field_6002.field_9236 && this.field_6191 > -this.method_5970() + 20) {
			this.field_6191 = -this.method_5970();
			this.method_5783(arg.method_7960() ? class_3417.field_15008 : class_3417.field_14815, this.method_6107(), this.method_6017());
		}
	}

	@Override
	public class_1916 method_8264() {
		if (this.field_7448 == null) {
			this.field_7448 = new class_1916();
			this.method_7237();
		}

		return this.field_7448;
	}

	public void method_16917(class_1916 arg) {
		this.field_7448 = arg;
	}

	private void method_16918() {
		this.method_7221(this.method_7231().method_16920(this.method_7231().method_16925() + 1));
		this.method_7237();
	}

	private void method_7237() {
		class_3850 lv = this.method_7231();
		Int2ObjectMap<class_3853.class_1652[]> int2ObjectMap = (Int2ObjectMap<class_3853.class_1652[]>)class_3853.field_17067.get(lv.method_16924());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			class_3853.class_1652[] lvs = int2ObjectMap.get(lv.method_16925());
			if (lvs != null) {
				class_1916 lv2 = this.method_8264();

				for (class_3853.class_1652 lv3 : lvs) {
					class_1914 lv4 = lv3.method_7246(this, this.field_5974);
					if (lv4 != null) {
						lv2.add(lv4);
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable class_1916 arg) {
	}

	@Override
	public class_1937 method_8260() {
		return this.field_6002;
	}

	@Override
	public class_2338 method_8263() {
		return new class_2338(this);
	}

	@Override
	public class_2561 method_5476() {
		class_270 lv = this.method_5781();
		class_2561 lv2 = this.method_5797();
		if (lv2 != null) {
			return class_268.method_1142(lv, lv2).method_10859(arg -> arg.method_10949(this.method_5769()).method_10975(this.method_5845()));
		} else {
			class_3852 lv3 = this.method_7231().method_16924();
			class_2561 lv4 = new class_2588(this.method_5864().method_5882() + '.' + class_2378.field_17167.method_10221(lv3).method_12832())
				.method_10859(arg -> arg.method_10949(this.method_5769()).method_10975(this.method_5845()));
			if (lv != null) {
				lv4.method_10854(lv.method_1202());
			}

			return lv4;
		}
	}

	@Override
	public float method_5751() {
		return this.method_6109() ? 0.81F : 1.62F;
	}

	@Nullable
	@Override
	public class_3765 method_16461() {
		return this.field_7451 != null ? this.field_7451.method_16469() : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 12) {
			this.method_7233(class_2398.field_11201);
		} else if (b == 13) {
			this.method_7233(class_2398.field_11231);
		} else if (b == 14) {
			this.method_7233(class_2398.field_11211);
		} else if (b == 42) {
			this.method_7233(class_2398.field_11202);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_7233(class_2394 arg) {
		for (int i = 0; i < 5; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					arg,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					this.field_6010 + 1.0 + (double)(this.field_5974.nextFloat() * this.field_6019),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.field_5998 * 2.0F) - (double)this.field_5998,
					d,
					e,
					f
				);
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_7230();
		if (arg3 == class_3730.field_16466) {
			this.method_7221(this.method_7231().method_16921(class_3852.field_17051));
		}

		if (arg3 == class_3730.field_16462 || arg3 == class_3730.field_16465 || arg3 == class_3730.field_16469) {
			this.method_7221(this.method_7231().method_16922(class_3854.method_16930(arg.method_8310(new class_2338(this)))));
		}

		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	public void method_7238() {
		this.field_7449 = true;
	}

	public class_1646 method_7225(class_1296 arg) {
		double d = this.field_5974.nextDouble();
		class_3854 lv;
		if (d < 0.5) {
			lv = class_3854.method_16930(this.field_6002.method_8310(new class_2338(this)));
		} else if (d < 0.75) {
			lv = this.method_7231().method_16919();
		} else {
			lv = ((class_1646)arg).method_7231().method_16919();
		}

		class_1646 lv2 = new class_1646(this.field_6002, lv);
		lv2.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv2)), class_3730.field_16466, null, null);
		return lv2;
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return false;
	}

	@Override
	public void method_5800(class_1538 arg) {
		if (!this.field_6002.field_9236 && !this.field_5988) {
			class_1640 lv = new class_1640(this.field_6002);
			lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
			lv.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv)), class_3730.field_16468, null, null);
			lv.method_5977(this.method_5987());
			if (this.method_16914()) {
				lv.method_5665(this.method_5797());
				lv.method_5880(this.method_5807());
			}

			this.field_6002.method_8649(lv);
			this.method_5650();
		}
	}

	public class_1277 method_7242() {
		return this.field_7458;
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		class_1792 lv2 = lv.method_7909();
		if (this.method_7227(lv2)) {
			class_1799 lv3 = this.field_7458.method_5491(lv);
			if (lv3.method_7960()) {
				arg.method_5650();
			} else {
				lv.method_7939(lv3.method_7947());
			}
		}
	}

	private boolean method_7227(class_1792 arg) {
		return arg == class_1802.field_8229
			|| arg == class_1802.field_8567
			|| arg == class_1802.field_8179
			|| arg == class_1802.field_8861
			|| arg == class_1802.field_8317
			|| arg == class_1802.field_8186
			|| arg == class_1802.field_8309;
	}

	public boolean method_7224() {
		return this.method_7228(1);
	}

	public boolean method_7234() {
		return this.method_7228(2);
	}

	public boolean method_7239() {
		boolean bl = this.method_7231().method_16924() == class_3852.field_17056;
		return bl ? !this.method_7228(5) : !this.method_7228(1);
	}

	private boolean method_7228(int i) {
		boolean bl = this.method_7231().method_16924() == class_3852.field_17056;

		for (int j = 0; j < this.field_7458.method_5439(); j++) {
			class_1799 lv = this.field_7458.method_5438(j);
			class_1792 lv2 = lv.method_7909();
			int k = lv.method_7947();
			if (lv2 == class_1802.field_8229 && k >= 3 * i
				|| lv2 == class_1802.field_8567 && k >= 12 * i
				|| lv2 == class_1802.field_8179 && k >= 12 * i
				|| lv2 == class_1802.field_8186 && k >= 12 * i) {
				return true;
			}

			if (bl && lv2 == class_1802.field_8861 && k >= 9 * i) {
				return true;
			}
		}

		return false;
	}

	public boolean method_7244() {
		for (int i = 0; i < this.field_7458.method_5439(); i++) {
			class_1792 lv = this.field_7458.method_5438(i).method_7909();
			if (lv == class_1802.field_8317 || lv == class_1802.field_8567 || lv == class_1802.field_8179 || lv == class_1802.field_8309) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (super.method_5758(i, arg)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.field_7458.method_5439()) {
				this.field_7458.method_5447(j, arg);
				return true;
			} else {
				return false;
			}
		}
	}
}
