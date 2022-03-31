package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class GameEventRenamesFix extends DataFix {
	private final String name;
	private final Map<String, String> renames;
	private final TypeReference field_38383;

	public GameEventRenamesFix(Schema schema, TypeReference typeReference, Map<String, String> renames) {
		this(schema, typeReference, typeReference.typeName() + "-renames at version: " + schema.getVersionKey(), renames);
	}

	public GameEventRenamesFix(Schema schema, TypeReference typeReference, String name, Map<String, String> renames) {
		super(schema, false);
		this.renames = renames;
		this.name = name;
		this.field_38383 = typeReference;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(this.field_38383.typeName(), IdentifierNormalizingSchema.getIdentifierType());
		if (!Objects.equals(type, this.getInputSchema().getType(this.field_38383))) {
			throw new IllegalStateException("\"" + this.field_38383.typeName() + "\" type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(string -> (String)this.renames.getOrDefault(string, string)));
		}
	}
}
