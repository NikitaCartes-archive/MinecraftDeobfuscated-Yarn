package net.minecraft;

import java.util.EnumSet;
import java.util.function.Predicate;

public class class_1338<T extends class_1309> extends class_1352 {
	protected final class_1314 field_6391;
	private final double field_6385;
	private final double field_6395;
	protected T field_6390;
	protected final float field_6386;
	protected class_11 field_6387;
	protected final class_1408 field_6394;
	protected final Class<T> field_6392;
	protected final Predicate<class_1309> field_6393;
	protected final Predicate<class_1309> field_6388;
	private final class_4051 field_18084;

	public class_1338(class_1314 arg, Class<T> class_, float f, double d, double e) {
		this(arg, class_, argx -> true, f, d, e, class_1301.field_6156::test);
	}

	public class_1338(class_1314 arg, Class<T> class_, Predicate<class_1309> predicate, float f, double d, double e, Predicate<class_1309> predicate2) {
		this.field_6391 = arg;
		this.field_6392 = class_;
		this.field_6393 = predicate;
		this.field_6386 = f;
		this.field_6385 = d;
		this.field_6395 = e;
		this.field_6388 = predicate2;
		this.field_6394 = arg.method_5942();
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		this.field_18084 = new class_4051().method_18418((double)f).method_18420(predicate2.and(predicate));
	}

	public class_1338(class_1314 arg, Class<T> class_, float f, double d, double e, Predicate<class_1309> predicate) {
		this(arg, class_, argx -> true, f, d, e, predicate);
	}

	@Override
	public boolean method_6264() {
		this.field_6390 = this.field_6391
			.field_6002
			.method_18465(
				this.field_6392,
				this.field_18084,
				this.field_6391,
				this.field_6391.field_5987,
				this.field_6391.field_6010,
				this.field_6391.field_6035,
				this.field_6391.method_5829().method_1009((double)this.field_6386, 3.0, (double)this.field_6386)
			);
		if (this.field_6390 == null) {
			return false;
		} else {
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
