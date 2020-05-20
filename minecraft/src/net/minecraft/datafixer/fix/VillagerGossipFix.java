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
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(),
			dynamic -> dynamic.update(
					"Gossips",
					dynamicx -> DataFixUtils.orElse(
							dynamicx.asStreamOpt()
								.result()
								.map(stream -> stream.map(dynamicxx -> (Dynamic)AbstractUuidFix.updateRegularMostLeast(dynamicxx, "Target", "Target").orElse(dynamicxx)))
								.map(dynamicx::createList),
							dynamicx
						)
				)
		);
	}
}
