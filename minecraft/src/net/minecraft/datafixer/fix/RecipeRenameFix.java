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

public class RecipeRenameFix extends DataFix {
	private final String name;
	private final Function<String, String> renamer;

	public RecipeRenameFix(Schema schema, boolean bl, String name, Function<String, String> renamer) {
		super(schema, bl);
		this.name = name;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.RECIPE.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
			throw new IllegalStateException("Recipe type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this.renamer));
		}
	}
}