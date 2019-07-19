package net.minecraft.loot.entry;

import net.minecraft.world.loot.condition.LootCondition;

public class GroupEntry extends CombinedEntry {
	GroupEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootEntrys, lootConditions);
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] children) {
		switch (children.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return children[0];
			case 2:
				EntryCombiner entryCombiner = children[0];
				EntryCombiner entryCombiner2 = children[1];
				return (context, lootChoiceExpander) -> {
					entryCombiner.expand(context, lootChoiceExpander);
					entryCombiner2.expand(context, lootChoiceExpander);
					return true;
				};
			default:
				return (context, lootChoiceExpander) -> {
					for (EntryCombiner entryCombinerx : children) {
						entryCombinerx.expand(context, lootChoiceExpander);
					}

					return true;
				};
		}
	}
}
