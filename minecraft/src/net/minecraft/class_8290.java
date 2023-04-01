package net.minecraft;

import net.minecraft.util.StringIdentifiable;

public enum class_8290 implements StringIdentifiable {
	APPROVE("approve"),
	REPEAL("repeal");

	public static final com.mojang.serialization.Codec<class_8290> field_43501 = StringIdentifiable.createCodec(class_8290::values);
	private final String field_43502;

	private class_8290(String string2) {
		this.field_43502 = string2;
	}

	@Override
	public String asString() {
		return this.field_43502;
	}
}
