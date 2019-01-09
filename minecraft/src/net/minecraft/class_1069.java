package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1069 implements class_3302 {
	private static final class_2960 field_5302 = new class_2960("textures/colormap/grass.png");

	@Override
	public void method_14491(class_3300 arg) {
		try {
			class_1933.method_8376(class_3685.method_16049(arg, field_5302));
		} catch (IOException var3) {
		}
	}
}
