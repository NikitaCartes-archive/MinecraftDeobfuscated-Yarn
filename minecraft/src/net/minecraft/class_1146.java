package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1146 implements class_1148<class_1111> {
	private final List<class_1148<class_1111>> field_5600 = Lists.<class_1148<class_1111>>newArrayList();
	private final Random field_5601 = new Random();
	private final class_2960 field_5602;
	private final class_2561 field_5599;

	public class_1146(class_2960 arg, @Nullable String string) {
		this.field_5602 = arg;
		this.field_5599 = string == null ? null : new class_2588(string);
	}

	@Override
	public int method_4894() {
		int i = 0;

		for (class_1148<class_1111> lv : this.field_5600) {
			i += lv.method_4894();
		}

		return i;
	}

	public class_1111 method_4887() {
		int i = this.method_4894();
		if (!this.field_5600.isEmpty() && i != 0) {
			int j = this.field_5601.nextInt(i);

			for (class_1148<class_1111> lv : this.field_5600) {
				j -= lv.method_4894();
				if (j < 0) {
					return lv.method_4893();
				}
			}

			return class_1144.field_5592;
		} else {
			return class_1144.field_5592;
		}
	}

	public void method_4885(class_1148<class_1111> arg) {
		this.field_5600.add(arg);
	}

	@Nullable
	public class_2561 method_4886() {
		return this.field_5599;
	}
}
