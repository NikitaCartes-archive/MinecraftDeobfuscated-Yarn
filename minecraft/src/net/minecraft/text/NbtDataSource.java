package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

/**
 * A data source for the NBT text content. Unmodifiable.
 */
public interface NbtDataSource {
	MapCodec<NbtDataSource> CODEC = TextCodecs.dispatchingCodec(
		new NbtDataSource.Type[]{EntityNbtDataSource.TYPE, BlockNbtDataSource.TYPE, StorageNbtDataSource.TYPE},
		NbtDataSource.Type::codec,
		NbtDataSource::getType,
		"source"
	);

	Stream<NbtCompound> get(ServerCommandSource source) throws CommandSyntaxException;

	NbtDataSource.Type<?> getType();

	public static record Type<T extends NbtDataSource>(MapCodec<T> codec, String id) implements StringIdentifiable {
		@Override
		public String asString() {
			return this.id;
		}
	}
}
