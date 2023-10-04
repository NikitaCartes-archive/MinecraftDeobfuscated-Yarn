package net.minecraft.text;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public record StorageNbtDataSource(Identifier id) implements NbtDataSource {
	public static final MapCodec<StorageNbtDataSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Identifier.CODEC.fieldOf("storage").forGetter(StorageNbtDataSource::id)).apply(instance, StorageNbtDataSource::new)
	);
	public static final NbtDataSource.Type<StorageNbtDataSource> TYPE = new NbtDataSource.Type<>(CODEC, "storage");

	@Override
	public Stream<NbtCompound> get(ServerCommandSource source) {
		NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
		return Stream.of(nbtCompound);
	}

	@Override
	public NbtDataSource.Type<?> getType() {
		return TYPE;
	}

	public String toString() {
		return "storage=" + this.id;
	}
}
