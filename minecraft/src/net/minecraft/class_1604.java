package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1604 extends class_1543 implements class_3745, class_1603 {
	private static final class_2940<Boolean> field_7334 = class_2945.method_12791(class_1604.class, class_2943.field_13323);
	private final class_1277 field_7335 = new class_1277(5);

	public class_1604(class_1937 arg) {
		super(class_1299.field_6105, arg);
		this.method_5835(0.6F, 1.95F);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(3, new class_1383<>(this, 1.0, 8.0F));
		this.field_6201.method_6277(8, new class_1379(this, 0.6));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 15.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 15.0F));
		this.field_6185.method_6277(1, new class_1399(this, class_1543.class).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_1646.class, false));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.35F);
		this.method_5996(class_1612.field_7365).method_6192(20.0);
		this.method_5996(class_1612.field_7359).method_6192(24.0);
		this.method_5996(class_1612.field_7363).method_6192(5.0);
		this.method_5996(class_1612.field_7365).method_6192(32.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7334, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7108() {
		return this.field_6011.method_12789(field_7334);
	}

	@Override
	public void method_7110(boolean bl) {
		this.field_6011.method_12778(field_7334, bl);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7109() {
		return this.method_6991(1);
	}

	@Override
	public void method_7106(boolean bl) {
		this.method_6992(1, bl);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_2499 lv = new class_2499();

		for (int i = 0; i < this.field_7335.method_5439(); i++) {
			class_1799 lv2 = this.field_7335.method_5438(i);
			if (!lv2.method_7960()) {
				lv.method_10606(lv2.method_7953(new class_2487()));
			}
		}

		arg.method_10566("Inventory", lv);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1543.class_1544 method_6990() {
		if (this.method_7109()) {
			return class_1543.class_1544.field_7211;
		} else if (this.method_7108()) {
			return class_1543.class_1544.field_7210;
		} else {
			return !this.method_6047().method_7960() && this.method_6047().method_7909() == class_1802.field_8399
				? class_1543.class_1544.field_7213
				: class_1543.class_1544.field_7207;
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_2499 lv = arg.method_10554("Inventory", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = class_1799.method_7915(lv.method_10602(i));
			if (!lv2.method_7960()) {
				this.field_7335.method_5491(lv2);
			}
		}

		this.method_5952(true);
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		class_2248 lv = arg2.method_8320(arg.method_10074()).method_11614();
		return lv != class_2246.field_10219 && lv != class_2246.field_10102 ? arg2.method_8610(arg) - 0.5F : 10.0F;
	}

	@Override
	protected boolean method_7075() {
		return true;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return arg.method_8314(class_1944.field_9282, new class_2338(this.field_5987, this.field_6010, this.field_6035)) > 8 ? false : super.method_5979(arg, arg2);
	}

	@Override
	public int method_5945() {
		return 1;
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
		class_1799 lv = new class_1799(class_1802.field_8399);
		if (this.field_5974.nextInt(300) == 0) {
			Map<class_1887, Integer> map = Maps.<class_1887, Integer>newHashMap();
			map.put(class_1893.field_9132, 1);
			class_1890.method_8214(map, lv);
		}

		this.method_5673(class_1304.field_6173, lv);
	}

	@Override
	public boolean method_5722(class_1297 arg) {
		if (super.method_5722(arg)) {
			return true;
		} else {
			return arg instanceof class_1309 && ((class_1309)arg).method_6046() == class_1310.field_6291
				? this.method_5781() == null && arg.method_5781() == null
				: false;
		}
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14976;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15049;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15159;
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		class_1665 lv = this.method_7107(f);
		double d = arg.field_5987 - this.field_5987;
		double e = arg.method_5829().field_1322 + (double)(arg.field_6019 / 3.0F) - lv.field_6010;
		double g = arg.field_6035 - this.field_6035;
		double h = (double)class_3532.method_15368(d * d + g * g);
		lv.method_7485(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4));
		this.method_5783(class_3417.field_15187, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
		this.field_6002.method_8649(lv);
		this.field_6278 = 0;
		class_1764.method_7782(this.method_6047(), false);
	}

	protected class_1665 method_7107(float f) {
		class_1667 lv = new class_1667(this.field_6002, this);
		lv.method_7442(true);
		lv.method_7444(class_3417.field_14636);
		lv.method_7435(this, f);
		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_6999() {
		return false;
	}

	public class_1277 method_16473() {
		return this.field_7335;
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		if (lv.method_7909() instanceof class_1746) {
			super.method_5949(arg);
		} else {
			class_1792 lv2 = lv.method_7909();
			if (this.method_7111(lv2)) {
				class_1799 lv3 = this.field_7335.method_5491(lv);
				if (lv3.method_7960()) {
					arg.method_5650();
				} else {
					lv.method_7939(lv3.method_7947());
				}
			}
		}
	}

	private boolean method_7111(class_1792 arg) {
		return this.method_16482() ? arg == class_1802.field_8539 : false;
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (super.method_5758(i, arg)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.field_7335.method_5439()) {
				this.field_7335.method_5447(j, arg);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void method_16484(int i, boolean bl) {
		if (i > 3) {
			int j = 5;
			if (this.field_5974.nextInt(Math.max(6 - i, 1)) == 0) {
				class_1799 lv = new class_1799(class_1802.field_8399);
				Map<class_1887, Integer> map = Maps.<class_1887, Integer>newHashMap();
				if (i > 6) {
					map.put(class_1893.field_9098, 2);
				}

				map.put(class_1893.field_9108, 1);
				class_1890.method_8214(map, lv);
				this.method_5673(class_1304.field_6173, lv);
			}
		}
	}

	@Override
	public boolean method_17326() {
		return super.method_17326() && this.method_16473().method_5442();
	}

	@Override
	public boolean method_5974(double d) {
		return super.method_5974(d) && this.method_16473().method_5442();
	}
}
