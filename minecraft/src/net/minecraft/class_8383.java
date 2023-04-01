package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum class_8383 implements StringIdentifiable {
	PICK_LOW("pick_low"),
	PICK_HIGH("pick_high"),
	PICK_RANDOM("pick_random"),
	PICK_ALL("pick_all"),
	PICK_NONE("pick_none"),
	FAIL("fail");

	public static final com.mojang.serialization.Codec<class_8383> field_44017 = StringIdentifiable.createCodec(class_8383::values);
	private final String field_44018;
	private final Text field_44019;

	private class_8383(String string2) {
		this.field_44018 = string2;
		this.field_44019 = Text.translatable("rule.tie_strategy." + string2);
	}

	@Override
	public String asString() {
		return this.field_44018;
	}

	public Text method_50576() {
		return this.field_44019;
	}
}
