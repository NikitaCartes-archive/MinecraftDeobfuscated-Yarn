package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface DecoratableArgumentType<T> extends ArgumentType<T> {
	@Nullable
	static CompletableFuture<Text> decorate(ArgumentCommandNode<?, ?> node, CommandContextBuilder<ServerCommandSource> builder) throws CommandSyntaxException {
		return node.getType() instanceof DecoratableArgumentType<?> decoratableArgumentType ? decoratableArgumentType.decorate(builder, node.getName()) : null;
	}

	static boolean isDecoratableArgumentNode(CommandNode<?> node) {
		if (node instanceof ArgumentCommandNode<?, ?> argumentCommandNode && argumentCommandNode.getType() instanceof DecoratableArgumentType) {
			return true;
		}

		return false;
	}

	@Nullable
	default CompletableFuture<Text> decorate(CommandContextBuilder<ServerCommandSource> builder, String argumentName) throws CommandSyntaxException {
		ParsedArgument<ServerCommandSource, ?> parsedArgument = (ParsedArgument<ServerCommandSource, ?>)builder.getArguments().get(argumentName);
		return parsedArgument != null && this.getFormatClass().isInstance(parsedArgument.getResult())
			? this.decorate(builder.getSource(), (T)this.getFormatClass().cast(parsedArgument.getResult()))
			: null;
	}

	CompletableFuture<Text> decorate(ServerCommandSource source, T format) throws CommandSyntaxException;

	Class<T> getFormatClass();
}
