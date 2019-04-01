package net.minecraft;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1813 extends class_1792 {
	private static final Map<class_3414, class_1813> field_8901 = Maps.<class_3414, class_1813>newHashMap();
	private final int field_8902;
	private final class_3414 field_8900;

	protected class_1813(int i, class_3414 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_8902 = i;
		this.field_8900 = arg;
		field_8901.put(this.field_8900, this);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (lv3.method_11614() == class_2246.field_10223 && !(Boolean)lv3.method_11654(class_2387.field_11180)) {
			class_1799 lv4 = arg.method_8041();
			if (!lv.field_9236) {
				((class_2387)class_2246.field_10223).method_10276(lv, lv2, lv3, lv4);
				lv.method_8444(null, 1010, lv2, class_1792.method_7880(this));
				lv4.method_7934(1);
				class_1657 lv5 = arg.method_8036();
				if (lv5 != null) {
					lv5.method_7281(class_3468.field_15375);
				}
			}

			return class_1269.field_5812;
		} else {
			return class_1269.field_5811;
		}
	}

	public int method_8010() {
		return this.field_8902;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		list.add(this.method_8011().method_10854(class_124.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_8011() {
		return new class_2588(this.method_7876() + ".desc");
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1813 method_8012(class_3414 arg) {
		return (class_1813)field_8901.get(arg);
	}

	@Environment(EnvType.CLIENT)
	public class_3414 method_8009() {
		return this.field_8900;
	}
}
