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
import net.minecraft.command.CommandRegistryAccess;

public class ItemStackArgumentType implements ArgumentType<ItemStackArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
	private final ItemStringReader field_48954;

	public ItemStackArgumentType(CommandRegistryAccess commandRegistryAccess) {
		this.field_48954 = new ItemStringReader(commandRegistryAccess);
	}

	public static ItemStackArgumentType itemStack(CommandRegistryAccess commandRegistryAccess) {
		return new ItemStackArgumentType(commandRegistryAccess);
	}

	public ItemStackArgument parse(StringReader stringReader) throws CommandSyntaxException {
		ItemStringReader.ItemResult itemResult = this.field_48954.consume(stringReader);
		return new ItemStackArgument(itemResult.item(), itemResult.nbt());
	}

	public static <S> ItemStackArgument getItemStackArgument(CommandContext<S> context, String name) {
		return context.getArgument(name, ItemStackArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return this.field_48954.getSuggestions(builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
