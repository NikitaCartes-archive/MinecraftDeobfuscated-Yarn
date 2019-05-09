package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class SchemaIdentifierNormalize extends Schema {
	public SchemaIdentifierNormalize(int i, Schema schema) {
		super(i, schema);
	}

	public static String normalize(String string) {
		Identifier identifier = Identifier.ofNullable(string);
		return identifier != null ? identifier.toString() : string;
	}

	@Override
	public Type<?> getChoiceType(TypeReference typeReference, String string) {
		return super.getChoiceType(typeReference, normalize(string));
	}
}
