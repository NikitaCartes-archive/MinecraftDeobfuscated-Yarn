package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public enum class_5672 {
	field_27944(0, new TranslatableText("generator.default")),
	field_27945(1, new TranslatableText("generator.flat")),
	field_27946(2, new TranslatableText("generator.large_biomes")),
	field_27947(3, new TranslatableText("generator.amplified"));

	private final int field_27948;
	private final Text field_27949;

	private class_5672(int j, Text text) {
		this.field_27948 = j;
		this.field_27949 = text;
	}

	public Text method_32506() {
		return this.field_27949;
	}

	public int method_32507() {
		return this.field_27948;
	}
}
