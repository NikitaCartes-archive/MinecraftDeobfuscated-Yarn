package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public enum PlayerModelPart {
	CAPE(0, "cape"),
	BODY(1, "jacket"),
	LEFT_ARM(2, "left_sleeve"),
	RIGHT_ARM(3, "right_sleeve"),
	LEFT_LEG(4, "left_pants_leg"),
	RIGHT_LEG(5, "right_pants_leg"),
	HEAD(6, "hat");

	private final int id;
	private final int bitFlag;
	private final String name;
	private final TextComponent localizedName;

	private PlayerModelPart(int j, String string2) {
		this.id = j;
		this.bitFlag = 1 << j;
		this.name = string2;
		this.localizedName = new TranslatableTextComponent("options.modelPart." + string2);
	}

	public int getBitFlag() {
		return this.bitFlag;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public TextComponent getLocalizedName() {
		return this.localizedName;
	}
}
