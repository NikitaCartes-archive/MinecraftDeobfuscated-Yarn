package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;

public class class_1203 extends DataFix {
	public class_1203(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsLowerCaseLanguageFix", this.getInputSchema().getType(class_1208.field_5717), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
					Optional<String> optional = dynamic.get("lang").asString();
					return optional.isPresent() ? dynamic.set("lang", dynamic.createString(((String)optional.get()).toLowerCase(Locale.ROOT))) : dynamic;
				})
		);
	}
}
