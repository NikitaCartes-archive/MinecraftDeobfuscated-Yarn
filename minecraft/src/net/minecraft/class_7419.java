package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface class_7419 {
	Stream<NbtCompound> toNbt(ServerCommandSource serverCommandSource) throws CommandSyntaxException;
}
