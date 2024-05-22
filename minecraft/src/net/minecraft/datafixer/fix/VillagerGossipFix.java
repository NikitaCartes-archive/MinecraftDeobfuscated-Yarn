package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class VillagerGossipFix extends ChoiceFix {
	public VillagerGossipFix(Schema outputSchema, String choiceType) {
		super(outputSchema, false, "Gossip for for " + choiceType, TypeReferences.ENTITY, choiceType);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(
			DSL.remainderFinder(),
			entityDynamic -> entityDynamic.update(
					"Gossips",
					gossipsDynamic -> DataFixUtils.orElse(
							gossipsDynamic.asStreamOpt()
								.result()
								.map(gossips -> gossips.map(gossipDynamic -> (Dynamic)AbstractUuidFix.updateRegularMostLeast(gossipDynamic, "Target", "Target").orElse(gossipDynamic)))
								.map(gossipsDynamic::createList),
							gossipsDynamic
						)
				)
		);
	}
}
