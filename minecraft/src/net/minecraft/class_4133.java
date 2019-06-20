package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;

public class class_4133 extends class_4097<class_1646> {
	private int field_18404;
	private boolean field_18403;

	public class_4133() {
		super(ImmutableMap.of(class_4140.field_18439, class_4141.field_18456, class_4140.field_18446, class_4141.field_18458));
	}

	protected boolean method_19037(class_3218 arg, class_1646 arg2) {
		return this.method_19036(arg.method_8532() % 24000L, arg2.method_19186());
	}

	protected void method_19614(class_3218 arg, class_1646 arg2, long l) {
		this.field_18403 = false;
		this.field_18404 = 0;
		arg2.method_18868().method_18875(class_4140.field_18446);
	}

	protected void method_19039(class_3218 arg, class_1646 arg2, long l) {
		class_4095<class_1646> lv = arg2.method_18868();
		lv.method_18878(class_4140.field_19386, class_4316.method_20791(l));
		if (!this.field_18403) {
			arg2.method_19182();
			this.field_18403 = true;
			arg2.method_19183();
			lv.method_18904(class_4140.field_18439).ifPresent(arg2x -> lv.method_18878(class_4140.field_18446, new class_4099(arg2x.method_19446())));
		}

		this.field_18404++;
	}

	protected boolean method_19040(class_3218 arg, class_1646 arg2, long l) {
		Optional<class_4208> optional = arg2.method_18868().method_18904(class_4140.field_18439);
		if (!optional.isPresent()) {
			return false;
		} else {
			class_4208 lv = (class_4208)optional.get();
			return this.field_18404 < 100
				&& Objects.equals(lv.method_19442(), arg.method_8597().method_12460())
				&& lv.method_19446().method_19769(arg2.method_19538(), 1.73);
		}
	}

	private boolean method_19036(long l, long m) {
		return m == 0L || l < m || l > m + 3500L;
	}
}
