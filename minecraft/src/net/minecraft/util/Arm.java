package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Arm {
	field_6182(new TranslatableText("options.mainHand.left")),
	field_6183(new TranslatableText("options.mainHand.right"));

	private final Text optionName;

	private Arm(Text text) {
		this.optionName = text;
	}

	@Environment(EnvType.CLIENT)
	public Arm getOpposite() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.optionName.getString();
	}
}
