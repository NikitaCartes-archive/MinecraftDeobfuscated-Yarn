package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1785 extends class_1755 {
	private final class_1299<?> field_7991;

	public class_1785(class_1299<?> arg, class_3611 arg2, class_1792.class_1793 arg3) {
		super(arg2, arg3);
		this.field_7991 = arg;
	}

	@Override
	public void method_7728(class_1937 arg, class_1799 arg2, class_2338 arg3) {
		if (!arg.field_9236) {
			this.method_7824(arg, arg2, arg3);
		}
	}

	@Override
	protected void method_7727(@Nullable class_1657 arg, class_1936 arg2, class_2338 arg3) {
		arg2.method_8396(arg, arg3, class_3417.field_14912, class_3419.field_15254, 1.0F, 1.0F);
	}

	private void method_7824(class_1937 arg, class_1799 arg2, class_2338 arg3) {
		class_1297 lv = this.field_7991.method_5894(arg, arg2, null, arg3, class_3730.field_16473, true, false);
		if (lv != null) {
			((class_1422)lv).method_6454(true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		if (this.field_7991 == class_1299.field_6111) {
			class_2487 lv = arg.method_7969();
			if (lv != null && lv.method_10573("BucketVariantTag", 3)) {
				int i = lv.method_10550("BucketVariantTag");
				class_124[] lvs = new class_124[]{class_124.field_1056, class_124.field_1080};
				String string = "color.minecraft." + class_1474.method_6652(i);
				String string2 = "color.minecraft." + class_1474.method_6651(i);

				for (int j = 0; j < class_1474.field_6879.length; j++) {
					if (i == class_1474.field_6879[j]) {
						list.add(new class_2588(class_1474.method_6649(j)).method_10856(lvs));
						return;
					}
				}

				list.add(new class_2588(class_1474.method_6657(i)).method_10856(lvs));
				class_2561 lv2 = new class_2588(string);
				if (!string.equals(string2)) {
					lv2.method_10864(", ").method_10852(new class_2588(string2));
				}

				lv2.method_10856(lvs);
				list.add(lv2);
			}
		}
	}
}
