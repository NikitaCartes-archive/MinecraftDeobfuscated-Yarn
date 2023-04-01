package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum class_8363 implements StringIdentifiable {
	DEFAULT("default"),
	NEVER("never"),
	ALWAYS("always");

	public static final com.mojang.serialization.Codec<class_8363> field_43966 = StringIdentifiable.createCodec(class_8363::values);
	private final String field_43967;
	private final Text field_43968;
	private final Text field_43969;

	private class_8363(String string2) {
		this.field_43967 = string2;
		this.field_43969 = Text.translatable("rule.weather.rain." + string2);
		this.field_43968 = Text.translatable("rule.weather.thunder." + string2);
	}

	@Override
	public String asString() {
		return this.field_43967;
	}

	public Text method_50454() {
		return this.field_43969;
	}

	public Text method_50455() {
		return this.field_43968;
	}
}
