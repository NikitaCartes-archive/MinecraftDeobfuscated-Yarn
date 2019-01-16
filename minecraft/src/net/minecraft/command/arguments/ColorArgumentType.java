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
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

public class ColorArgumentType implements ArgumentType<TextFormat> {
	private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
	public static final DynamicCommandExceptionType INVALID_COLOR_EXCPETION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.color.invalid", object)
	);

	private ColorArgumentType() {
	}

	public static ColorArgumentType create() {
		return new ColorArgumentType();
	}

	public static TextFormat getColorArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, TextFormat.class);
	}

	public TextFormat method_9279(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		TextFormat textFormat = TextFormat.getFormatByName(string);
		if (textFormat != null && !textFormat.isModifier()) {
			return textFormat;
		} else {
			throw INVALID_COLOR_EXCPETION.create(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(TextFormat.method_540(true, false), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
