package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class ColorArgumentType implements ArgumentType<ChatFormat> {
	private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
	public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("argument.color.invalid", object)
	);

	private ColorArgumentType() {
	}

	public static ColorArgumentType create() {
		return new ColorArgumentType();
	}

	public static ChatFormat getColor(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, ChatFormat.class);
	}

	public ChatFormat method_9279(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		ChatFormat chatFormat = ChatFormat.getFormatByName(string);
		if (chatFormat != null && !chatFormat.isModifier()) {
			return chatFormat;
		} else {
			throw INVALID_COLOR_EXCEPTION.create(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(ChatFormat.getNames(true, false), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
