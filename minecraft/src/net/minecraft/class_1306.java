package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1306 {
	field_6182(new class_2588("options.mainHand.left")),
	field_6183(new class_2588("options.mainHand.right"));

	private final class_2561 field_6181;

	private class_1306(class_2561 arg) {
		this.field_6181 = arg;
	}

	@Environment(EnvType.CLIENT)
	public class_1306 method_5928() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.field_6181.getString();
	}
}
