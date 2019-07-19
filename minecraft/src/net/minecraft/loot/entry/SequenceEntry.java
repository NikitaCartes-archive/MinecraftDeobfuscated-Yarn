package net.minecraft.loot.entry;

import net.minecraft.world.loot.condition.LootCondition;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
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
				return children[0].and(children[1]);
			default:
				return (context, lootChoiceExpander) -> {
					for (EntryCombiner entryCombiner : children) {
						if (!entryCombiner.expand(context, lootChoiceExpander)) {
							return false;
						}
					}

					return true;
				};
		}
	}
}
