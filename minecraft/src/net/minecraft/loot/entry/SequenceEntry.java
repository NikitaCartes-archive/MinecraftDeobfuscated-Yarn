package net.minecraft.loot.entry;

import java.util.function.Consumer;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
		super(lootPoolEntrys, lootConditions);
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.GROUP;
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] children) {
		switch(children.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return children[0];
			case 2:
				EntryCombiner entryCombiner = children[0];
				EntryCombiner entryCombiner2 = children[1];
				return (lootContext, consumer) -> {
					entryCombiner.expand(lootContext, consumer);
					entryCombiner2.expand(lootContext, consumer);
					return true;
				};
			default:
				return (context, lootChoiceExpander) -> {
					for(EntryCombiner entryCombinerxx : children) {
						entryCombinerxx.expand(context, lootChoiceExpander);
					}

					return true;
				};
		}
	}
}
