package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3760<T extends class_1309> extends class_1400<T> {
	private boolean field_17281 = true;

	public class_3760(class_3763 arg, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<class_1309> predicate) {
		super(arg, class_, i, bl, bl2, predicate);
	}

	public void method_17351(boolean bl) {
		this.field_17281 = bl;
	}

	@Override
	public boolean method_6264() {
		return this.field_17281 && super.method_6264();
	}
}
