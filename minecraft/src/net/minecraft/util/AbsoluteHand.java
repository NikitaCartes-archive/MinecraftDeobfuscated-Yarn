package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum AbsoluteHand {
	field_6182(new TranslatableText("options.mainHand.left")),
	field_6183(new TranslatableText("options.mainHand.right"));

	private final Text field_6181;

	private AbsoluteHand(Text text) {
		this.field_6181 = text;
	}

	@Environment(EnvType.CLIENT)
	public AbsoluteHand getOpposite() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.field_6181.getString();
	}
}
