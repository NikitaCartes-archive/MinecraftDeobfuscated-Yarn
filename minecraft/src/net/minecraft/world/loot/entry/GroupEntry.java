package net.minecraft.world.loot.entry;

import net.minecraft.class_4570;

public class GroupEntry extends CombinedEntry {
	GroupEntry(LootEntry[] lootEntrys, class_4570[] args) {
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
				EntryCombiner entryCombiner = entryCombiners[0];
				EntryCombiner entryCombiner2 = entryCombiners[1];
				return (lootContext, consumer) -> {
					entryCombiner.expand(lootContext, consumer);
					entryCombiner2.expand(lootContext, consumer);
					return true;
				};
			default:
				return (lootContext, consumer) -> {
					for (EntryCombiner entryCombinerx : entryCombiners) {
						entryCombinerx.expand(lootContext, consumer);
					}

					return true;
				};
		}
	}
}
