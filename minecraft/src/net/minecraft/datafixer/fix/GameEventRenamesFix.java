package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class GameEventRenamesFix extends DataFix {
	private final String name;
	private final TypeReference typeReference;
	private final UnaryOperator<String> renamer;

	public GameEventRenamesFix(Schema outputSchema, String name, TypeReference typeReference, UnaryOperator<String> renamer) {
		super(outputSchema, false);
		this.name = name;
		this.typeReference = typeReference;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(this.typeReference.typeName(), IdentifierNormalizingSchema.getIdentifierType());
		if (!Objects.equals(type, this.getInputSchema().getType(this.typeReference))) {
			throw new IllegalStateException("\"" + this.typeReference.typeName() + "\" is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this.renamer));
		}
	}
}
