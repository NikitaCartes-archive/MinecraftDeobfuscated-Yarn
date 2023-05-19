package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.Text;

public class ObjectiveDisplayNameFix extends DataFix {
	public ObjectiveDisplayNameFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.OBJECTIVE);
		return this.fixTypeEverywhereTyped(
			"ObjectiveDisplayNameFix",
			type,
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> dynamic.update(
							"DisplayName",
							dynamic2 -> DataFixUtils.orElse(
									dynamic2.asString().map(string -> Text.Serializer.toJson(Text.literal(string))).map(dynamic::createString).result(), dynamic2
								)
						)
				)
		);
	}
}
