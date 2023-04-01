package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum class_8323 implements StringIdentifiable {
	NONE("none", Text.translatable("rule.flailing.none")),
	NORMAL("normal", Text.translatable("rule.flailing.normal")),
	MILD("mild", Text.translatable("rule.flailing.mild")),
	WILD("wild", Text.translatable("rule.flailing.wild").formatted(Formatting.BOLD)),
	EXTREME("extreme", Text.translatable("rule.flailing.extreme").formatted(Formatting.BOLD, Formatting.RED)),
	WINDMILL("windmill", Text.translatable("rule.flailing.windmill").formatted(Formatting.BOLD, Formatting.RED, Formatting.UNDERLINE));

	private final String field_43815;
	private final Text field_43816;
	public static final com.mojang.serialization.Codec<class_8323> field_43814 = StringIdentifiable.createCodec(class_8323::values);

	private class_8323(String string2, Text text) {
		this.field_43815 = string2;
		this.field_43816 = text;
	}

	public Text method_50350() {
		return this.field_43816;
	}

	@Override
	public String asString() {
		return this.field_43815;
	}
}
