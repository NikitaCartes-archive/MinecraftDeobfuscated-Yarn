package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface DecoratableArgumentType<T> extends ArgumentType<T> {
	@Nullable
	default CompletableFuture<Text> decorate(ServerCommandSource serverCommandSource, ParsedArgument<ServerCommandSource, ?> parsedArgument) throws CommandSyntaxException {
		return this.getFormatClass().isInstance(parsedArgument.getResult())
			? this.decorate(serverCommandSource, (T)this.getFormatClass().cast(parsedArgument.getResult()))
			: null;
	}

	CompletableFuture<Text> decorate(ServerCommandSource source, T format) throws CommandSyntaxException;

	Class<T> getFormatClass();
}
