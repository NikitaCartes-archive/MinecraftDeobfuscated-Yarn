package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_1404<T extends class_1309> extends class_1400<T> {
	private final class_1321 field_6656;

	public class_1404(class_1321 arg, Class<T> class_, boolean bl, @Nullable Predicate<? super T> predicate) {
		super(arg, class_, 10, bl, false, predicate);
		this.field_6656 = arg;
	}

	@Override
	public boolean method_6264() {
		return !this.field_6656.method_6181() && super.method_6264();
	}

	@Override
	public boolean method_6266() {
		return this.field_6642 != null ? this.field_6642.test(this.field_6644) : super.method_6266();
	}
}
