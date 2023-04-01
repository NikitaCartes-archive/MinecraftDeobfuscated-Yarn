package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum class_8300 implements StringIdentifiable {
	OFF("off", Text.translatable("rule.autojump.off")),
	YES("yes", Text.translatable("rule.autoJump.yes")),
	ON("on", Text.translatable("rule.autoJump.on")),
	TRUE("true", Text.translatable("rule.autoJump.true")),
	OF_COURSE("of_course", Text.translatable("rule.autoJump.of_course")),
	ALSO_DEFAULT_VANILLA_TO_TRUE("also_default_vanilla_to_true", Text.translatable("rule.autoJump.also_default_vanilla_to_true"));

	public static final com.mojang.serialization.Codec<class_8300> field_43728 = StringIdentifiable.createCodec(class_8300::values);
	private final String field_43729;
	private final Text field_43730;

	private class_8300(String string2, Text text) {
		this.field_43729 = string2;
		this.field_43730 = text;
	}

	public Text method_50279() {
		return this.field_43730;
	}

	@Override
	public String asString() {
		return this.field_43729;
	}
}
