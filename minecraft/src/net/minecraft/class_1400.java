package net.minecraft;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1400<T extends class_1309> extends class_1405 {
	protected final Class<T> field_6643;
	private final int field_6641;
	protected final class_1400.class_1401 field_6645;
	protected final Predicate<? super T> field_6642;
	protected T field_6644;

	public class_1400(class_1314 arg, Class<T> class_, boolean bl) {
		this(arg, class_, bl, false);
	}

	public class_1400(class_1314 arg, Class<T> class_, boolean bl, boolean bl2) {
		this(arg, class_, 10, bl, bl2, null);
	}

	public class_1400(class_1314 arg, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<? super T> predicate) {
		super(arg, bl, bl2);
		this.field_6643 = class_;
		this.field_6641 = i;
		this.field_6645 = new class_1400.class_1401(arg);
		this.method_6265(1);
		this.field_6642 = argx -> {
			if (argx == null) {
				return false;
			} else if (predicate != null && !predicate.test(argx)) {
				return false;
			} else {
				return !class_1301.field_6155.test(argx) ? false : this.method_6328(argx, false);
			}
		};
	}

	@Override
	public boolean method_6264() {
		if (this.field_6641 > 0 && this.field_6660.method_6051().nextInt(this.field_6641) != 0) {
			return false;
		} else if (this.field_6643 != class_1657.class && this.field_6643 != class_3222.class) {
			List<T> list = this.field_6660.field_6002.method_8390(this.field_6643, this.method_6321(this.method_6326()), this.field_6642);
			if (list.isEmpty()) {
				return false;
			} else {
				Collections.sort(list, this.field_6645);
				this.field_6644 = (T)list.get(0);
				return true;
			}
		} else {
			this.field_6644 = (T)this.field_6660
				.field_6002
				.method_8439(
					this.field_6660.field_5987,
					this.field_6660.field_6010 + (double)this.field_6660.method_5751(),
					this.field_6660.field_6035,
					this.method_6326(),
					this.method_6326(),
					new Function<class_1657, Double>() {
						@Nullable
						public Double method_6323(@Nullable class_1657 arg) {
							class_1799 lv = arg.method_6118(class_1304.field_6169);
							return (!(class_1400.this.field_6660 instanceof class_1613) || lv.method_7909() != class_1802.field_8398)
									&& (!(class_1400.this.field_6660 instanceof class_1642) || lv.method_7909() != class_1802.field_8470)
									&& (!(class_1400.this.field_6660 instanceof class_1548) || lv.method_7909() != class_1802.field_8681)
								? 1.0
								: 0.5;
						}
					},
					this.field_6642
				);
			return this.field_6644 != null;
		}
	}

	protected class_238 method_6321(double d) {
		return this.field_6660.method_5829().method_1009(d, 4.0, d);
	}

	@Override
	public void method_6269() {
		this.field_6660.method_5980(this.field_6644);
		super.method_6269();
	}

	public static class class_1401 implements Comparator<class_1297> {
		private final class_1297 field_6646;

		public class_1401(class_1297 arg) {
			this.field_6646 = arg;
		}

		public int method_6322(class_1297 arg, class_1297 arg2) {
			double d = this.field_6646.method_5858(arg);
			double e = this.field_6646.method_5858(arg2);
			if (d < e) {
				return -1;
			} else {
				return d > e ? 1 : 0;
			}
		}
	}
}
