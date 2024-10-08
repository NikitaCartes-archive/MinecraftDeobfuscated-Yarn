package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;

public record StorageLootNbtProvider(Identifier source) implements LootNbtProvider {
	public static final MapCodec<StorageLootNbtProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Identifier.CODEC.fieldOf("source").forGetter(StorageLootNbtProvider::source)).apply(instance, StorageLootNbtProvider::new)
	);

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.STORAGE;
	}

	@Override
	public NbtElement getNbt(LootContext context) {
		return context.getWorld().getServer().getDataCommandStorage().get(this.source);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of();
	}
}
