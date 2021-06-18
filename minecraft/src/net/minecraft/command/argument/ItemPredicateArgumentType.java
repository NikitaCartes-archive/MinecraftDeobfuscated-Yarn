package net.minecraft.command.argument;

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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgumentType.ItemPredicateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
	private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.item.tag.unknown", object)
	);

	public static ItemPredicateArgumentType itemPredicate() {
		return new ItemPredicateArgumentType();
	}

	public ItemPredicateArgumentType.ItemPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, true).consume();
		if (itemStringReader.getItem() != null) {
			ItemPredicateArgumentType.ItemPredicate itemPredicate = new ItemPredicateArgumentType.ItemPredicate(itemStringReader.getItem(), itemStringReader.getNbt());
			return commandContext -> itemPredicate;
		} else {
			Identifier identifier = itemStringReader.getId();
			return commandContext -> {
				Tag<Item> tag = commandContext.getSource()
					.getServer()
					.getTagManager()
					.getTag(Registry.ITEM_KEY, identifier, identifierxx -> UNKNOWN_TAG_EXCEPTION.create(identifierxx.toString()));
				return new ItemPredicateArgumentType.TagPredicate(tag, itemStringReader.getNbt());
			};
		}
	}

	public static Predicate<ItemStack> getItemPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<ItemPredicateArgumentType.ItemPredicateArgument>getArgument(name, ItemPredicateArgumentType.ItemPredicateArgument.class).create(context);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, true);

		try {
			itemStringReader.consume();
		} catch (CommandSyntaxException var6) {
		}

		return itemStringReader.getSuggestions(builder, ItemTags.getTagGroup());
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static class ItemPredicate implements Predicate<ItemStack> {
		private final Item item;
		@Nullable
		private final NbtCompound nbt;

		public ItemPredicate(Item item, @Nullable NbtCompound nbt) {
			this.item = item;
			this.nbt = nbt;
		}

		public boolean test(ItemStack itemStack) {
			return itemStack.isOf(this.item) && NbtHelper.matches(this.nbt, itemStack.getTag(), true);
		}
	}

	public interface ItemPredicateArgument {
		Predicate<ItemStack> create(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}

	static class TagPredicate implements Predicate<ItemStack> {
		private final Tag<Item> tag;
		@Nullable
		private final NbtCompound compound;

		public TagPredicate(Tag<Item> tag, @Nullable NbtCompound nbt) {
			this.tag = tag;
			this.compound = nbt;
		}

		public boolean test(ItemStack itemStack) {
			return itemStack.isIn(this.tag) && NbtHelper.matches(this.compound, itemStack.getTag(), true);
		}
	}
}
