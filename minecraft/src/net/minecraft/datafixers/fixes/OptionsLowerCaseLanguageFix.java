package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class OptionsLowerCaseLanguageFix extends DataFix {
	public OptionsLowerCaseLanguageFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsLowerCaseLanguageFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = dynamic.get("lang").asString();
					return optional.isPresent() ? dynamic.set("lang", dynamic.createString(((String)optional.get()).toLowerCase(Locale.ROOT))) : dynamic;
				})
		);
	}
}
