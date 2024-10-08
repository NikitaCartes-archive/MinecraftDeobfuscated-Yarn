package net.minecraft.loot.provider.nbt;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.context.ContextParameter;

public interface LootNbtProvider {
	@Nullable
	NbtElement getNbt(LootContext context);

	Set<ContextParameter<?>> getRequiredParameters();

	LootNbtProviderType getType();
}
