package net.minecraft.text;

import java.util.stream.Stream;
import net.minecraft.class_7419;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public record StorageNbtText(Identifier id) implements class_7419 {
	@Override
	public Stream<NbtCompound> toNbt(ServerCommandSource serverCommandSource) {
		NbtCompound nbtCompound = serverCommandSource.getServer().getDataCommandStorage().get(this.id);
		return Stream.of(nbtCompound);
	}

	public String toString() {
		return "storage=" + this.id;
	}
}
