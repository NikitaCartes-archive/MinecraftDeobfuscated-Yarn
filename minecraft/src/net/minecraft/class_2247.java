package net.minecraft;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2247 implements Predicate<class_2694> {
	private final class_2680 field_10632;
	private final Set<class_2769<?>> field_10631;
	@Nullable
	private final class_2487 field_10633;

	public class_2247(class_2680 arg, Set<class_2769<?>> set, @Nullable class_2487 arg2) {
		this.field_10632 = arg;
		this.field_10631 = set;
		this.field_10633 = arg2;
	}

	public class_2680 method_9494() {
		return this.field_10632;
	}

	public boolean method_9493(class_2694 arg) {
		class_2680 lv = arg.method_11681();
		if (lv.method_11614() != this.field_10632.method_11614()) {
			return false;
		} else {
			for (class_2769<?> lv2 : this.field_10631) {
				if (lv.method_11654(lv2) != this.field_10632.method_11654(lv2)) {
					return false;
				}
			}

			if (this.field_10633 == null) {
				return true;
			} else {
				class_2586 lv3 = arg.method_11680();
				return lv3 != null && class_2512.method_10687(this.field_10633, lv3.method_11007(new class_2487()), true);
			}
		}
	}

	public boolean method_9495(class_3218 arg, class_2338 arg2, int i) {
		if (!arg.method_8652(arg2, this.field_10632, i)) {
			return false;
		} else {
			if (this.field_10633 != null) {
				class_2586 lv = arg.method_8321(arg2);
				if (lv != null) {
					class_2487 lv2 = this.field_10633.method_10553();
					lv2.method_10569("x", arg2.method_10263());
					lv2.method_10569("y", arg2.method_10264());
					lv2.method_10569("z", arg2.method_10260());
					lv.method_11014(lv2);
				}
			}

			return true;
		}
	}
}
