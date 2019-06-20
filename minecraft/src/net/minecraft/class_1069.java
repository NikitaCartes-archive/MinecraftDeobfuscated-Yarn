package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1069 extends class_4080<int[]> {
	private static final class_2960 field_5302 = new class_2960("textures/colormap/grass.png");

	protected int[] method_18662(class_3300 arg, class_3695 arg2) {
		try {
			return class_3685.method_16049(arg, field_5302);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load grass color texture", var4);
		}
	}

	protected void method_18661(int[] is, class_3300 arg, class_3695 arg2) {
		class_1933.method_8376(is);
	}
}
