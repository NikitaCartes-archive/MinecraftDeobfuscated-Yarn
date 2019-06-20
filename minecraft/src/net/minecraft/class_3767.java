package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_3767 extends class_18 {
	private final Map<Integer, class_3765> field_16639 = Maps.<Integer, class_3765>newHashMap();
	private final class_3218 field_16641;
	private int field_16638;
	private int field_16637;

	public class_3767(class_3218 arg) {
		super(method_16533(arg.field_9247));
		this.field_16641 = arg;
		this.field_16638 = 1;
		this.method_80();
	}

	public class_3765 method_16541(int i) {
		return (class_3765)this.field_16639.get(i);
	}

	public void method_16539() {
		this.field_16637++;
		Iterator<class_3765> iterator = this.field_16639.values().iterator();

		while (iterator.hasNext()) {
			class_3765 lv = (class_3765)iterator.next();
			if (this.field_16641.method_8450().method_8355(class_1928.field_19422)) {
				lv.method_16506();
			}

			if (lv.method_20022()) {
				iterator.remove();
				this.method_80();
			} else {
				lv.method_16509();
			}
		}

		if (this.field_16637 % 200 == 0) {
			this.method_80();
		}

		class_4209.method_20575(this.field_16641, this.field_16639.values());
	}

	public static boolean method_16838(class_3763 arg, class_3765 arg2) {
		return arg != null && arg2 != null && arg2.method_16831() != null
			? arg.method_5805()
				&& arg.method_16481()
				&& arg.method_6131() <= 2400
				&& arg.field_6002.method_8597().method_12460() == arg2.method_16831().method_8597().method_12460()
			: false;
	}

	@Nullable
	public class_3765 method_16540(class_3222 arg) {
		if (arg.method_7325()) {
			return null;
		} else if (this.field_16641.method_8450().method_8355(class_1928.field_19422)) {
			return null;
		} else {
			class_2874 lv = arg.field_6002.method_8597().method_12460();
			if (lv == class_2874.field_13076) {
				return null;
			} else {
				class_2338 lv2 = new class_2338(arg);
				List<class_4156> list = (List<class_4156>)this.field_16641
					.method_19494()
					.method_19125(class_4158.field_18501, lv2, 64, class_4153.class_4155.field_18488)
					.collect(Collectors.toList());
				int i = 0;
				class_243 lv3 = new class_243(0.0, 0.0, 0.0);

				for (class_4156 lv4 : list) {
					class_2338 lv5 = lv4.method_19141();
					lv3 = lv3.method_1031((double)lv5.method_10263(), (double)lv5.method_10264(), (double)lv5.method_10260());
					i++;
				}

				class_2338 lv6;
				if (i > 0) {
					lv3 = lv3.method_1021(1.0 / (double)i);
					lv6 = new class_2338(lv3);
				} else {
					lv6 = lv2;
				}

				class_3765 lv7 = this.method_16532(arg.method_14220(), lv6);
				boolean bl = false;
				if (!lv7.method_16524()) {
					if (!this.field_16639.containsKey(lv7.method_16494())) {
						this.field_16639.put(lv7.method_16494(), lv7);
					}

					bl = true;
				} else if (lv7.method_16493() < lv7.method_16514()) {
					bl = true;
				} else {
					arg.method_6016(class_1294.field_16595);
					arg.field_13987.method_14364(new class_2663(arg, (byte)43));
				}

				if (bl) {
					lv7.method_16518(arg);
					arg.field_13987.method_14364(new class_2663(arg, (byte)43));
					if (!lv7.method_20021()) {
						arg.method_7281(class_3468.field_19256);
						class_174.field_19251.method_9027(arg);
					}
				}

				this.method_80();
				return lv7;
			}
		}
	}

	private class_3765 method_16532(class_3218 arg, class_2338 arg2) {
		class_3765 lv = arg.method_19502(arg2);
		return lv != null ? lv : new class_3765(this.method_16534(), arg, arg2);
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_16638 = arg.method_10550("NextAvailableID");
		this.field_16637 = arg.method_10550("Tick");
		class_2499 lv = arg.method_10554("Raids", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			class_3765 lv3 = new class_3765(this.field_16641, lv2);
			this.field_16639.put(lv3.method_16494(), lv3);
		}
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		arg.method_10569("NextAvailableID", this.field_16638);
		arg.method_10569("Tick", this.field_16637);
		class_2499 lv = new class_2499();

		for (class_3765 lv2 : this.field_16639.values()) {
			class_2487 lv3 = new class_2487();
			lv2.method_16502(lv3);
			lv.add(lv3);
		}

		arg.method_10566("Raids", lv);
		return arg;
	}

	public static String method_16533(class_2869 arg) {
		return "raids" + arg.method_12460().method_12489();
	}

	private int method_16534() {
		return ++this.field_16638;
	}

	@Nullable
	public class_3765 method_19209(class_2338 arg, int i) {
		class_3765 lv = null;
		double d = (double)i;

		for (class_3765 lv2 : this.field_16639.values()) {
			double e = lv2.method_16495().method_10262(arg);
			if (lv2.method_16504() && e < d) {
				lv = lv2;
				d = e;
			}
		}

		return lv;
	}
}
