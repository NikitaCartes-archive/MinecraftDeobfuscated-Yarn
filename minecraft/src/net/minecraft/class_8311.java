package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public enum class_8311 implements StringIdentifiable {
	NONE("blonk"),
	AWESOM("awesom"),
	SQUID("squid"),
	VETERINARIAN("veterinarian"),
	NO_CIRCLE("no_circle"),
	NYAN("nyan");

	public static final com.mojang.serialization.Codec<class_8311> field_43761 = StringIdentifiable.createCodec(class_8311::values);
	private final String field_43762;
	private final Text field_43763;
	private final Identifier field_43764;

	private class_8311(String string2) {
		this.field_43762 = string2;
		this.field_43763 = Text.translatable("rule.caep." + string2);
		this.field_43764 = new Identifier("textures/entity/player/caeps/" + string2 + ".png");
	}

	@Override
	public String asString() {
		return this.field_43762;
	}

	public Text method_50318() {
		return this.field_43763;
	}

	public Identifier method_50319() {
		return this.field_43764;
	}
}
