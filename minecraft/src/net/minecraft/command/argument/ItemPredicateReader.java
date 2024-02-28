package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ItemPredicateReader {
	static final DynamicCommandExceptionType INVALID_ID_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("argument.item.id.invalid", id)
	);
	static final DynamicCommandExceptionType INVALID_TAG_EXCEPTION = new DynamicCommandExceptionType(
		tag -> Text.stringifiedTranslatable("arguments.item.tag.unknown", tag)
	);
	static final DynamicCommandExceptionType UNKNOWN_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		type -> Text.stringifiedTranslatable("arguments.item.component.unknown", type)
	);
	static final Dynamic2CommandExceptionType MALFORMED_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType(
		(type, error) -> Text.stringifiedTranslatable("arguments.item.component.malformed", type, error)
	);
	static final SimpleCommandExceptionType EXPECTED_ITEM_COMPONENT_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("arguments.item.component.expected")
	);
	private static final char HASH = '#';
	public static final char SQUARE_OPEN_BRACKET = '[';
	public static final char SQUARE_CLOSED_BRACKET = ']';
	public static final char COMMA = ',';
	public static final char EQUAL_SIGN = '=';
	public static final char CURLY_OPEN_BRACKET = '{';
	static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> DEFAULT_SUGGESTOR = SuggestionsBuilder::buildFuture;
	final RegistryWrapper.Impl<Item> itemRegistry;
	final DynamicOps<NbtElement> nbtOps;
	final boolean allowTags;

	public ItemPredicateReader(RegistryWrapper.WrapperLookup registryLookup, boolean allowTags) {
		this.itemRegistry = registryLookup.getWrapperOrThrow(RegistryKeys.ITEM);
		this.nbtOps = registryLookup.getOps(NbtOps.INSTANCE);
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

		default <T> void onComponent(DataComponentType<T> type, T value) {
		}

		default void setNbt(NbtCompound nbt) {
		}

		default void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
		}
	}

	class Reader {
		private final StringReader reader;
		private final ItemPredicateReader.Callbacks callbacks;
		private boolean readComponents;

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

			this.callbacks.setSuggestor(this::suggestBracket);
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.readComponents();
				this.readComponents = true;
			}

			this.callbacks.setSuggestor(this::suggestBracket);
			if (this.reader.canRead() && this.reader.peek() == '{') {
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

		private void readComponents() throws CommandSyntaxException {
			this.reader.expect('[');
			this.callbacks.setSuggestor(this::suggestComponentType);

			while (this.reader.canRead() && this.reader.peek() != ']') {
				this.reader.skipWhitespace();
				DataComponentType<?> dataComponentType = readComponentType(this.reader);
				this.callbacks.setSuggestor(this::suggestEqual);
				this.reader.skipWhitespace();
				this.reader.expect('=');
				this.callbacks.setSuggestor(ItemPredicateReader.DEFAULT_SUGGESTOR);
				this.reader.skipWhitespace();
				this.readComponentValue(dataComponentType);
				this.reader.skipWhitespace();
				this.callbacks.setSuggestor(this::suggestEndOfComponent);
				if (!this.reader.canRead() || this.reader.peek() != ',') {
					break;
				}

				this.reader.skip();
				this.reader.skipWhitespace();
				this.callbacks.setSuggestor(this::suggestComponentType);
				if (!this.reader.canRead()) {
					throw ItemPredicateReader.EXPECTED_ITEM_COMPONENT_EXCEPTION.createWithContext(this.reader);
				}
			}

			this.reader.expect(']');
			this.callbacks.setSuggestor(ItemPredicateReader.DEFAULT_SUGGESTOR);
		}

		public static DataComponentType<?> readComponentType(StringReader reader) throws CommandSyntaxException {
			if (!reader.canRead()) {
				throw ItemPredicateReader.EXPECTED_ITEM_COMPONENT_EXCEPTION.createWithContext(reader);
			} else {
				int i = reader.getCursor();
				Identifier identifier = Identifier.fromCommandInput(reader);
				DataComponentType<?> dataComponentType = Registries.DATA_COMPONENT_TYPE.get(identifier);
				if (dataComponentType != null && !dataComponentType.shouldSkipSerialization()) {
					return dataComponentType;
				} else {
					reader.setCursor(i);
					throw ItemPredicateReader.UNKNOWN_COMPONENT_EXCEPTION.createWithContext(reader, identifier);
				}
			}
		}

		private <T> void readComponentValue(DataComponentType<T> type) throws CommandSyntaxException {
			int i = this.reader.getCursor();
			NbtElement nbtElement = new StringNbtReader(this.reader).parseElement();
			DataResult<T> dataResult = type.getCodecOrThrow().parse(ItemPredicateReader.this.nbtOps, nbtElement);
			this.callbacks.onComponent(type, Util.getResult(dataResult, error -> {
				this.reader.setCursor(i);
				return ItemPredicateReader.MALFORMED_COMPONENT_EXCEPTION.createWithContext(this.reader, type.toString(), error);
			}));
		}

		private void readNbt() throws CommandSyntaxException {
			this.callbacks.setSuggestor(ItemPredicateReader.DEFAULT_SUGGESTOR);
			this.callbacks.setNbt(new StringNbtReader(this.reader).parseCompound());
		}

		private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder) {
			if (builder.getRemaining().isEmpty()) {
				if (!this.readComponents) {
					builder.suggest(String.valueOf('['));
				}

				builder.suggest(String.valueOf('{'));
			}

			return builder.buildFuture();
		}

		private CompletableFuture<Suggestions> suggestEndOfComponent(SuggestionsBuilder builder) {
			if (builder.getRemaining().isEmpty()) {
				builder.suggest(String.valueOf(','));
				builder.suggest(String.valueOf(']'));
			}

			return builder.buildFuture();
		}

		private CompletableFuture<Suggestions> suggestEqual(SuggestionsBuilder builder) {
			if (builder.getRemaining().isEmpty()) {
				builder.suggest(String.valueOf('='));
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

		private CompletableFuture<Suggestions> suggestComponentType(SuggestionsBuilder builder) {
			String string = builder.getRemaining().toLowerCase(Locale.ROOT);
			CommandSource.forEachMatching(Registries.DATA_COMPONENT_TYPE.getEntrySet(), string, entry -> ((RegistryKey)entry.getKey()).getValue(), entry -> {
				DataComponentType<?> dataComponentType = (DataComponentType<?>)entry.getValue();
				if (dataComponentType.getCodec() != null) {
					Identifier identifier = ((RegistryKey)entry.getKey()).getValue();
					builder.suggest(identifier.toString() + "=");
				}
			});
			return builder.buildFuture();
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
