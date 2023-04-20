package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class DecoratedPotFieldRenameFix extends DataFix {
	private static final String DECORATED_POT_ID = "minecraft:decorated_pot";

	public DecoratedPotFieldRenameFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:decorated_pot");
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:decorated_pot");
		return this.convertUnchecked("DecoratedPotFieldRenameFix", type, type2);
	}
}
