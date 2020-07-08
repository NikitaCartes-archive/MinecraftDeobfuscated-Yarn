package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class RemoveGolemGossipFix extends ChoiceFix {
	public RemoveGolemGossipFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "Remove Golem Gossip Fix", TypeReferences.ENTITY, "minecraft:villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), RemoveGolemGossipFix::updateGossipsList);
	}

	private static Dynamic<?> updateGossipsList(Dynamic<?> villagerData) {
		return villagerData.update(
			"Gossips", dynamic2 -> villagerData.createList(dynamic2.asStream().filter(dynamicx -> !dynamicx.get("Type").asString("").equals("golem")))
		);
	}
}
