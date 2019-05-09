package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class ObjectiveRenderTypeFix extends DataFix {
	public ObjectiveRenderTypeFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static ScoreboardCriterion.RenderType parseLegacyRenderType(String string) {
		return string.equals("health") ? ScoreboardCriterion.RenderType.field_1471 : ScoreboardCriterion.RenderType.field_1472;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
			throw new IllegalStateException("Objective type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("ObjectiveRenderTypeFix", type, dynamicOps -> pair -> pair.mapSecond(dynamic -> {
						Optional<String> optional = dynamic.get("RenderType").asString();
						if (!optional.isPresent()) {
							String string = dynamic.get("CriteriaName").asString("");
							ScoreboardCriterion.RenderType renderType = parseLegacyRenderType(string);
							return dynamic.set("RenderType", dynamic.createString(renderType.getName()));
						} else {
							return dynamic;
						}
					}));
		}
	}
}
