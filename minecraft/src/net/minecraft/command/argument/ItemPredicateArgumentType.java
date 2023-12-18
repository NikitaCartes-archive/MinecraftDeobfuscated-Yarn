package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgumentType.ItemStackPredicateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
	private final RegistryWrapper<Item> registryWrapper;

	public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
		this.registryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.ITEM);
	}

	public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
		return new ItemPredicateArgumentType(commandRegistryAccess);
	}

	public ItemPredicateArgumentType.ItemStackPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		Either<ItemStringReader.ItemResult, ItemStringReader.TagResult> either = ItemStringReader.itemOrTag(this.registryWrapper, stringReader);
		return either.map(item -> getItemStackPredicate(item2 -> item2.equals(item.item()), item.nbt()), tag -> getItemStackPredicate(tag.tag()::contains, tag.nbt()));
	}

	public static Predicate<ItemStack> getItemStackPredicate(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ItemPredicateArgumentType.ItemStackPredicateArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return ItemStringReader.getSuggestions(this.registryWrapper, builder, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	private static ItemPredicateArgumentType.ItemStackPredicateArgument getItemStackPredicate(Predicate<RegistryEntry<Item>> predicate, @Nullable NbtCompound nbt) {
		return nbt != null ? stack -> stack.itemMatches(predicate) && NbtHelper.matches(nbt, stack.getNbt(), true) : stack -> stack.itemMatches(predicate);
	}

	public interface ItemStackPredicateArgument extends Predicate<ItemStack> {
	}
}
