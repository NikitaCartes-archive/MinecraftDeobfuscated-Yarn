package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1531 extends class_1309 {
	private static final class_2379 field_7113 = new class_2379(0.0F, 0.0F, 0.0F);
	private static final class_2379 field_7119 = new class_2379(0.0F, 0.0F, 0.0F);
	private static final class_2379 field_7124 = new class_2379(-10.0F, 0.0F, -10.0F);
	private static final class_2379 field_7115 = new class_2379(-15.0F, 0.0F, 10.0F);
	private static final class_2379 field_7121 = new class_2379(-1.0F, 0.0F, -1.0F);
	private static final class_2379 field_7117 = new class_2379(1.0F, 0.0F, 1.0F);
	public static final class_2940<Byte> field_7107 = class_2945.method_12791(class_1531.class, class_2943.field_13319);
	public static final class_2940<class_2379> field_7123 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	public static final class_2940<class_2379> field_7122 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	public static final class_2940<class_2379> field_7116 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	public static final class_2940<class_2379> field_7105 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	public static final class_2940<class_2379> field_7127 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	public static final class_2940<class_2379> field_7125 = class_2945.method_12791(class_1531.class, class_2943.field_13316);
	private static final Predicate<class_1297> field_7102 = arg -> arg instanceof class_1688
			&& ((class_1688)arg).method_7518() == class_1688.class_1689.field_7674;
	private final class_2371<class_1799> field_7114 = class_2371.method_10213(2, class_1799.field_8037);
	private final class_2371<class_1799> field_7108 = class_2371.method_10213(4, class_1799.field_8037);
	private boolean field_7111;
	public long field_7112;
	private int field_7118;
	private boolean field_7109;
	private class_2379 field_7104 = field_7113;
	private class_2379 field_7106 = field_7119;
	private class_2379 field_7126 = field_7124;
	private class_2379 field_7120 = field_7115;
	private class_2379 field_7110 = field_7121;
	private class_2379 field_7103 = field_7117;

	public class_1531(class_1937 arg) {
		super(class_1299.field_6131, arg);
		this.field_5960 = this.method_5740();
		this.method_5835(0.5F, 1.975F);
		this.field_6013 = 0.0F;
	}

	public class_1531(class_1937 arg, double d, double e, double f) {
		this(arg);
		this.method_5814(d, e, f);
	}

	@Override
	protected final void method_5835(float f, float g) {
		double d = this.field_5987;
		double e = this.field_6010;
		double h = this.field_6035;
		float i = this.method_6912() ? 0.0F : (this.method_6109() ? 0.5F : 1.0F);
		super.method_5835(f * i, g * i);
		this.method_5814(d, e, h);
	}

	@Override
	public boolean method_6034() {
		return super.method_6034() && !this.method_5740();
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7107, (byte)0);
		this.field_6011.method_12784(field_7123, field_7113);
		this.field_6011.method_12784(field_7122, field_7119);
		this.field_6011.method_12784(field_7116, field_7124);
		this.field_6011.method_12784(field_7105, field_7115);
		this.field_6011.method_12784(field_7127, field_7121);
		this.field_6011.method_12784(field_7125, field_7117);
	}

	@Override
	public Iterable<class_1799> method_5877() {
		return this.field_7114;
	}

	@Override
	public Iterable<class_1799> method_5661() {
		return this.field_7108;
	}

	@Override
	public class_1799 method_6118(class_1304 arg) {
		switch (arg.method_5925()) {
			case field_6177:
				return this.field_7114.get(arg.method_5927());
			case field_6178:
				return this.field_7108.get(arg.method_5927());
			default:
				return class_1799.field_8037;
		}
	}

	@Override
	public void method_5673(class_1304 arg, class_1799 arg2) {
		switch (arg.method_5925()) {
			case field_6177:
				this.method_6116(arg2);
				this.field_7114.set(arg.method_5927(), arg2);
				break;
			case field_6178:
				this.method_6116(arg2);
				this.field_7108.set(arg.method_5927(), arg2);
		}
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		class_1304 lv;
		if (i == 98) {
			lv = class_1304.field_6173;
		} else if (i == 99) {
			lv = class_1304.field_6171;
		} else if (i == 100 + class_1304.field_6169.method_5927()) {
			lv = class_1304.field_6169;
		} else if (i == 100 + class_1304.field_6174.method_5927()) {
			lv = class_1304.field_6174;
		} else if (i == 100 + class_1304.field_6172.method_5927()) {
			lv = class_1304.field_6172;
		} else {
			if (i != 100 + class_1304.field_6166.method_5927()) {
				return false;
			}

			lv = class_1304.field_6166;
		}

		if (!arg.method_7960() && !class_1308.method_5935(lv, arg) && lv != class_1304.field_6169) {
			return false;
		} else {
			this.method_5673(lv, arg);
			return true;
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_2499 lv = new class_2499();

		for (class_1799 lv2 : this.field_7108) {
			class_2487 lv3 = new class_2487();
			if (!lv2.method_7960()) {
				lv2.method_7953(lv3);
			}

			lv.method_10606(lv3);
		}

		arg.method_10566("ArmorItems", lv);
		class_2499 lv4 = new class_2499();

		for (class_1799 lv5 : this.field_7114) {
			class_2487 lv6 = new class_2487();
			if (!lv5.method_7960()) {
				lv5.method_7953(lv6);
			}

			lv4.method_10606(lv6);
		}

		arg.method_10566("HandItems", lv4);
		arg.method_10556("Invisible", this.method_5767());
		arg.method_10556("Small", this.method_6914());
		arg.method_10556("ShowArms", this.method_6929());
		arg.method_10569("DisabledSlots", this.field_7118);
		arg.method_10556("NoBasePlate", this.method_6901());
		if (this.method_6912()) {
			arg.method_10556("Marker", this.method_6912());
		}

		arg.method_10566("Pose", this.method_6911());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("ArmorItems", 9)) {
			class_2499 lv = arg.method_10554("ArmorItems", 10);

			for (int i = 0; i < this.field_7108.size(); i++) {
				this.field_7108.set(i, class_1799.method_7915(lv.method_10602(i)));
			}
		}

		if (arg.method_10573("HandItems", 9)) {
			class_2499 lv = arg.method_10554("HandItems", 10);

			for (int i = 0; i < this.field_7114.size(); i++) {
				this.field_7114.set(i, class_1799.method_7915(lv.method_10602(i)));
			}
		}

		this.method_5648(arg.method_10577("Invisible"));
		this.method_6922(arg.method_10577("Small"));
		this.method_6913(arg.method_10577("ShowArms"));
		this.field_7118 = arg.method_10550("DisabledSlots");
		this.method_6907(arg.method_10577("NoBasePlate"));
		this.method_6902(arg.method_10577("Marker"));
		this.field_7109 = !this.method_6912();
		this.field_5960 = this.method_5740();
		class_2487 lv2 = arg.method_10562("Pose");
		this.method_6928(lv2);
	}

	private void method_6928(class_2487 arg) {
		class_2499 lv = arg.method_10554("Head", 5);
		this.method_6919(lv.isEmpty() ? field_7113 : new class_2379(lv));
		class_2499 lv2 = arg.method_10554("Body", 5);
		this.method_6927(lv2.isEmpty() ? field_7119 : new class_2379(lv2));
		class_2499 lv3 = arg.method_10554("LeftArm", 5);
		this.method_6910(lv3.isEmpty() ? field_7124 : new class_2379(lv3));
		class_2499 lv4 = arg.method_10554("RightArm", 5);
		this.method_6925(lv4.isEmpty() ? field_7115 : new class_2379(lv4));
		class_2499 lv5 = arg.method_10554("LeftLeg", 5);
		this.method_6909(lv5.isEmpty() ? field_7121 : new class_2379(lv5));
		class_2499 lv6 = arg.method_10554("RightLeg", 5);
		this.method_6926(lv6.isEmpty() ? field_7117 : new class_2379(lv6));
	}

	private class_2487 method_6911() {
		class_2487 lv = new class_2487();
		if (!field_7113.equals(this.field_7104)) {
			lv.method_10566("Head", this.field_7104.method_10255());
		}

		if (!field_7119.equals(this.field_7106)) {
			lv.method_10566("Body", this.field_7106.method_10255());
		}

		if (!field_7124.equals(this.field_7126)) {
			lv.method_10566("LeftArm", this.field_7126.method_10255());
		}

		if (!field_7115.equals(this.field_7120)) {
			lv.method_10566("RightArm", this.field_7120.method_10255());
		}

		if (!field_7121.equals(this.field_7110)) {
			lv.method_10566("LeftLeg", this.field_7110.method_10255());
		}

		if (!field_7117.equals(this.field_7103)) {
			lv.method_10566("RightLeg", this.field_7103.method_10255());
		}

		return lv;
	}

	@Override
	public boolean method_5810() {
		return false;
	}

	@Override
	protected void method_6087(class_1297 arg) {
	}

	@Override
	protected void method_6070() {
		List<class_1297> list = this.field_6002.method_8333(this, this.method_5829(), field_7102);

		for (int i = 0; i < list.size(); i++) {
			class_1297 lv = (class_1297)list.get(i);
			if (this.method_5858(lv) <= 0.2) {
				lv.method_5697(this);
			}
		}
	}

	@Override
	public class_1269 method_5664(class_1657 arg, class_243 arg2, class_1268 arg3) {
		class_1799 lv = arg.method_5998(arg3);
		if (this.method_6912() || lv.method_7909() == class_1802.field_8448) {
			return class_1269.field_5811;
		} else if (!this.field_6002.field_9236 && !arg.method_7325()) {
			class_1304 lv2 = class_1308.method_5953(lv);
			if (lv.method_7960()) {
				class_1304 lv3 = this.method_6916(arg2);
				class_1304 lv4 = this.method_6915(lv3) ? lv2 : lv3;
				if (this.method_6084(lv4)) {
					this.method_6904(arg, lv4, lv, arg3);
				}
			} else {
				if (this.method_6915(lv2)) {
					return class_1269.field_5814;
				}

				if (lv2.method_5925() == class_1304.class_1305.field_6177 && !this.method_6929()) {
					return class_1269.field_5814;
				}

				this.method_6904(arg, lv2, lv, arg3);
			}

			return class_1269.field_5812;
		} else {
			return class_1269.field_5812;
		}
	}

	protected class_1304 method_6916(class_243 arg) {
		class_1304 lv = class_1304.field_6173;
		boolean bl = this.method_6914();
		double d = bl ? arg.field_1351 * 2.0 : arg.field_1351;
		class_1304 lv2 = class_1304.field_6166;
		if (d >= 0.1 && d < 0.1 + (bl ? 0.8 : 0.45) && this.method_6084(lv2)) {
			lv = class_1304.field_6166;
		} else if (d >= 0.9 + (bl ? 0.3 : 0.0) && d < 0.9 + (bl ? 1.0 : 0.7) && this.method_6084(class_1304.field_6174)) {
			lv = class_1304.field_6174;
		} else if (d >= 0.4 && d < 0.4 + (bl ? 1.0 : 0.8) && this.method_6084(class_1304.field_6172)) {
			lv = class_1304.field_6172;
		} else if (d >= 1.6 && this.method_6084(class_1304.field_6169)) {
			lv = class_1304.field_6169;
		} else if (!this.method_6084(class_1304.field_6173) && this.method_6084(class_1304.field_6171)) {
			lv = class_1304.field_6171;
		}

		return lv;
	}

	public boolean method_6915(class_1304 arg) {
		return (this.field_7118 & 1 << arg.method_5926()) != 0 || arg.method_5925() == class_1304.class_1305.field_6177 && !this.method_6929();
	}

	private void method_6904(class_1657 arg, class_1304 arg2, class_1799 arg3, class_1268 arg4) {
		class_1799 lv = this.method_6118(arg2);
		if (lv.method_7960() || (this.field_7118 & 1 << arg2.method_5926() + 8) == 0) {
			if (!lv.method_7960() || (this.field_7118 & 1 << arg2.method_5926() + 16) == 0) {
				if (arg.field_7503.field_7477 && lv.method_7960() && !arg3.method_7960()) {
					class_1799 lv2 = arg3.method_7972();
					lv2.method_7939(1);
					this.method_5673(arg2, lv2);
				} else if (arg3.method_7960() || arg3.method_7947() <= 1) {
					this.method_5673(arg2, arg3);
					arg.method_6122(arg4, lv);
				} else if (lv.method_7960()) {
					class_1799 lv2 = arg3.method_7972();
					lv2.method_7939(1);
					this.method_5673(arg2, lv2);
					arg3.method_7934(1);
				}
			}
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.field_6002.field_9236 || this.field_5988) {
			return false;
		} else if (class_1282.field_5849.equals(arg)) {
			this.method_5650();
			return false;
		} else if (this.method_5679(arg) || this.field_7111 || this.method_6912()) {
			return false;
		} else if (arg.method_5535()) {
			this.method_6908(arg);
			this.method_5650();
			return false;
		} else if (class_1282.field_5867.equals(arg)) {
			if (this.method_5809()) {
				this.method_6905(arg, 0.15F);
			} else {
				this.method_5639(5);
			}

			return false;
		} else if (class_1282.field_5854.equals(arg) && this.method_6032() > 0.5F) {
			this.method_6905(arg, 4.0F);
			return false;
		} else {
			boolean bl = arg.method_5526() instanceof class_1665;
			boolean bl2 = "player".equals(arg.method_5525());
			if (!bl2 && !bl) {
				return false;
			} else if (arg.method_5529() instanceof class_1657 && !((class_1657)arg.method_5529()).field_7503.field_7476) {
				return false;
			} else if (arg.method_5530()) {
				this.method_6920();
				this.method_6898();
				this.method_5650();
				return false;
			} else {
				long l = this.field_6002.method_8510();
				if (l - this.field_7112 > 5L && !bl) {
					this.field_6002.method_8421(this, (byte)32);
					this.field_7112 = l;
				} else {
					this.method_6924(arg);
					this.method_6898();
					this.method_5650();
				}

				return true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 32) {
			if (this.field_6002.field_9236) {
				this.field_6002.method_8486(this.field_5987, this.field_6010, this.field_6035, class_3417.field_14897, this.method_5634(), 0.3F, 1.0F, false);
				this.field_7112 = this.field_6002.method_8510();
			}
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995() * 4.0;
		if (Double.isNaN(e) || e == 0.0) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	private void method_6898() {
		if (this.field_6002 instanceof class_3218) {
			((class_3218)this.field_6002)
				.method_14199(
					new class_2388(class_2398.field_11217, class_2246.field_10161.method_9564()),
					this.field_5987,
					this.field_6010 + (double)this.field_6019 / 1.5,
					this.field_6035,
					10,
					(double)(this.field_5998 / 4.0F),
					(double)(this.field_6019 / 4.0F),
					(double)(this.field_5998 / 4.0F),
					0.05
				);
		}
	}

	private void method_6905(class_1282 arg, float f) {
		float g = this.method_6032();
		g -= f;
		if (g <= 0.5F) {
			this.method_6908(arg);
			this.method_5650();
		} else {
			this.method_6033(g);
		}
	}

	private void method_6924(class_1282 arg) {
		class_2248.method_9577(this.field_6002, new class_2338(this), new class_1799(class_1802.field_8694));
		this.method_6908(arg);
	}

	private void method_6908(class_1282 arg) {
		this.method_6920();
		this.method_16080(arg);

		for (int i = 0; i < this.field_7114.size(); i++) {
			class_1799 lv = this.field_7114.get(i);
			if (!lv.method_7960()) {
				class_2248.method_9577(this.field_6002, new class_2338(this).method_10084(), lv);
				this.field_7114.set(i, class_1799.field_8037);
			}
		}

		for (int ix = 0; ix < this.field_7108.size(); ix++) {
			class_1799 lv = this.field_7108.get(ix);
			if (!lv.method_7960()) {
				class_2248.method_9577(this.field_6002, new class_2338(this).method_10084(), lv);
				this.field_7108.set(ix, class_1799.field_8037);
			}
		}
	}

	private void method_6920() {
		this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_15118, this.method_5634(), 1.0F, 1.0F);
	}

	@Override
	protected float method_6031(float f, float g) {
		this.field_6220 = this.field_5982;
		this.field_6283 = this.field_6031;
		return 0.0F;
	}

	@Override
	public float method_5751() {
		return this.method_6109() ? this.field_6019 * 0.5F : this.field_6019 * 0.9F;
	}

	@Override
	public double method_5678() {
		return this.method_6912() ? 0.0 : 0.1F;
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (!this.method_5740()) {
			super.method_6091(f, g, h);
		}
	}

	@Override
	public void method_5636(float f) {
		this.field_6220 = this.field_5982 = f;
		this.field_6259 = this.field_6241 = f;
	}

	@Override
	public void method_5847(float f) {
		this.field_6220 = this.field_5982 = f;
		this.field_6259 = this.field_6241 = f;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		class_2379 lv = this.field_6011.method_12789(field_7123);
		if (!this.field_7104.equals(lv)) {
			this.method_6919(lv);
		}

		class_2379 lv2 = this.field_6011.method_12789(field_7122);
		if (!this.field_7106.equals(lv2)) {
			this.method_6927(lv2);
		}

		class_2379 lv3 = this.field_6011.method_12789(field_7116);
		if (!this.field_7126.equals(lv3)) {
			this.method_6910(lv3);
		}

		class_2379 lv4 = this.field_6011.method_12789(field_7105);
		if (!this.field_7120.equals(lv4)) {
			this.method_6925(lv4);
		}

		class_2379 lv5 = this.field_6011.method_12789(field_7127);
		if (!this.field_7110.equals(lv5)) {
			this.method_6909(lv5);
		}

		class_2379 lv6 = this.field_6011.method_12789(field_7125);
		if (!this.field_7103.equals(lv6)) {
			this.method_6926(lv6);
		}

		boolean bl = this.method_6912();
		if (this.field_7109 != bl) {
			this.method_6899(bl);
			this.field_6033 = !bl;
			this.field_7109 = bl;
		}
	}

	private void method_6899(boolean bl) {
		if (bl) {
			this.method_5835(0.0F, 0.0F);
		} else {
			this.method_5835(0.5F, 1.975F);
		}
	}

	@Override
	protected void method_6027() {
		this.method_5648(this.field_7111);
	}

	@Override
	public void method_5648(boolean bl) {
		this.field_7111 = bl;
		super.method_5648(bl);
	}

	@Override
	public boolean method_6109() {
		return this.method_6914();
	}

	@Override
	public void method_5768() {
		this.method_5650();
	}

	@Override
	public boolean method_5659() {
		return this.method_5767();
	}

	@Override
	public class_3619 method_5657() {
		return this.method_6912() ? class_3619.field_15975 : super.method_5657();
	}

	private void method_6922(boolean bl) {
		this.field_6011.method_12778(field_7107, this.method_6906(this.field_6011.method_12789(field_7107), 1, bl));
		this.method_5835(0.5F, 1.975F);
	}

	public boolean method_6914() {
		return (this.field_6011.method_12789(field_7107) & 1) != 0;
	}

	private void method_6913(boolean bl) {
		this.field_6011.method_12778(field_7107, this.method_6906(this.field_6011.method_12789(field_7107), 4, bl));
	}

	public boolean method_6929() {
		return (this.field_6011.method_12789(field_7107) & 4) != 0;
	}

	private void method_6907(boolean bl) {
		this.field_6011.method_12778(field_7107, this.method_6906(this.field_6011.method_12789(field_7107), 8, bl));
	}

	public boolean method_6901() {
		return (this.field_6011.method_12789(field_7107) & 8) != 0;
	}

	private void method_6902(boolean bl) {
		this.field_6011.method_12778(field_7107, this.method_6906(this.field_6011.method_12789(field_7107), 16, bl));
		this.method_5835(0.5F, 1.975F);
	}

	public boolean method_6912() {
		return (this.field_6011.method_12789(field_7107) & 16) != 0;
	}

	private byte method_6906(byte b, int i, boolean bl) {
		if (bl) {
			b = (byte)(b | i);
		} else {
			b = (byte)(b & ~i);
		}

		return b;
	}

	public void method_6919(class_2379 arg) {
		this.field_7104 = arg;
		this.field_6011.method_12778(field_7123, arg);
	}

	public void method_6927(class_2379 arg) {
		this.field_7106 = arg;
		this.field_6011.method_12778(field_7122, arg);
	}

	public void method_6910(class_2379 arg) {
		this.field_7126 = arg;
		this.field_6011.method_12778(field_7116, arg);
	}

	public void method_6925(class_2379 arg) {
		this.field_7120 = arg;
		this.field_6011.method_12778(field_7105, arg);
	}

	public void method_6909(class_2379 arg) {
		this.field_7110 = arg;
		this.field_6011.method_12778(field_7127, arg);
	}

	public void method_6926(class_2379 arg) {
		this.field_7103 = arg;
		this.field_6011.method_12778(field_7125, arg);
	}

	public class_2379 method_6921() {
		return this.field_7104;
	}

	public class_2379 method_6923() {
		return this.field_7106;
	}

	@Environment(EnvType.CLIENT)
	public class_2379 method_6930() {
		return this.field_7126;
	}

	@Environment(EnvType.CLIENT)
	public class_2379 method_6903() {
		return this.field_7120;
	}

	@Environment(EnvType.CLIENT)
	public class_2379 method_6917() {
		return this.field_7110;
	}

	@Environment(EnvType.CLIENT)
	public class_2379 method_6900() {
		return this.field_7103;
	}

	@Override
	public boolean method_5863() {
		return super.method_5863() && !this.method_6912();
	}

	@Override
	public class_1306 method_6068() {
		return class_1306.field_6183;
	}

	@Override
	protected class_3414 method_6041(int i) {
		return class_3417.field_15186;
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14897;
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15118;
	}

	@Override
	public void method_5800(class_1538 arg) {
	}

	@Override
	public boolean method_6086() {
		return false;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7107.equals(arg)) {
			this.method_5835(0.5F, 1.975F);
		}

		super.method_5674(arg);
	}

	@Override
	public boolean method_6102() {
		return false;
	}
}
