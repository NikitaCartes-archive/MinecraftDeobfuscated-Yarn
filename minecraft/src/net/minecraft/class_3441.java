package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3441 extends class_3439 {
	private static final Logger field_15303 = LogManager.getLogger();
	private final class_1863 field_15304;

	public class_3441(class_1863 arg) {
		this.field_15304 = arg;
	}

	public int method_14903(Collection<class_1860> collection, class_3222 arg) {
		List<class_2960> list = Lists.<class_2960>newArrayList();
		int i = 0;

		for (class_1860 lv : collection) {
			class_2960 lv2 = lv.method_8114();
			if (!this.field_15300.contains(lv2) && !lv.method_8118()) {
				this.method_14881(lv2);
				this.method_14877(lv2);
				list.add(lv2);
				class_174.field_1207.method_9107(arg, lv);
				i++;
			}
		}

		this.method_14899(class_2713.class_2714.field_12415, arg, list);
		return i;
	}

	public int method_14900(Collection<class_1860> collection, class_3222 arg) {
		List<class_2960> list = Lists.<class_2960>newArrayList();
		int i = 0;

		for (class_1860 lv : collection) {
			class_2960 lv2 = lv.method_8114();
			if (this.field_15300.contains(lv2)) {
				this.method_14879(lv2);
				list.add(lv2);
				i++;
			}
		}

		this.method_14899(class_2713.class_2714.field_12417, arg, list);
		return i;
	}

	private void method_14899(class_2713.class_2714 arg, class_3222 arg2, List<class_2960> list) {
		arg2.field_13987.method_14364(new class_2713(arg, list, Collections.emptyList(), this.field_15299, this.field_15298, this.field_15297, this.field_15296));
	}

	public class_2487 method_14902() {
		class_2487 lv = new class_2487();
		lv.method_10556("isGuiOpen", this.field_15299);
		lv.method_10556("isFilteringCraftable", this.field_15298);
		lv.method_10556("isFurnaceGuiOpen", this.field_15297);
		lv.method_10556("isFurnaceFilteringCraftable", this.field_15296);
		class_2499 lv2 = new class_2499();

		for (class_2960 lv3 : this.field_15300) {
			lv2.method_10606(new class_2519(lv3.toString()));
		}

		lv.method_10566("recipes", lv2);
		class_2499 lv4 = new class_2499();

		for (class_2960 lv5 : this.field_15295) {
			lv4.method_10606(new class_2519(lv5.toString()));
		}

		lv.method_10566("toBeDisplayed", lv4);
		return lv;
	}

	public void method_14901(class_2487 arg) {
		this.field_15299 = arg.method_10577("isGuiOpen");
		this.field_15298 = arg.method_10577("isFilteringCraftable");
		this.field_15297 = arg.method_10577("isFurnaceGuiOpen");
		this.field_15296 = arg.method_10577("isFurnaceFilteringCraftable");
		class_2499 lv = arg.method_10554("recipes", 8);

		for (int i = 0; i < lv.size(); i++) {
			class_2960 lv2 = new class_2960(lv.method_10608(i));
			class_1860 lv3 = this.field_15304.method_8130(lv2);
			if (lv3 == null) {
				field_15303.error("Tried to load unrecognized recipe: {} removed now.", lv2);
			} else {
				this.method_14876(lv3);
			}
		}

		class_2499 lv4 = arg.method_10554("toBeDisplayed", 8);

		for (int j = 0; j < lv4.size(); j++) {
			class_2960 lv5 = new class_2960(lv4.method_10608(j));
			class_1860 lv6 = this.field_15304.method_8130(lv5);
			if (lv6 == null) {
				field_15303.error("Tried to load unrecognized recipe: {} removed now.", lv5);
			} else {
				this.method_14885(lv6);
			}
		}
	}

	public void method_14904(class_3222 arg) {
		arg.field_13987
			.method_14364(
				new class_2713(
					class_2713.class_2714.field_12416, this.field_15300, this.field_15295, this.field_15299, this.field_15298, this.field_15297, this.field_15296
				)
			);
	}
}
