package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class AttributeIdPrefixFix extends AttributeRenameFix {
	private static final List<String> PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");

	public AttributeIdPrefixFix(Schema outputSchema) {
		super(outputSchema, "AttributeIdPrefixFix", AttributeIdPrefixFix::removePrefix);
	}

	private static String removePrefix(String id) {
		String string = IdentifierNormalizingSchema.normalize(id);

		for (String string2 : PREFIXES) {
			String string3 = IdentifierNormalizingSchema.normalize(string2);
			if (string.startsWith(string3)) {
				return "minecraft:" + string.substring(string3.length());
			}
		}

		return id;
	}
}
