package net.minecraft;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1338<T extends class_1297> extends class_1352 {
	private final Predicate<class_1297> field_6389 = new Predicate<class_1297>() {
		public boolean method_6248(@Nullable class_1297 arg) {
			return arg.method_5805() && class_1338.this.field_6391.method_5985().method_6369(arg) && !class_1338.this.field_6391.method_5722(arg);
		}
	};
	protected final class_1314 field_6391;
	private final double field_6385;
	private final double field_6395;
	protected T field_6390;
	private final float field_6386;
	private class_11 field_6387;
	private final class_1408 field_6394;
	private final Class<T> field_6392;
	private final Predicate<? super class_1297> field_6393;
	private final Predicate<? super class_1297> field_6388;

	public class_1338(class_1314 arg, Class<T> class_, float f, double d, double e) {
		this(arg, class_, argx -> true, f, d, e, class_1301.field_6156);
	}

	public class_1338(class_1314 arg, Class<T> class_, Predicate<? super class_1297> predicate, float f, double d, double e, Predicate<class_1297> predicate2) {
		this.field_6391 = arg;
		this.field_6392 = class_;
		this.field_6393 = predicate;
		this.field_6386 = f;
		this.field_6385 = d;
		this.field_6395 = e;
		this.field_6388 = predicate2;
		this.field_6394 = arg.method_5942();
		this.method_6265(1);
	}

	public class_1338(class_1314 arg, Class<T> class_, float f, double d, double e, Predicate<class_1297> predicate) {
		this(arg, class_, argx -> true, f, d, e, predicate);
	}

	@Override
	public boolean method_6264() {
		List<T> list = this.field_6391
			.field_6002
			.method_8390(
				this.field_6392,
				this.field_6391.method_5829().method_1009((double)this.field_6386, 3.0, (double)this.field_6386),
				arg -> this.field_6388.test(arg) && this.field_6389.test(arg) && this.field_6393.test(arg)
			);
		if (list.isEmpty()) {
			return false;
		} else {
			this.field_6390 = (T)list.get(0);
			class_243 lv = class_1414.method_6379(
				this.field_6391, 16, 7, new class_243(this.field_6390.field_5987, this.field_6390.field_6010, this.field_6390.field_6035)
			);
			if (lv == null) {
				return false;
			} else if (this.field_6390.method_5649(lv.field_1352, lv.field_1351, lv.field_1350) < this.field_6390.method_5858(this.field_6391)) {
				return false;
			} else {
				this.field_6387 = this.field_6394.method_6352(lv.field_1352, lv.field_1351, lv.field_1350);
				return this.field_6387 != null;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6394.method_6357();
	}

	@Override
	public void method_6269() {
		this.field_6394.method_6334(this.field_6387, this.field_6385);
	}

	@Override
	public void method_6270() {
		this.field_6390 = null;
	}

	@Override
	public void method_6268() {
		if (this.field_6391.method_5858(this.field_6390) < 49.0) {
			this.field_6391.method_5942().method_6344(this.field_6395);
		} else {
			this.field_6391.method_5942().method_6344(this.field_6385);
		}
	}
}
