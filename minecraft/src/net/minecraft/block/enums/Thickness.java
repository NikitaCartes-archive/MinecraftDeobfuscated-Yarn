package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum Thickness implements StringIdentifiable {
	TIP_MERGE("tip_merge"),
	TIP("tip"),
	FRUSTUM("frustum"),
	MIDDLE("middle"),
	BASE("base");

	private final String name;

	private Thickness(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
