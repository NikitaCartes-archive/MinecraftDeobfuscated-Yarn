package net.minecraft;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_1220 extends Schema {
	public class_1220(int i, Schema schema) {
		super(i, schema);
	}

	public static String method_5193(String string) {
		class_2960 lv = class_2960.method_12829(string);
		return lv != null ? lv.toString() : string;
	}

	@Override
	public Type<?> getChoiceType(TypeReference typeReference, String string) {
		return super.getChoiceType(typeReference, method_5193(string));
	}
}
