package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class AdvancementCriteriaRenameFix extends DataFix {
	private final String description;
	private final String advancementId;
	private final UnaryOperator<String> renamer;

	public AdvancementCriteriaRenameFix(Schema outputSchema, String description, String advancementId, UnaryOperator<String> renamer) {
		super(outputSchema, false);
		this.description = description;
		this.advancementId = advancementId;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.description, this.getInputSchema().getType(TypeReferences.ADVANCEMENTS), typed -> typed.update(DSL.remainderFinder(), this::update)
		);
	}

	private Dynamic<?> update(Dynamic<?> advancements) {
		return advancements.update(
			this.advancementId,
			advancement -> advancement.update(
					"criteria",
					criteria -> criteria.updateMapValues(
							pair -> pair.mapFirst(key -> DataFixUtils.orElse(key.asString().map(keyString -> key.createString((String)this.renamer.apply(keyString))).result(), key))
						)
				)
		);
	}
}
