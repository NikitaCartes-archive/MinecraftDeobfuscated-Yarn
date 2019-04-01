package net.minecraft;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1400<T extends class_1309> extends class_1405 {
	protected final Class<T> field_6643;
	protected final int field_6641;
	protected class_1309 field_6644;
	protected class_4051 field_6642;

	public class_1400(class_1308 arg, Class<T> class_, boolean bl) {
		this(arg, class_, bl, false);
	}

	public class_1400(class_1308 arg, Class<T> class_, boolean bl, boolean bl2) {
		this(arg, class_, 10, bl, bl2, null);
	}

	public class_1400(class_1308 arg, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<class_1309> predicate) {
		super(arg, bl, bl2);
		this.field_6643 = class_;
		this.field_6641 = i;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
		this.field_6642 = new class_4051().method_18418(this.method_6326()).method_18420(predicate);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6641 > 0 && this.field_6660.method_6051().nextInt(this.field_6641) != 0) {
			return false;
		} else {
			this.method_18415();
			return this.field_6644 != null;
		}
	}

	protected class_238 method_6321(double d) {
		return this.field_6660.method_5829().method_1009(d, 4.0, d);
	}

	protected void method_18415() {
		if (this.field_6643 != class_1657.class && this.field_6643 != class_3222.class) {
			this.field_6644 = this.field_6660
				.field_6002
				.method_18465(
					this.field_6643,
					this.field_6642,
					this.field_6660,
					this.field_6660.field_5987,
					this.field_6660.field_6010 + (double)this.field_6660.method_5751(),
					this.field_6660.field_6035,
					this.method_6321(this.method_6326())
				);
		} else {
			this.field_6644 = this.field_6660
				.field_6002
				.method_18463(
					this.field_6642,
					this.field_6660,
					this.field_6660.field_5987,
					this.field_6660.field_6010 + (double)this.field_6660.method_5751(),
					this.field_6660.field_6035
				);
		}
	}

	@Override
	public void method_6269() {
		this.field_6660.method_5980(this.field_6644);
		super.method_6269();
	}
}
