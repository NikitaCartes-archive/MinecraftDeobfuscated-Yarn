package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_910 extends class_875<class_1498, class_549<class_1498>> {
	private static final Map<String, class_2960> field_4714 = Maps.<String, class_2960>newHashMap();

	public class_910(class_898 arg) {
		super(arg, new class_549<>(0.0F), 1.1F);
		this.method_4046(new class_4073(this));
	}

	protected class_2960 method_3983(class_1498 arg) {
		String string = arg.method_6784();
		class_2960 lv = (class_2960)field_4714.get(string);
		if (lv == null) {
			lv = new class_2960(string);
			class_310.method_1551().method_1531().method_4616(lv, new class_1048(arg.method_6787()));
			field_4714.put(string, lv);
		}

		return lv;
	}
}
