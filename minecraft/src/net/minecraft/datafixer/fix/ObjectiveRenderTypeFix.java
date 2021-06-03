package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class ObjectiveRenderTypeFix extends DataFix {
	public ObjectiveRenderTypeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private static ScoreboardCriterion.RenderType parseLegacyRenderType(String oldName) {
		return oldName.equals("health") ? ScoreboardCriterion.RenderType.HEARTS : ScoreboardCriterion.RenderType.INTEGER;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.OBJECTIVE);
		return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", type, typed -> typed.update(DSL.remainderFinder(), dynamic -> {
				Optional<String> optional = dynamic.get("RenderType").asString().result();
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
