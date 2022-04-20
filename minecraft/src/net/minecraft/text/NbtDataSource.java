package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;

/**
 * A data source for the NBT text content. Unmodifiable.
 */
@FunctionalInterface
public interface NbtDataSource {
	Stream<NbtCompound> get(ServerCommandSource source) throws CommandSyntaxException;
}
