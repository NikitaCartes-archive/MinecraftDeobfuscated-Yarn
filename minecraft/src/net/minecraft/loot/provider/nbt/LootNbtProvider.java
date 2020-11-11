package net.minecraft.loot.provider.nbt;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.Tag;

public interface LootNbtProvider {
	@Nullable
	Tag method_32440(LootContext lootContext);

	Set<LootContextParameter<?>> method_32441();

	LootNbtProviderType getType();
}
