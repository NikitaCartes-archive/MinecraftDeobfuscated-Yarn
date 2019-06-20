package net.minecraft;

import com.google.common.collect.Iterables;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4044 extends class_4075 {
	private static final class_2960 field_18032 = new class_2960("back");

	public class_4044(class_1060 arg) {
		super(arg, class_1059.field_18031, "textures/painting");
	}

	@Override
	protected Iterable<class_2960> method_18665() {
		return Iterables.concat(class_2378.field_11150.method_10235(), Collections.singleton(field_18032));
	}

	public class_1058 method_18345(class_1535 arg) {
		return this.method_18667(class_2378.field_11150.method_10221(arg));
	}

	public class_1058 method_18342() {
		return this.method_18667(field_18032);
	}
}
