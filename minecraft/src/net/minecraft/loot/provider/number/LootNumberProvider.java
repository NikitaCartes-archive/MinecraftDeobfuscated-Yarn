package net.minecraft.loot.provider.number;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

public interface LootNumberProvider extends LootContextAware {
	float nextFloat(LootContext context);

	default int nextInt(LootContext context) {
		return Math.round(this.nextFloat(context));
	}

	LootNumberProviderType getType();
}
