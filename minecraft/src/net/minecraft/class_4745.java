package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class class_4745 extends DataFix {
	private final String field_21816;
	private final Function<String, String> field_21817;

	public class_4745(Schema schema, boolean bl, String string, Function<String, String> function) {
		super(schema, bl);
		this.field_21816 = string;
		this.field_21817 = function;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(TypeReferences.RECIPE.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
			throw new IllegalStateException("Recipe type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.field_21816, type, dynamicOps -> pair -> pair.mapSecond(this.field_21817));
		}
	}
}
