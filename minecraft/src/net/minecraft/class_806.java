package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_806 {
	public static final class_806 field_4292 = new class_806();
	private final List<class_799> field_4293 = Lists.<class_799>newArrayList();
	private final List<class_1087> field_4291;

	private class_806() {
		this.field_4291 = Collections.emptyList();
	}

	public class_806(class_1088 arg, class_793 arg2, Function<class_2960, class_1100> function, List<class_799> list) {
		this.field_4291 = (List<class_1087>)list.stream().map(arg3 -> {
			class_1100 lv = (class_1100)function.apply(arg3.method_3472());
			return Objects.equals(lv, arg2) ? null : arg.method_15878(arg3.method_3472(), class_1086.field_5350);
		}).collect(Collectors.toList());
		Collections.reverse(this.field_4291);

		for (int i = list.size() - 1; i >= 0; i--) {
			this.field_4293.add(list.get(i));
		}
	}

	@Nullable
	public class_1087 method_3495(class_1087 arg, class_1799 arg2, @Nullable class_1937 arg3, @Nullable class_1309 arg4) {
		if (!this.field_4293.isEmpty()) {
			for (int i = 0; i < this.field_4293.size(); i++) {
				class_799 lv = (class_799)this.field_4293.get(i);
				if (lv.method_3473(arg2, arg3, arg4)) {
					class_1087 lv2 = (class_1087)this.field_4291.get(i);
					if (lv2 == null) {
						return arg;
					}

					return lv2;
				}
			}
		}

		return arg;
	}
}
