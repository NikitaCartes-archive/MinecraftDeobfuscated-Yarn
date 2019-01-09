package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_971 extends class_909<class_1641, class_619<class_1641>> {
	private static final class_2960 field_4835 = new class_2960("textures/entity/zombie_villager/zombie_villager.png");

	public class_971(class_898 arg, class_3296 arg2) {
		super(arg, new class_619<>(), 0.5F);
		this.method_4046(new class_987<>(this, new class_619(0.5F, true), new class_619(1.0F, true)));
		this.method_4046(new class_3885<>(this, arg2, "zombie_villager"));
	}

	protected class_2960 method_4175(class_1641 arg) {
		return field_4835;
	}

	protected void method_4176(class_1641 arg, float f, float g, float h) {
		if (arg.method_7198()) {
			g += (float)(Math.cos((double)arg.field_6012 * 3.25) * Math.PI * 0.25);
		}

		super.method_4058(arg, f, g, h);
	}
}
