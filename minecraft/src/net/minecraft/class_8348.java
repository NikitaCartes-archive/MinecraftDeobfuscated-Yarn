package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum class_8348 implements StringIdentifiable {
	NORMAL_ONLY("normal_only"),
	FLIPPED_ONLY("flipped_only"),
	BOTH("both");

	public static final com.mojang.serialization.Codec<class_8348> field_43925 = StringIdentifiable.createCodec(class_8348::values);
	private final String field_43926;
	private final Text field_43927;

	private class_8348(String string2) {
		this.field_43926 = string2;
		this.field_43927 = Text.translatable("rule.recipe_flip." + string2);
	}

	public Text method_50422() {
		return this.field_43927;
	}

	@Override
	public String asString() {
		return this.field_43926;
	}
}
