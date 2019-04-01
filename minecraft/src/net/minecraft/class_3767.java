package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
	}

	public static boolean method_16838(class_3763 arg, class_3765 arg2) {
		return arg != null && arg2 != null && arg2.method_16831() != null
			? arg.method_5805()
				&& arg.method_16481()
				&& arg.method_6131() <= 2400
				&& arg.field_6002.method_8597().method_12460() == arg2.method_16831().method_8597().method_12460()
			: false;
	}

	public static boolean method_16537(class_1309 arg, class_2338 arg2, int i) {
		return arg2.method_10262(new class_2338(arg.field_5987, arg.field_6010, arg.field_6035)) < (double)(i * i + 24);
	}

	@Nullable
	public class_3765 method_16540(class_3222 arg) {
		if (arg.method_7325()) {
			return null;
		} else {
			class_2338 lv = new class_2338(arg);
			Optional<class_2338> optional = this.field_16641
				.method_19494()
				.method_20006(argx -> argx == class_4158.field_18518, Objects::nonNull, lv, 15, class_4153.class_4155.field_18489);
			if (!optional.isPresent()) {
				optional = Optional.of(lv);
			}

			class_3765 lv2 = this.method_16532(arg.method_14220(), (class_2338)optional.get());
			boolean bl = false;
			if (!lv2.method_16524()) {
				if (!this.field_16639.containsKey(lv2.method_16494())) {
					this.field_16639.put(lv2.method_16494(), lv2);
				}

				bl = true;
			} else if (lv2.method_16493() < lv2.method_16514()) {
				bl = true;
			}

			if (bl) {
				lv2.method_16518(arg);
				arg.field_13987.method_14364(new class_2663(arg, (byte)43));
			}

			this.method_80();
			return lv2;
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
	public class_3765 method_19209(class_2338 arg) {
		class_3765 lv = null;
		double d = 2.147483647E9;

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
