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
import java.util.function.Predicate;
import net.minecraft.class_9215;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgumentType.ItemStackPredicateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
	private final class_9215 field_48959;

	public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
		this.field_48959 = new class_9215(commandRegistryAccess);
	}

	public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
		return new ItemPredicateArgumentType(commandRegistryAccess);
	}

	public ItemPredicateArgumentType.ItemStackPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		Predicate<ItemStack> predicate = this.field_48959.method_56859(stringReader);
		return predicate::test;
	}

	public static ItemPredicateArgumentType.ItemStackPredicateArgument getItemStackPredicate(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ItemPredicateArgumentType.ItemStackPredicateArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return this.field_48959.method_56860(builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface ItemStackPredicateArgument extends Predicate<ItemStack> {
	}
}
