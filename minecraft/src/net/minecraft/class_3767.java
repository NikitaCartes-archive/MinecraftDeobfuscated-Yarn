package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class class_3767 extends class_18 {
	private final Map<Integer, class_3765> field_16639 = Maps.<Integer, class_3765>newHashMap();
	private final Map<class_1657, Integer> field_16640 = Maps.<class_1657, Integer>newHashMap();
	private class_1937 field_16641;
	private int field_16638;
	private int field_16637;

	public class_3767(String string) {
		super(string);
		this.field_16638 = 1;
	}

	public class_3767(class_1937 arg) {
		super(method_16533(arg.field_9247));
		this.field_16641 = arg;
		this.field_16638 = 1;
		this.method_80();
	}

	public class_3765 method_16541(int i) {
		return (class_3765)this.field_16639.get(i);
	}

	public void method_16530(class_1937 arg) {
		this.field_16641 = arg;

		for (class_3765 lv : this.field_16639.values()) {
			lv.method_16489(arg);
		}
	}

	public void method_16539() {
		this.field_16637++;
		Iterator<Entry<Integer, class_3765>> iterator = this.field_16639.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Integer, class_3765> entry = (Entry<Integer, class_3765>)iterator.next();
			if (((class_3765)entry.getValue()).method_16511() != null && !((class_3765)entry.getValue()).method_16503()) {
				((class_3765)entry.getValue()).method_16509();
			} else {
				class_1415 lv = ((class_3765)entry.getValue()).method_16511();
				if (lv != null) {
					((class_3765)entry.getValue()).method_16512(null);
				}

				iterator.remove();
				this.method_80();
			}
		}

		if (this.field_16637 % 200 == 0) {
			this.method_80();
		}
	}

	public void method_16531(class_1415 arg) {
		Iterator<Entry<class_1657, Integer>> iterator = this.field_16640.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<class_1657, Integer> entry = (Entry<class_1657, Integer>)iterator.next();
			class_1657 lv = (class_1657)entry.getKey();
			int i = (Integer)entry.getValue();
			if (!this.method_16839() && method_16537(arg, lv)) {
				this.method_16540(arg, lv);
			}

			if (this.field_16637 > i + 120000) {
				iterator.remove();
				this.method_80();
			}
		}
	}

	public static boolean method_16838(class_3763 arg, class_3765 arg2) {
		return arg != null && arg2 != null && arg2.method_16831() != null
			? arg.method_5805() && arg.method_6131() <= 2400 && arg.field_6002.method_8597().method_12460() == arg2.method_16831().method_8597().method_12460()
			: false;
	}

	private boolean method_16839() {
		return this.field_16641 != null && this.field_16641.method_8407() == class_1267.field_5801;
	}

	public static boolean method_16537(class_1415 arg, class_1309 arg2) {
		return arg.method_16470(new class_2338(arg2.field_5987, arg2.field_6010, arg2.field_6035), 24);
	}

	public void method_16538(class_1657 arg) {
		this.field_16640.put(arg, this.field_16637);
	}

	public void method_16536(class_1657 arg) {
		this.field_16640.remove(arg);
	}

	@Nullable
	public class_3765 method_16540(class_1415 arg, class_1657 arg2) {
		class_3765 lv = this.method_16532(arg2.field_6002, arg);
		if (!lv.method_16524() && this.method_16535(arg, arg2)) {
			if (!this.field_16639.containsKey(lv.method_16494())) {
				this.field_16639.put(lv.method_16494(), lv);
			}

			lv.method_16518(arg2);
			((class_3222)arg2).field_13987.method_14364(new class_2663(arg2, (byte)43));
		} else if (lv.method_16493() < lv.method_16514()) {
			lv.method_16518(arg2);
			((class_3222)arg2).field_13987.method_14364(new class_2663(arg2, (byte)43));
		}

		this.method_16536(arg2);
		this.method_80();
		return lv;
	}

	private boolean method_16535(class_1415 arg, class_1657 arg2) {
		return !arg2.method_7325() && !arg.method_6397();
	}

	@Nullable
	public class_3765 method_16532(class_1937 arg, class_1415 arg2) {
		class_3765 lv = this.method_16541(arg2.method_16467());
		return lv != null ? lv : new class_3765(this.method_16534(), arg, arg2);
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_16638 = arg.method_10550("NextAvailableID");
		this.field_16637 = arg.method_10550("Tick");
		class_2499 lv = arg.method_10554("Raids", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			class_3765 lv3 = new class_3765();
			lv3.method_16497(lv2);
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
			lv.method_10606(lv3);
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
}
