package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum Attachment implements StringRepresentable {
	field_17098("floor"),
	field_17099("ceiling"),
	field_17100("single_wall"),
	field_17101("double_wall");

	private final String field_17102;

	private Attachment(String string2) {
		this.field_17102 = string2;
	}

	@Override
	public String asString() {
		return this.field_17102;
	}
}
