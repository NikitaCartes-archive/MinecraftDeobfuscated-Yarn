package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum class_8340 implements StringIdentifiable {
	NONE("none"),
	NORMAL("normal"),
	SEE_THROUGH("see_through");

	public static final com.mojang.serialization.Codec<class_8340> field_43908 = StringIdentifiable.createCodec(class_8340::values);
	private final String field_43909;
	private final Text field_43910;

	private class_8340(String string2) {
		this.field_43909 = string2;
		this.field_43910 = Text.translatable("rule.name_visibility." + string2);
	}

	@Override
	public String asString() {
		return this.field_43909;
	}

	public Text method_50396() {
		return this.field_43910;
	}
}
