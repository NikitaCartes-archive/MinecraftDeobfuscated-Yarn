package net.minecraft.sortme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public enum OptionMainHand {
	field_6182(new TranslatableTextComponent("options.mainHand.left")),
	field_6183(new TranslatableTextComponent("options.mainHand.right"));

	private final TextComponent field_6181;

	private OptionMainHand(TextComponent textComponent) {
		this.field_6181 = textComponent;
	}

	@Environment(EnvType.CLIENT)
	public OptionMainHand getOpposite() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.field_6181.getString();
	}
}
