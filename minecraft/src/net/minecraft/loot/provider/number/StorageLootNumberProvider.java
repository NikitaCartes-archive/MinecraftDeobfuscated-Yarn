package net.minecraft.loot.provider.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public record StorageLootNumberProvider(Identifier storage, NbtPathArgumentType.NbtPath path) implements LootNumberProvider {
	public static final MapCodec<StorageLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("storage").forGetter(StorageLootNumberProvider::storage),
					NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageLootNumberProvider::path)
				)
				.apply(instance, StorageLootNumberProvider::new)
	);

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.STORAGE;
	}

	private Optional<AbstractNbtNumber> getNumber(LootContext context) {
		NbtCompound nbtCompound = context.getWorld().getServer().getDataCommandStorage().get(this.storage);

		try {
			List<NbtElement> list = this.path.get(nbtCompound);
			if (list.size() == 1 && list.get(0) instanceof AbstractNbtNumber abstractNbtNumber) {
				return Optional.of(abstractNbtNumber);
			}
		} catch (CommandSyntaxException var6) {
		}

		return Optional.empty();
	}

	@Override
	public float nextFloat(LootContext context) {
		return (Float)this.getNumber(context).map(AbstractNbtNumber::floatValue).orElse(0.0F);
	}

	@Override
	public int nextInt(LootContext context) {
		return (Integer)this.getNumber(context).map(AbstractNbtNumber::intValue).orElse(0);
	}
}
