package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Arm {
	LEFT(new TranslatableText("options.mainHand.left")),
	RIGHT(new TranslatableText("options.mainHand.right"));

	private final Text optionName;

	private Arm(Text optionName) {
		this.optionName = optionName;
	}

	@Environment(EnvType.CLIENT)
	public Arm getOpposite() {
		return this == LEFT ? RIGHT : LEFT;
	}

	public String toString() {
		return this.optionName.getString();
	}

	@Environment(EnvType.CLIENT)
	public Text method_27301() {
		return this.optionName;
	}
}
