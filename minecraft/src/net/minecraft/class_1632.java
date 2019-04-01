package net.minecraft;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1632 extends class_1543 {
	private static final Predicate<class_1267> field_19014 = arg -> arg == class_1267.field_5802 || arg == class_1267.field_5807;
	private boolean field_7406;

	public class_1632(class_1299<? extends class_1632> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1632.class_3761(this));
		this.field_6201.method_6277(3, new class_3763.class_4223(this, 10.0F));
		this.field_6201.method_6277(4, new class_1366(this, 1.0, false));
		this.field_6185.method_6277(1, new class_1399(this, class_3763.class).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_3988.class, true));
		this.field_6185.method_6277(3, new class_1400(this, class_1439.class, true));
		this.field_6185.method_6277(4, new class_1632.class_1633(this));
		this.field_6201.method_6277(8, new class_1379(this, 0.6));
		this.field_6201.method_6277(9, new class_1361(this, class_1657.class, 3.0F, 1.0F));
		this.field_6201.method_6277(10, new class_1361(this, class_1308.class, 8.0F));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.35F);
		this.method_5996(class_1612.field_7365).method_6192(12.0);
		this.method_5996(class_1612.field_7359).method_6192(24.0);
		this.method_5996(class_1612.field_7363).method_6192(5.0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_7406) {
			arg.method_10556("Johnny", true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1543.class_1544 method_6990() {
		if (this.method_6510()) {
			return class_1543.class_1544.field_7211;
		} else {
			return this.method_20034() ? class_1543.class_1544.field_19012 : class_1543.class_1544.field_7207;
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("Johnny", 99)) {
			this.field_7406 = arg.method_10577("Johnny");
		}
	}

	@Override
	public class_3414 method_20033() {
		return class_3417.field_19151;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		class_1315 lv = super.method_5943(arg, arg2, arg3, arg4, arg5);
		((class_1409)this.method_5942()).method_6363(true);
		this.method_5964(arg2);
		this.method_5984(arg2);
		return lv;
	}

	@Override
	protected void method_5964(class_1266 arg) {
		if (this.method_16478() == null) {
			this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8475));
		}
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
	public void method_5665(@Nullable class_2561 arg) {
		super.method_5665(arg);
		if (!this.field_7406 && arg != null && arg.getString().equals("Johnny")) {
			this.field_7406 = true;
		}
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14735;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14642;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14558;
	}

	@Override
	public void method_16484(int i, boolean bl) {
		class_3765 lv = this.method_16478();
		int j = 1;
		class_1799 lv2;
		if (i <= lv.method_20016(class_1267.field_5805)) {
			lv2 = new class_1799(class_1802.field_8406);
		} else if (i <= lv.method_20016(class_1267.field_5802)) {
			lv2 = new class_1799(class_1802.field_8062);
		} else {
			lv2 = new class_1799(class_1802.field_8475);
			j = 2;
		}

		boolean bl2 = this.field_5974.nextFloat() <= lv.method_20025();
		if (bl2) {
			Map<class_1887, Integer> map = Maps.<class_1887, Integer>newHashMap();
			map.put(class_1893.field_9118, j);
			class_1890.method_8214(map, lv2);
		}

		this.method_5673(class_1304.field_6173, lv2);
	}

	static class class_1633 extends class_1400<class_1309> {
		public class_1633(class_1632 arg) {
			super(arg, class_1309.class, 0, true, true, class_1309::method_6102);
		}

		@Override
		public boolean method_6264() {
			return ((class_1632)this.field_6660).field_7406 && super.method_6264();
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_6660.method_16826(0);
		}
	}

	static class class_3761 extends class_1339 {
		public class_3761(class_1308 arg) {
			super(arg, 6, class_1632.field_19014);
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			class_1632 lv = (class_1632)this.field_6413;
			class_1267 lv2 = this.field_6413.field_6002.method_8407();
			boolean bl = lv2 == class_1267.field_5802 || lv2 == class_1267.field_5807;
			return bl && lv.method_16482() && super.method_6264();
		}

		@Override
		public void method_6269() {
			super.method_6269();
			this.field_6413.method_16826(0);
		}
	}
}
