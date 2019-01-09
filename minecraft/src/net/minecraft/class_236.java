package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_236<T> {
	private static final Logger field_1315 = LogManager.getLogger();
	private final class_233<T> field_1314;
	private final Queue<class_236.class_237<T>> field_1313 = new PriorityQueue(method_987());
	private UnsignedLong field_1311 = UnsignedLong.ZERO;
	private final Map<String, class_236.class_237<T>> field_1312 = Maps.<String, class_236.class_237<T>>newHashMap();

	private static <T> Comparator<class_236.class_237<T>> method_987() {
		return (arg, arg2) -> {
			int i = Long.compare(arg.field_1318, arg2.field_1318);
			return i != 0 ? i : arg.field_1319.compareTo(arg2.field_1319);
		};
	}

	public class_236(class_233<T> arg) {
		this.field_1314 = arg;
	}

	public void method_988(T object, long l) {
		while (true) {
			class_236.class_237<T> lv = (class_236.class_237<T>)this.field_1313.peek();
			if (lv == null || lv.field_1318 > l) {
				return;
			}

			this.field_1313.remove();
			this.field_1312.remove(lv.field_1317);
			lv.field_1316.method_974(object, this, l);
		}
	}

	private void method_985(String string, long l, class_234<T> arg) {
		this.field_1311 = this.field_1311.plus(UnsignedLong.ONE);
		class_236.class_237<T> lv = new class_236.class_237<>(l, this.field_1311, string, arg);
		this.field_1312.put(string, lv);
		this.field_1313.add(lv);
	}

	public boolean method_981(String string, long l, class_234<T> arg) {
		if (this.field_1312.containsKey(string)) {
			return false;
		} else {
			this.method_985(string, l, arg);
			return true;
		}
	}

	public void method_984(String string, long l, class_234<T> arg) {
		class_236.class_237<T> lv = (class_236.class_237<T>)this.field_1312.remove(string);
		if (lv != null) {
			this.field_1313.remove(lv);
		}

		this.method_985(string, l, arg);
	}

	private void method_986(class_2487 arg) {
		class_2487 lv = arg.method_10562("Callback");
		class_234<T> lv2 = this.field_1314.method_972(lv);
		if (lv2 != null) {
			String string = arg.method_10558("Name");
			long l = arg.method_10537("TriggerTime");
			this.method_981(string, l, lv2);
		}
	}

	public void method_979(class_2499 arg) {
		this.field_1313.clear();
		this.field_1312.clear();
		this.field_1311 = UnsignedLong.ZERO;
		if (!arg.isEmpty()) {
			if (arg.method_10601() != 10) {
				field_1315.warn("Invalid format of events: " + arg);
			} else {
				for (class_2520 lv : arg) {
					this.method_986((class_2487)lv);
				}
			}
		}
	}

	private class_2487 method_980(class_236.class_237<T> arg) {
		class_2487 lv = new class_2487();
		lv.method_10582("Name", arg.field_1317);
		lv.method_10544("TriggerTime", arg.field_1318);
		lv.method_10566("Callback", this.field_1314.method_973(arg.field_1316));
		return lv;
	}

	public class_2499 method_982() {
		class_2499 lv = new class_2499();
		this.field_1313.stream().sorted(method_987()).map(this::method_980).forEach(lv::method_10606);
		return lv;
	}

	public static class class_237<T> {
		public final long field_1318;
		public final UnsignedLong field_1319;
		public final String field_1317;
		public final class_234<T> field_1316;

		private class_237(long l, UnsignedLong unsignedLong, String string, class_234<T> arg) {
			this.field_1318 = l;
			this.field_1319 = unsignedLong;
			this.field_1317 = string;
			this.field_1316 = arg;
		}
	}
}
