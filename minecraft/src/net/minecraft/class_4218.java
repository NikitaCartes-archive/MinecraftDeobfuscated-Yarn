package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class class_4218 extends class_4097<class_1314> {
	public class_4218() {
		super(
			ImmutableMap.of(
				class_4140.field_19006,
				class_4141.field_18456,
				class_4140.field_18445,
				class_4141.field_18457,
				class_4140.field_18446,
				class_4141.field_18458,
				class_4140.field_18447,
				class_4141.field_18458
			)
		);
	}

	protected boolean method_19583(class_3218 arg, class_1314 arg2) {
		return arg.method_8409().nextInt(10) == 0 && this.method_19590(arg2);
	}

	protected void method_19584(class_3218 arg, class_1314 arg2, long l) {
		class_1309 lv = this.method_19586(arg2);
		if (lv != null) {
			this.method_19585(arg, arg2, lv);
		} else {
			Optional<class_1309> optional = this.method_19588(arg2);
			if (optional.isPresent()) {
				method_19580(arg2, (class_1309)optional.get());
			} else {
				this.method_19579(arg2).ifPresent(arg2x -> method_19580(arg2, arg2x));
			}
		}
	}

	private void method_19585(class_3218 arg, class_1314 arg2, class_1309 arg3) {
		for (int i = 0; i < 10; i++) {
			class_243 lv = class_1414.method_6378(arg2, 20, 8);
			if (lv != null && arg.method_19500(new class_2338(lv))) {
				arg2.method_18868().method_18878(class_4140.field_18445, new class_4142(lv, 0.6F, 0));
				return;
			}
		}
	}

	private static void method_19580(class_1314 arg, class_1309 arg2) {
		class_4095<?> lv = arg.method_18868();
		lv.method_18878(class_4140.field_18447, arg2);
		lv.method_18878(class_4140.field_18446, new class_4102(arg2));
		lv.method_18878(class_4140.field_18445, new class_4142(new class_4102(arg2), 0.6F, 1));
	}

	private Optional<class_1309> method_19579(class_1314 arg) {
		return this.method_19594(arg).stream().findAny();
	}

	private Optional<class_1309> method_19588(class_1314 arg) {
		Map<class_1309, Integer> map = this.method_19592(arg);
		return map.entrySet()
			.stream()
			.sorted(Comparator.comparingInt(Entry::getValue))
			.filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5)
			.map(Entry::getKey)
			.findFirst();
	}

	private Map<class_1309, Integer> method_19592(class_1314 arg) {
		Map<class_1309, Integer> map = Maps.<class_1309, Integer>newHashMap();
		this.method_19594(arg).stream().filter(this::method_19593).forEach(argx -> {
			Integer var10000 = (Integer)map.compute(this.method_19576(argx), (argxx, integer) -> integer == null ? 1 : integer + 1);
		});
		return map;
	}

	private List<class_1309> method_19594(class_1314 arg) {
		return (List<class_1309>)arg.method_18868().method_18904(class_4140.field_19006).get();
	}

	private class_1309 method_19576(class_1309 arg) {
		return (class_1309)arg.method_18868().method_18904(class_4140.field_18447).get();
	}

	@Nullable
	private class_1309 method_19586(class_1309 arg) {
		return (class_1309)((List)arg.method_18868().method_18904(class_4140.field_19006).get())
			.stream()
			.filter(arg2 -> this.method_19577(arg, arg2))
			.findAny()
			.orElse(null);
	}

	private boolean method_19593(class_1309 arg) {
		return arg.method_18868().method_18904(class_4140.field_18447).isPresent();
	}

	private boolean method_19577(class_1309 arg, class_1309 arg2) {
		return arg2.method_18868().method_18904(class_4140.field_18447).filter(arg2x -> arg2x == arg).isPresent();
	}

	private boolean method_19590(class_1314 arg) {
		return arg.method_18868().method_18896(class_4140.field_19006);
	}
}
