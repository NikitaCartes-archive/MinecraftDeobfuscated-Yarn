package net.minecraft;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface class_1924 {
	List<class_1297> method_8333(@Nullable class_1297 arg, class_238 arg2, @Nullable Predicate<? super class_1297> predicate);

	default List<class_1297> method_8335(@Nullable class_1297 arg, class_238 arg2) {
		return this.method_8333(arg, arg2, class_1301.field_6155);
	}

	default Stream<class_265> method_8334(@Nullable class_1297 arg, class_265 arg2, Set<class_1297> set) {
		if (arg2.method_1110()) {
			return Stream.empty();
		} else {
			class_238 lv = arg2.method_1107();
			return this.method_8335(arg, lv.method_1014(0.25))
				.stream()
				.filter(arg2x -> !set.contains(arg2x) && (arg == null || !arg.method_5794(arg2x)))
				.flatMap(
					arg3 -> Stream.of(arg3.method_5827(), arg == null ? null : arg.method_5708(arg3))
							.filter(Objects::nonNull)
							.filter(arg2xx -> arg2xx.method_994(lv))
							.map(class_259::method_1078)
				);
		}
	}
}
