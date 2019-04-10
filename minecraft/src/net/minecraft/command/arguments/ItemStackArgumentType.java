package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.sortme.ItemStringReader;

public class ItemStackArgumentType implements ArgumentType<ItemStackArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

	public static ItemStackArgumentType create() {
		return new ItemStackArgumentType();
	}

	public ItemStackArgument method_9778(StringReader stringReader) throws CommandSyntaxException {
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
		return new ItemStackArgument(itemStringReader.getItem(), itemStringReader.method_9797());
	}

	public static <S> ItemStackArgument getItemStackArgument(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, ItemStackArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, false);

		try {
			itemStringReader.consume();
		} catch (CommandSyntaxException var6) {
		}

		return itemStringReader.method_9793(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
