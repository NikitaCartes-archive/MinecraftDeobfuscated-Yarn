package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.registry.Registry;

public class ItemStackArgumentType implements ArgumentType<ItemStackArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

	public static ItemStackArgumentType itemStack() {
		return new ItemStackArgumentType();
	}

	public ItemStackArgument parse(StringReader stringReader) throws CommandSyntaxException {
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
		return new ItemStackArgument(itemStringReader.getItem(), itemStringReader.getNbt());
	}

	public static <S> ItemStackArgument getItemStackArgument(CommandContext<S> context, String name) {
		return context.getArgument(name, ItemStackArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, false);

		try {
			itemStringReader.consume();
		} catch (CommandSyntaxException var6) {
		}

		return itemStringReader.getSuggestions(builder, Registry.ITEM);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
