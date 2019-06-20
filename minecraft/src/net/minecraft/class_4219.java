package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;

public class class_4219 extends class_4097<class_1314> {
	private final class_4140<class_4208> field_18862;
	private final int field_18863;
	private final int field_18864;
	private long field_18865;

	public class_4219(class_4140<class_4208> arg, int i, int j) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18458, arg, class_4141.field_18456));
		this.field_18862 = arg;
		this.field_18863 = i;
		this.field_18864 = j;
	}

	protected boolean method_19607(class_3218 arg, class_1314 arg2) {
		Optional<class_4208> optional = arg2.method_18868().method_18904(this.field_18862);
		return optional.isPresent()
			&& Objects.equals(arg.method_8597().method_12460(), ((class_4208)optional.get()).method_19442())
			&& ((class_4208)optional.get()).method_19446().method_19769(arg2.method_19538(), (double)this.field_18864);
	}

	protected void method_19608(class_3218 arg, class_1314 arg2, long l) {
		if (l > this.field_18865) {
			class_4095<?> lv = arg2.method_18868();
			Optional<class_4208> optional = lv.method_18904(this.field_18862);
			optional.ifPresent(arg2x -> lv.method_18878(class_4140.field_18445, new class_4142(arg2x.method_19446(), 0.4F, this.field_18863)));
			this.field_18865 = l + 80L;
		}
	}
}
