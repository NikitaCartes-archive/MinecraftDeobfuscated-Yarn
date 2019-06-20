package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1780 extends class_1792 {
	public class_1780(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_2487 lv = arg.method_7941("Explosion");
		if (lv != null) {
			method_7809(lv, list);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_7809(class_2487 arg, List<class_2561> list) {
		class_1781.class_1782 lv = class_1781.class_1782.method_7813(arg.method_10571("Type"));
		list.add(new class_2588("item.minecraft.firework_star.shape." + lv.method_7812()).method_10854(class_124.field_1080));
		int[] is = arg.method_10561("Colors");
		if (is.length > 0) {
			list.add(method_7811(new class_2585("").method_10854(class_124.field_1080), is));
		}

		int[] js = arg.method_10561("FadeColors");
		if (js.length > 0) {
			list.add(method_7811(new class_2588("item.minecraft.firework_star.fade_to").method_10864(" ").method_10854(class_124.field_1080), js));
		}

		if (arg.method_10577("Trail")) {
			list.add(new class_2588("item.minecraft.firework_star.trail").method_10854(class_124.field_1080));
		}

		if (arg.method_10577("Flicker")) {
			list.add(new class_2588("item.minecraft.firework_star.flicker").method_10854(class_124.field_1080));
		}
	}

	@Environment(EnvType.CLIENT)
	private static class_2561 method_7811(class_2561 arg, int[] is) {
		for (int i = 0; i < is.length; i++) {
			if (i > 0) {
				arg.method_10864(", ");
			}

			arg.method_10852(method_7810(is[i]));
		}

		return arg;
	}

	@Environment(EnvType.CLIENT)
	private static class_2561 method_7810(int i) {
		class_1767 lv = class_1767.method_7786(i);
		return lv == null ? new class_2588("item.minecraft.firework_star.custom_color") : new class_2588("item.minecraft.firework_star." + lv.method_7792());
	}
}
