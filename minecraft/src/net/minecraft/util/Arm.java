package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Arm {
	LEFT(new TranslatableText("options.mainHand.left")),
	RIGHT(new TranslatableText("options.mainHand.right"));

	private final Text optionName;

	private Arm(Text optionName) {
		this.optionName = optionName;
	}

	public Arm getOpposite() {
		return this == LEFT ? RIGHT : LEFT;
	}

	public String toString() {
		return this.optionName.getString();
	}

	public Text getOptionName() {
		return this.optionName;
	}
}
