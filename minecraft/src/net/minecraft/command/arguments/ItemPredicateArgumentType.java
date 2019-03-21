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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sortme.ItemStringReader;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgumentType.ItemPredicateFactory> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
	private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.item.tag.unknown", object)
	);

	public static ItemPredicateArgumentType create() {
		return new ItemPredicateArgumentType();
	}

	public ItemPredicateArgumentType.ItemPredicateFactory method_9800(StringReader stringReader) throws CommandSyntaxException {
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, true).consume();
		if (itemStringReader.getItem() != null) {
			ItemPredicateArgumentType.ItemPredicate itemPredicate = new ItemPredicateArgumentType.ItemPredicate(
				itemStringReader.getItem(), itemStringReader.method_9797()
			);
			return commandContext -> itemPredicate;
		} else {
			Identifier identifier = itemStringReader.method_9790();
			return commandContext -> {
				Tag<Item> tag = commandContext.getSource().getMinecraftServer().getTagManager().items().get(identifier);
				if (tag == null) {
					throw UNKNOWN_TAG_EXCEPTION.create(identifier.toString());
				} else {
					return new ItemPredicateArgumentType.TagPredicate(tag, itemStringReader.method_9797());
				}
			};
		}
	}

	public static Predicate<ItemStack> getPredicateArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<ItemPredicateArgumentType.ItemPredicateFactory>getArgument(string, ItemPredicateArgumentType.ItemPredicateFactory.class)
			.create(commandContext);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		ItemStringReader itemStringReader = new ItemStringReader(stringReader, true);

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

	static class ItemPredicate implements Predicate<ItemStack> {
		private final Item item;
		@Nullable
		private final CompoundTag compound;

		public ItemPredicate(Item item, @Nullable CompoundTag compoundTag) {
			this.item = item;
			this.compound = compoundTag;
		}

		public boolean method_9806(ItemStack itemStack) {
			return itemStack.getItem() == this.item && TagHelper.areTagsEqual(this.compound, itemStack.getTag(), true);
		}
	}

	public interface ItemPredicateFactory {
		Predicate<ItemStack> create(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}

	static class TagPredicate implements Predicate<ItemStack> {
		private final Tag<Item> tag;
		@Nullable
		private final CompoundTag compound;

		public TagPredicate(Tag<Item> tag, @Nullable CompoundTag compoundTag) {
			this.tag = tag;
			this.compound = compoundTag;
		}

		public boolean method_9807(ItemStack itemStack) {
			return this.tag.contains(itemStack.getItem()) && TagHelper.areTagsEqual(this.compound, itemStack.getTag(), true);
		}
	}
}
