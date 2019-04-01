package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1604 extends class_1543 implements class_3745, class_1603 {
	private static final class_2940<Boolean> field_7334 = class_2945.method_12791(class_1604.class, class_2943.field_13323);
	private final class_1277 field_7335 = new class_1277(5);

	public class_1604(class_1299<? extends class_1604> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(2, new class_3763.class_4223(this, 10.0F));
		this.field_6201.method_6277(3, new class_1383<>(this, 1.0, 8.0F));
		this.field_6201.method_6277(8, new class_1379(this, 0.6));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 15.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 15.0F));
		this.field_6185.method_6277(1, new class_1399(this, class_1543.class).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_3988.class, false));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.35F);
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

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_2499 lv = new class_2499();

		for (int i = 0; i < this.field_7335.method_5439(); i++) {
			class_1799 lv2 = this.field_7335.method_5438(i);
			if (!lv2.method_7960()) {
				lv.add(lv2.method_7953(new class_2487()));
			}
		}

		arg.method_10566("Inventory", lv);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1543.class_1544 method_6990() {
		if (this.method_7108()) {
			return class_1543.class_1544.field_7210;
		} else if (this.method_18809(class_1802.field_8399)) {
			return class_1543.class_1544.field_7213;
		} else {
			return this.method_6510() ? class_1543.class_1544.field_7211 : class_1543.class_1544.field_7207;
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
		return lv != class_2246.field_10219 && lv != class_2246.field_10102 ? 0.5F - arg2.method_8610(arg) : 10.0F;
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
		class_1799 lv = this.method_5998(class_1675.method_18812(this, class_1802.field_8399));
		if (this.method_18809(class_1802.field_8399)) {
			class_1764.method_7777(this.field_6002, this, lv, 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4));
		}

		this.field_6278 = 0;
	}

	@Override
	public void method_18811(class_1309 arg, class_1799 arg2, class_1676 arg3, float f) {
		class_1297 lv = (class_1297)arg3;
		double d = arg.field_5987 - this.field_5987;
		double e = arg.field_6035 - this.field_6035;
		double g = (double)class_3532.method_15368(d * d + e * e);
		double h = arg.method_5829().field_1322 + (double)(arg.method_17682() / 3.0F) - lv.field_6010 + g * 0.2F;
		class_1160 lv2 = this.method_19168(new class_243(d, h, e), f);
		arg3.method_7485(
			(double)lv2.method_4943(), (double)lv2.method_4945(), (double)lv2.method_4947(), 1.6F, (float)(14 - this.field_6002.method_8407().method_5461() * 4)
		);
		this.method_5783(class_3417.field_15187, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
	}

	private class_1160 method_19168(class_243 arg, float f) {
		class_243 lv = arg.method_1029();
		class_243 lv2 = lv.method_1036(new class_243(0.0, 1.0, 0.0));
		if (lv2.method_1027() <= 1.0E-7) {
			lv2 = lv.method_1036(this.method_18864(1.0F));
		}

		class_1158 lv3 = new class_1158(new class_1160(lv2), 90.0F, true);
		class_1160 lv4 = new class_1160(lv);
		lv4.method_19262(lv3);
		class_1158 lv5 = new class_1158(lv4, f, true);
		class_1160 lv6 = new class_1160(lv);
		lv6.method_19262(lv5);
		return lv6;
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
		return this.method_16482() && arg == class_1802.field_8539;
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
		class_3765 lv = this.method_16478();
		boolean bl2 = this.field_5974.nextFloat() <= lv.method_20025();
		if (bl2) {
			class_1799 lv2 = new class_1799(class_1802.field_8399);
			Map<class_1887, Integer> map = Maps.<class_1887, Integer>newHashMap();
			if (i > lv.method_20016(class_1267.field_5802)) {
				map.put(class_1893.field_9098, 2);
			} else if (i > lv.method_20016(class_1267.field_5805)) {
				map.put(class_1893.field_9098, 1);
			}

			map.put(class_1893.field_9108, 1);
			class_1890.method_8214(map, lv2);
			this.method_5673(class_1304.field_6173, lv2);
		}
	}

	@Override
	public boolean method_17326() {
		return super.method_17326() && this.method_16473().method_5442();
	}

	@Override
	public class_3414 method_20033() {
		return class_3417.field_19150;
	}

	@Override
	public boolean method_5974(double d) {
		return super.method_5974(d) && this.method_16473().method_5442();
	}
}
