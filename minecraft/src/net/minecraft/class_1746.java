package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

public class class_1746 extends class_1827 {
	public class_1746(class_2248 arg, class_2248 arg2, class_1792.class_1793 arg3) {
		super(arg, arg2, arg3);
		Validate.isInstanceOf(class_2185.class, arg);
		Validate.isInstanceOf(class_2185.class, arg2);
	}

	@Environment(EnvType.CLIENT)
	public static void method_7705(class_1799 arg, List<class_2561> list) {
		class_2487 lv = arg.method_7941("BlockEntityTag");
		if (lv != null && lv.method_10545("Patterns")) {
			class_2499 lv2 = lv.method_10554("Patterns", 10);

			for (int i = 0; i < lv2.size() && i < 6; i++) {
				class_2487 lv3 = lv2.method_10602(i);
				class_1767 lv4 = class_1767.method_7791(lv3.method_10550("Color"));
				class_2582 lv5 = class_2582.method_10946(lv3.method_10558("Pattern"));
				if (lv5 != null) {
					list.add(new class_2588("block.minecraft.banner." + lv5.method_10947() + '.' + lv4.method_7792()).method_10854(class_124.field_1080));
				}
			}
		}
	}

	public class_1767 method_7706() {
		return ((class_2185)this.method_7711()).method_9303();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		method_7705(arg, list);
	}
}
