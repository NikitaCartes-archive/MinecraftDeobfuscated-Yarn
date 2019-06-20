package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class class_4220 extends class_4097<class_1646> {
	private final class_4140<List<class_4208>> field_18866;
	private final class_4140<class_4208> field_18867;
	private final float field_18868;
	private final int field_18869;
	private final int field_18870;
	private long field_18871;
	@Nullable
	private class_4208 field_18872;

	public class_4220(class_4140<List<class_4208>> arg, float f, int i, int j, class_4140<class_4208> arg2) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18458, arg, class_4141.field_18456, arg2, class_4141.field_18456));
		this.field_18866 = arg;
		this.field_18868 = f;
		this.field_18869 = i;
		this.field_18870 = j;
		this.field_18867 = arg2;
	}

	protected boolean method_19609(class_3218 arg, class_1646 arg2) {
		Optional<List<class_4208>> optional = arg2.method_18868().method_18904(this.field_18866);
		Optional<class_4208> optional2 = arg2.method_18868().method_18904(this.field_18867);
		if (optional.isPresent() && optional2.isPresent()) {
			List<class_4208> list = (List<class_4208>)optional.get();
			if (!list.isEmpty()) {
				this.field_18872 = (class_4208)list.get(arg.method_8409().nextInt(list.size()));
				return this.field_18872 != null
					&& Objects.equals(arg.method_8597().method_12460(), this.field_18872.method_19442())
					&& ((class_4208)optional2.get()).method_19446().method_19769(arg2.method_19538(), (double)this.field_18870);
			}
		}

		return false;
	}

	protected void method_19610(class_3218 arg, class_1646 arg2, long l) {
		if (l > this.field_18871 && this.field_18872 != null) {
			arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(this.field_18872.method_19446(), this.field_18868, this.field_18869));
			this.field_18871 = l + 100L;
		}
	}
}
