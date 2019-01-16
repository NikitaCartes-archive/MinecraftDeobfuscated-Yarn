package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

public class ObjectiveDisplayNameFix extends DataFix {
	public ObjectiveDisplayNameFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
			throw new IllegalStateException("Objective type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(
				"ObjectiveDisplayNameFix",
				type,
				dynamicOps -> pair -> pair.mapSecond(
							dynamic -> dynamic.update(
									"DisplayName",
									dynamic2 -> DataFixUtils.orElse(
											dynamic2.asString().map(string -> TextComponent.Serializer.toJsonString(new StringTextComponent(string))).map(dynamic::createString), dynamic2
										)
								)
						)
			);
		}
	}
}
