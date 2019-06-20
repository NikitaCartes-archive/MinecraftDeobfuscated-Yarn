package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class class_4307 extends class_4148<class_1309> {
	public class_4307() {
		this(200);
	}

	public class_4307(int i) {
		super(i);
	}

	@Override
	protected void method_19101(class_3218 arg, class_1309 arg2) {
		method_20656(arg.method_8510(), arg2);
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18441);
	}

	public static void method_20656(long l, class_1309 arg) {
		class_4095<?> lv = arg.method_18868();
		Optional<List<class_1309>> optional = lv.method_18904(class_4140.field_18441);
		if (optional.isPresent()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(argx -> argx.method_5864().equals(class_1299.field_6147));
			if (bl) {
				lv.method_18878(class_4140.field_19355, l);
			}
		}
	}
}
