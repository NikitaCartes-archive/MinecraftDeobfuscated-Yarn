package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemPredicateReader {
	static final DynamicCommandExceptionType INVALID_ID_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("argument.item.id.invalid", id)
	);
	static final DynamicCommandExceptionType INVALID_TAG_EXCEPTION = new DynamicCommandExceptionType(
		tag -> Text.stringifiedTranslatable("arguments.item.tag.unknown", tag)
	);
	private static final char HASH = '#';
	private static final char CURLY_OPEN_BRACKET = '{';
	static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> DEFAULT_SUGGESTOR = SuggestionsBuilder::buildFuture;
	final RegistryWrapper.Impl<Item> itemRegistry;
	final boolean allowTags;

	public ItemPredicateReader(RegistryWrapper.WrapperLookup registryLookup, boolean allowTags) {
		this.itemRegistry = registryLookup.getWrapperOrThrow(RegistryKeys.ITEM);
		this.allowTags = allowTags;
	}

	public void read(StringReader reader, ItemPredicateReader.Callbacks callbacks) throws CommandSyntaxException {
		int i = reader.getCursor();

		try {
			new ItemPredicateReader.Reader(reader, callbacks).suggest();
		} catch (CommandSyntaxException var5) {
			reader.setCursor(i);
			throw var5;
		}
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ItemPredicateReader.SuggestionCallbacks suggestionCallbacks = new ItemPredicateReader.SuggestionCallbacks();
		ItemPredicateReader.Reader reader = new ItemPredicateReader.Reader(stringReader, suggestionCallbacks);

		try {
			reader.suggest();
		} catch (CommandSyntaxException var6) {
		}

		return suggestionCallbacks.getSuggestions(builder, stringReader);
	}

	public interface Callbacks {
		default void onItem(RegistryEntry<Item> item) {
		}

		default void onTag(RegistryEntryList<Item> tag) {
		}

		default void setNbt(NbtCompound nbt) {
		}

		default void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
		}
	}

	class Reader {
		private final StringReader reader;
		private final ItemPredicateReader.Callbacks callbacks;

		Reader(StringReader reader, ItemPredicateReader.Callbacks callbacks) {
			this.reader = reader;
			this.callbacks = callbacks;
		}

		public void suggest() throws CommandSyntaxException {
			this.callbacks.setSuggestor(ItemPredicateReader.this.allowTags ? this::suggestAll : this::suggestItems);
			if (ItemPredicateReader.this.allowTags && this.reader.canRead() && this.reader.peek() == '#') {
				this.readTag();
			} else {
				this.readItem();
			}

			this.callbacks.setSuggestor(this::suggestCurlyBraceIfPossible);
			if (this.reader.canRead() && this.reader.peek() == '{') {
				this.callbacks.setSuggestor(ItemPredicateReader.DEFAULT_SUGGESTOR);
				this.readNbt();
			}
		}

		private void readItem() throws CommandSyntaxException {
			int i = this.reader.getCursor();
			Identifier identifier = Identifier.fromCommandInput(this.reader);
			this.callbacks
				.onItem((RegistryEntry<Item>)ItemPredicateReader.this.itemRegistry.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
					this.reader.setCursor(i);
					return ItemPredicateReader.INVALID_ID_EXCEPTION.createWithContext(this.reader, identifier);
				}));
		}

		private void readTag() throws CommandSyntaxException {
			int i = this.reader.getCursor();
			this.reader.expect('#');
			this.callbacks.setSuggestor(this::suggestTags);
			Identifier identifier = Identifier.fromCommandInput(this.reader);
			RegistryEntryList<Item> registryEntryList = (RegistryEntryList<Item>)ItemPredicateReader.this.itemRegistry
				.getOptional(TagKey.of(RegistryKeys.ITEM, identifier))
				.orElseThrow(() -> {
					this.reader.setCursor(i);
					return ItemPredicateReader.INVALID_TAG_EXCEPTION.createWithContext(this.reader, identifier);
				});
			this.callbacks.onTag(registryEntryList);
		}

		private void readNbt() throws CommandSyntaxException {
			this.callbacks.setSuggestor(ItemPredicateReader.DEFAULT_SUGGESTOR);
			this.callbacks.setNbt(new StringNbtReader(this.reader).parseCompound());
		}

		private CompletableFuture<Suggestions> suggestCurlyBraceIfPossible(SuggestionsBuilder builder) {
			if (builder.getRemaining().isEmpty()) {
				builder.suggest(String.valueOf('{'));
			}

			return builder.buildFuture();
		}

		private CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder builder) {
			return CommandSource.suggestIdentifiers(ItemPredicateReader.this.itemRegistry.streamKeys().map(RegistryKey::getValue), builder);
		}

		private CompletableFuture<Suggestions> suggestTags(SuggestionsBuilder builder) {
			return CommandSource.suggestIdentifiers(ItemPredicateReader.this.itemRegistry.streamTagKeys().map(TagKey::id), builder, String.valueOf('#'));
		}

		private CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder) {
			this.suggestTags(builder);
			return this.suggestItems(builder);
		}
	}

	static class SuggestionCallbacks implements ItemPredicateReader.Callbacks {
		private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor = ItemPredicateReader.DEFAULT_SUGGESTOR;

		@Override
		public void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
			this.suggestor = suggestor;
		}

		public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, StringReader reader) {
			return (CompletableFuture<Suggestions>)this.suggestor.apply(builder.createOffset(reader.getCursor()));
		}
	}
}
