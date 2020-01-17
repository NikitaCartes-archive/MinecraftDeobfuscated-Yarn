package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema extends Schema {
	public IdentifierNormalizingSchema(int i, Schema schema) {
		super(i, schema);
	}

	public static String normalize(String id) {
		Identifier identifier = Identifier.tryParse(id);
		return identifier != null ? identifier.toString() : id;
	}

	@Override
	public Type<?> getChoiceType(TypeReference typeReference, String string) {
		return super.getChoiceType(typeReference, normalize(string));
	}
}
