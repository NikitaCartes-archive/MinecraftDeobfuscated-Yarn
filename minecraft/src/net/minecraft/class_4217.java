package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_4217 extends class_4097<class_1646> {
	@Nullable
	private class_2338 field_18858;
	private boolean field_18859;
	private boolean field_18860;
	private long field_18861;

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18446, class_4141.field_18457), Pair.of(class_4140.field_18873, class_4141.field_18456));
	}

	protected boolean method_19564(class_3218 arg, class_1646 arg2) {
		if (!arg.method_8450().method_8355("mobGriefing")) {
			return false;
		} else if (arg2.method_7231().method_16924() != class_3852.field_17056) {
			return false;
		} else {
			Set<class_2338> set = (Set<class_2338>)((List)arg2.method_18868().method_19543(class_4140.field_18873).get())
				.stream()
				.map(class_4208::method_19446)
				.collect(Collectors.toSet());
			class_2338 lv = new class_2338(arg2);
			List<class_2338> list = (List<class_2338>)ImmutableList.of(lv.method_10074(), lv.method_10072(), lv.method_10095(), lv.method_10078(), lv.method_10067())
				.stream()
				.filter(set::contains)
				.collect(Collectors.toList());
			if (!list.isEmpty()) {
				this.field_18858 = (class_2338)list.get(arg.method_8409().nextInt(list.size()));
				this.field_18859 = arg2.method_19623();
				this.field_18860 = arg2.method_7239();
				return true;
			} else {
				return false;
			}
		}
	}

	protected void method_19565(class_3218 arg, class_1646 arg2, long l) {
		if (l > this.field_18861 && this.field_18858 != null) {
			arg2.method_18868().method_18878(class_4140.field_18446, new class_4099(this.field_18858));
			class_2338 lv = this.field_18858.method_10084();
			class_2680 lv2 = arg.method_8320(lv);
			class_2248 lv3 = lv2.method_11614();
			if (lv3 instanceof class_2302 && ((class_2302)lv3).method_9825(lv2) && this.field_18860) {
				arg.method_8651(lv, true);
			} else if (lv2.method_11588() && this.field_18859) {
				class_1277 lv4 = arg2.method_18011();

				for (int i = 0; i < lv4.method_5439(); i++) {
					class_1799 lv5 = lv4.method_5438(i);
					boolean bl = false;
					if (!lv5.method_7960()) {
						if (lv5.method_7909() == class_1802.field_8317) {
							arg.method_8652(lv, class_2246.field_10293.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8567) {
							arg.method_8652(lv, class_2246.field_10247.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8179) {
							arg.method_8652(lv, class_2246.field_10609.method_9564(), 3);
							bl = true;
						} else if (lv5.method_7909() == class_1802.field_8309) {
							arg.method_8652(lv, class_2246.field_10341.method_9564(), 3);
							bl = true;
						}
					}

					if (bl) {
						lv5.method_7934(1);
						if (lv5.method_7960()) {
							lv4.method_5447(i, class_1799.field_8037);
						}
						break;
					}
				}
			}

			this.field_18861 = l + 20L;
		}
	}

	protected void method_19566(class_3218 arg, class_1646 arg2, long l) {
		arg2.method_18868().method_18875(class_4140.field_18446);
	}
}
