package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class class_4256 extends class_4148<class_1308> {
	public class_4256() {
		super(100);
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_19007);
	}

	public void method_19998(class_3218 arg, class_1308 arg2) {
		this.field_18463 = arg.method_8510();
		arg2.method_18868().method_18879(class_4140.field_19007, this.method_19999(arg, arg2));
	}

	private Optional<class_2338> method_19999(class_3218 arg, class_1308 arg2) {
		class_4153 lv = arg.method_19494();
		Predicate<class_2338> predicate = arg2x -> {
			if (arg2x.equals(new class_2338(arg2))) {
				return true;
			} else {
				class_11 lvx = arg2.method_5942().method_6348(arg2x);
				return lvx != null && lvx.method_19315();
			}
		};
		return lv.method_20006(class_4158.field_18517.method_19164(), predicate, new class_2338(arg2), 16, class_4153.class_4155.field_18489);
	}
}
