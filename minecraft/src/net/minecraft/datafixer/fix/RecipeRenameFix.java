package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class RecipeRenameFix extends DataFix {
	private final String name;
	private final Function<String, String> renamer;

	public RecipeRenameFix(Schema outputSchema, boolean changesType, String name, Function<String, String> renamer) {
		super(outputSchema, changesType);
		this.name = name;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.RECIPE.typeName(), IdentifierNormalizingSchema.getIdentifierType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
			throw new IllegalStateException("Recipe type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this.renamer));
		}
	}
}
