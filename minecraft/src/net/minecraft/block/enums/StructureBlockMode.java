package net.minecraft.block.enums;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum StructureBlockMode implements StringIdentifiable {
	SAVE("save"),
	LOAD("load"),
	CORNER("corner"),
	DATA("data");

	private final String name;
	private final Text text;

	private StructureBlockMode(String name) {
		this.name = name;
		this.text = Text.translatable("structure_block.mode_info." + name);
	}

	@Override
	public String asString() {
		return this.name;
	}

	public Text asText() {
		return this.text;
	}
}
