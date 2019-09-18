package net.minecraft.world.loot.entry;

import net.minecraft.class_4570;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootEntry[] lootEntrys, class_4570[] args) {
		super(lootEntrys, args);
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] entryCombiners) {
		switch (entryCombiners.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return entryCombiners[0];
			case 2:
				return entryCombiners[0].and(entryCombiners[1]);
			default:
				return (lootContext, consumer) -> {
					for (EntryCombiner entryCombiner : entryCombiners) {
						if (!entryCombiner.expand(lootContext, consumer)) {
							return false;
						}
					}

					return true;
				};
		}
	}
}
