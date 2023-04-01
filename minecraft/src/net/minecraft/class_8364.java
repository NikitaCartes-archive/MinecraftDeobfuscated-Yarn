package net.minecraft;

import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public enum class_8364 implements StringIdentifiable {
	ONE("1", "earth_1.png"),
	A("a", "earth_a.png"),
	PRIME("prime", "earth_prime.png"),
	NONE("none", "earth.png");

	public static final com.mojang.serialization.Codec<class_8364> field_43975 = StringIdentifiable.createCodec(class_8364::values);
	private final String field_43976;
	private final Identifier field_43977;

	private class_8364(String string2, String string3) {
		this.field_43976 = string2;
		this.field_43977 = new Identifier("textures/environment/" + string3);
	}

	@Override
	public String asString() {
		return this.field_43976;
	}

	public Identifier method_50457() {
		return this.field_43977;
	}
}
