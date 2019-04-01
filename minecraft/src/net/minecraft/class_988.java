package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_988 extends class_3887<class_1501, class_578<class_1501>> {
	private static final class_2960[] field_4880 = new class_2960[]{
		new class_2960("textures/entity/llama/decor/white.png"),
		new class_2960("textures/entity/llama/decor/orange.png"),
		new class_2960("textures/entity/llama/decor/magenta.png"),
		new class_2960("textures/entity/llama/decor/light_blue.png"),
		new class_2960("textures/entity/llama/decor/yellow.png"),
		new class_2960("textures/entity/llama/decor/lime.png"),
		new class_2960("textures/entity/llama/decor/pink.png"),
		new class_2960("textures/entity/llama/decor/gray.png"),
		new class_2960("textures/entity/llama/decor/light_gray.png"),
		new class_2960("textures/entity/llama/decor/cyan.png"),
		new class_2960("textures/entity/llama/decor/purple.png"),
		new class_2960("textures/entity/llama/decor/blue.png"),
		new class_2960("textures/entity/llama/decor/brown.png"),
		new class_2960("textures/entity/llama/decor/green.png"),
		new class_2960("textures/entity/llama/decor/red.png"),
		new class_2960("textures/entity/llama/decor/black.png")
	};
	private static final class_2960 field_17740 = new class_2960("textures/entity/llama/decor/trader_llama.png");
	private final class_578<class_1501> field_4881 = new class_578<>(0.5F);

	public class_988(class_3883<class_1501, class_578<class_1501>> arg) {
		super(arg);
	}

	public void method_4191(class_1501 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1767 lv = arg.method_6800();
		if (lv != null) {
			this.method_17164(field_4880[lv.method_7789()]);
		} else {
			if (!arg.method_6807()) {
				return;
			}

			this.method_17164(field_17740);
		}

		this.method_17165().method_17081(this.field_4881);
		this.field_4881.method_17100(arg, f, g, i, j, k, l);
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
