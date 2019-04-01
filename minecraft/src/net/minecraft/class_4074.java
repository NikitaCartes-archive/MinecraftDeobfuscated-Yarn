package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4074 extends class_4075 {
	public class_4074(class_1060 arg) {
		super(arg, class_1059.field_18229, "textures/mob_effect");
	}

	@Override
	protected Iterable<class_2960> method_18665() {
		return class_2378.field_11159.method_10235();
	}

	public class_1058 method_18663(class_1291 arg) {
		return this.method_18667(class_2378.field_11159.method_10221(arg));
	}
}
