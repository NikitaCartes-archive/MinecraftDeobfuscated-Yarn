package net.minecraft;

import java.util.List;
import java.util.Optional;

public class class_4102 implements class_4115 {
	private final class_1309 field_18342;

	public class_4102(class_1309 arg) {
		this.field_18342 = arg;
	}

	@Override
	public class_2338 method_18989() {
		return new class_2338(this.field_18342);
	}

	@Override
	public class_243 method_18991() {
		return new class_243(this.field_18342.field_5987, this.field_18342.field_6010 + (double)this.field_18342.method_5751(), this.field_18342.field_6035);
	}

	@Override
	public boolean method_18990(class_1309 arg) {
		Optional<List<class_1309>> optional = arg.method_18868().method_18904(class_4140.field_18442);
		return this.field_18342.method_5805() && optional.isPresent() && ((List)optional.get()).contains(this.field_18342);
	}

	public String toString() {
		return "EntityPosWrapper for " + this.field_18342;
	}
}
