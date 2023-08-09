package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public record StorageLootNbtProvider(Identifier source) implements LootNbtProvider {
	public static final Codec<StorageLootNbtProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Identifier.CODEC.fieldOf("source").forGetter(StorageLootNbtProvider::source)).apply(instance, StorageLootNbtProvider::new)
	);

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.STORAGE;
	}

	@Nullable
	@Override
	public NbtElement getNbt(LootContext context) {
		return context.getWorld().getServer().getDataCommandStorage().get(this.source);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of();
	}
}
