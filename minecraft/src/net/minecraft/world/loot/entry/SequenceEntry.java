package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.LootChoiceProvider;
import net.minecraft.world.loot.condition.LootCondition;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootEntrys, lootConditions);
	}

	@Override
	protected LootChoiceProvider combine(LootChoiceProvider[] lootChoiceProviders) {
		switch (lootChoiceProviders.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return lootChoiceProviders[0];
			case 2:
				return lootChoiceProviders[0].and(lootChoiceProviders[1]);
			default:
				return (lootContext, consumer) -> {
					for (LootChoiceProvider lootChoiceProvider : lootChoiceProviders) {
						if (!lootChoiceProvider.expand(lootContext, consumer)) {
							return false;
						}
					}

					return true;
				};
		}
	}
}
