package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class RemoveGolemGossipFix extends ChoiceFix {
	public RemoveGolemGossipFix(Schema outputSchema, boolean changesTyped) {
		super(outputSchema, changesTyped, "Remove Golem Gossip Fix", TypeReferences.ENTITY, "minecraft:villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), RemoveGolemGossipFix::updateGossipsList);
	}

	private static Dynamic<?> updateGossipsList(Dynamic<?> villagerData) {
		return villagerData.update(
			"Gossips",
			gossipsDynamic -> villagerData.createList(gossipsDynamic.asStream().filter(gossipDynamic -> !gossipDynamic.get("Type").asString("").equals("golem")))
		);
	}
}
