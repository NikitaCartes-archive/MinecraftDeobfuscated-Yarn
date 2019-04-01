package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_934 extends class_3729<class_1604> {
	private static final class_2960 field_4757 = new class_2960("textures/entity/illager/pillager.png");

	public class_934(class_898 arg) {
		super(arg, new class_589<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new class_989<>(this));
	}

	protected class_2960 method_4092(class_1604 arg) {
		return field_4757;
	}
}
