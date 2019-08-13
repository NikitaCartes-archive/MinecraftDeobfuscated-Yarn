package net.minecraft.world.loot.entry;

import java.util.function.Consumer;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootEntrys, lootConditions);
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] entryCombiners) {
		switch(entryCombiners.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return entryCombiners[0];
			case 2:
				return entryCombiners[0].and(entryCombiners[1]);
			default:
				return (lootContext, consumer) -> {
					for(EntryCombiner entryCombiner : entryCombiners) {
						if (!entryCombiner.expand(lootContext, consumer)) {
							return false;
						}
					}

					return true;
				};
		}
	}
}
