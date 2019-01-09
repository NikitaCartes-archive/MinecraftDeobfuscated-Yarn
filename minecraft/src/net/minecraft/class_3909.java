package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3909<T extends class_1309> extends class_1400<T> {
	private int field_17282 = 0;

	public class_3909(class_3763 arg, Class<T> class_, boolean bl, @Nullable Predicate<? super T> predicate) {
		super(arg, class_, 500, bl, false, predicate);
	}

	public int method_17352() {
		return this.field_17282;
	}

	public void method_17353() {
		this.field_17282--;
	}

	@Override
	public boolean method_6264() {
		if (this.field_17282 > 0 || !this.field_6660.method_6051().nextBoolean()) {
			return false;
		} else if (((class_3763)this.field_6660).method_16482() && this.field_6643.isAssignableFrom(class_3763.class)) {
			List<T> list = this.field_6660.field_6002.method_8390(this.field_6643, this.method_6321(this.method_6326()), this.field_6642);
			if (list.isEmpty()) {
				return false;
			} else {
				Collections.sort(list, this.field_6645);
				this.field_6644 = (T)list.get(0);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void method_6269() {
		this.field_17282 = 200;
		super.method_6269();
	}
}
