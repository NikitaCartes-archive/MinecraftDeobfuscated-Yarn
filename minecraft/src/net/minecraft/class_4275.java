package net.minecraft;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4275 extends class_1792 {
	private static final Random field_19177 = new Random();

	public class_4275(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		class_1799 lv = super.method_7861(arg, arg2, arg3);
		if (arg3 instanceof class_3222) {
			((class_3222)arg3).field_13987.method_14364(new class_2668(11, 0.0F));
		}

		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_124[] lvs = class_124.values();
		class_124 lv = lvs[field_19177.nextInt(lvs.length)];
		list.add(new class_2585("Tasty!").method_10854(lv));
	}
}
