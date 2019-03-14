package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public enum AbsoluteHand {
	field_6182(new TranslatableTextComponent("options.mainHand.left")),
	field_6183(new TranslatableTextComponent("options.mainHand.right"));

	private final TextComponent name;

	private AbsoluteHand(TextComponent textComponent) {
		this.name = textComponent;
	}

	@Environment(EnvType.CLIENT)
	public AbsoluteHand getOpposite() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.name.getString();
	}
}
