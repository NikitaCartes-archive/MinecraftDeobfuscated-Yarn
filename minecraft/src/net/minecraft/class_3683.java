package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3683 extends class_927<class_3701, class_582<class_3701>> {
	private static final class_2960 field_16259 = new class_2960("textures/entity/cat/ocelot.png");

	public class_3683(class_898 arg) {
		super(arg, new class_582<>(0.0F), 0.4F);
	}

	protected class_2960 method_16046(class_3701 arg) {
		return field_16259;
	}
}
