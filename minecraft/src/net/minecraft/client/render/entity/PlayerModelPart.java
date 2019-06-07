package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public enum PlayerModelPart {
	field_7559(0, "cape"),
	field_7564(1, "jacket"),
	field_7568(2, "left_sleeve"),
	field_7570(3, "right_sleeve"),
	field_7566(4, "left_pants_leg"),
	field_7565(5, "right_pants_leg"),
	field_7563(6, "hat");

	private final int id;
	private final int bitFlag;
	private final String name;
	private final Text optionName;

	private PlayerModelPart(int j, String string2) {
		this.id = j;
		this.bitFlag = 1 << j;
		this.name = string2;
		this.optionName = new TranslatableText("options.modelPart." + string2);
	}

	public int getBitFlag() {
		return this.bitFlag;
	}

	public String getName() {
		return this.name;
	}

	public Text getOptionName() {
		return this.optionName;
	}
}
