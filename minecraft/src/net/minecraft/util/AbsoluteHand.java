package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum AbsoluteHand {
	field_6182(new TranslatableComponent("options.mainHand.left")),
	field_6183(new TranslatableComponent("options.mainHand.right"));

	private final Component name;

	private AbsoluteHand(Component component) {
		this.name = component;
	}

	@Environment(EnvType.CLIENT)
	public AbsoluteHand getOpposite() {
		return this == field_6182 ? field_6183 : field_6182;
	}

	public String toString() {
		return this.name.getString();
	}
}
