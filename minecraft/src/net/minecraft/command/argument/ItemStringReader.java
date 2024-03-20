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
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemStringReader {
	static final DynamicCommandExceptionType INVALID_ITEM_ID_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("argument.item.id.invalid", id)
	);
	static final DynamicCommandExceptionType UNKNOWN_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("arguments.item.component.unknown", id)
	);
	static final Dynamic2CommandExceptionType MALFORMED_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType(
		(type, error) -> Text.stringifiedTranslatable("arguments.item.component.malformed", type, error)
	);
	static final SimpleCommandExceptionType COMPONENT_EXPECTED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.item.component.expected"));
	static final DynamicCommandExceptionType REPEATED_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		type -> Text.stringifiedTranslatable("arguments.item.component.repeated", type)
	);
	public static final char OPEN_SQUARE_BRACKET = '[';
	public static final char CLOSED_SQUARE_BRACKET = ']';
	public static final char COMMA = ',';
	public static final char EQUAL_SIGN = '=';
	static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
	final RegistryWrapper.Impl<Item> itemRegistry;
	final DynamicOps<NbtElement> nbtOps;

	public ItemStringReader(RegistryWrapper.WrapperLookup registriesLookup) {
		this.itemRegistry = registriesLookup.getWrapperOrThrow(RegistryKeys.ITEM);
		this.nbtOps = registriesLookup.getOps(NbtOps.INSTANCE);
	}

	public ItemStringReader.ItemResult consume(StringReader reader) throws CommandSyntaxException {
		final MutableObject<RegistryEntry<Item>> mutableObject = new MutableObject<>();
		final ComponentMap.Builder builder = ComponentMap.builder();
		this.consume(reader, new ItemStringReader.Callbacks() {
			@Override
			public void onItem(RegistryEntry<Item> item) {
				mutableObject.setValue(item);
			}

			@Override
			public <T> void onComponent(DataComponentType<T> type, T value) {
				builder.add(type, value);
			}
		});
		return new ItemStringReader.ItemResult((RegistryEntry<Item>)Objects.requireNonNull(mutableObject.getValue(), "Parser gave no item"), builder.build());
	}

	public void consume(StringReader reader, ItemStringReader.Callbacks callbacks) throws CommandSyntaxException {
		int i = reader.getCursor();

		try {
			new ItemStringReader.Reader(reader, callbacks).read();
		} catch (CommandSyntaxException var5) {
			reader.setCursor(i);
			throw var5;
		}
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ItemStringReader.SuggestionCallbacks suggestionCallbacks = new ItemStringReader.SuggestionCallbacks();
		ItemStringReader.Reader reader = new ItemStringReader.Reader(stringReader, suggestionCallbacks);

		try {
			reader.read();
		} catch (CommandSyntaxException var6) {
		}

		return suggestionCallbacks.getSuggestions(builder, stringReader);
	}

	public interface Callbacks {
		default void onItem(RegistryEntry<Item> item) {
		}

		default <T> void onComponent(DataComponentType<T> type, T value) {
		}

		default void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
		}
	}

	public static record ItemResult(RegistryEntry<Item> item, ComponentMap components) {
	}

	class Reader {
		private final StringReader reader;
		private final ItemStringReader.Callbacks callbacks;

		Reader(StringReader reader, ItemStringReader.Callbacks callbacks) {
			this.reader = reader;
			this.callbacks = callbacks;
		}

		public void read() throws CommandSyntaxException {
			this.callbacks.setSuggestor(this::suggestItems);
			this.readItem();
			this.callbacks.setSuggestor(this::suggestBracket);
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.callbacks.setSuggestor(ItemStringReader.SUGGEST_DEFAULT);
				this.readComponents();
			}
		}

		private void readItem() throws CommandSyntaxException {
			int i = this.reader.getCursor();
			Identifier identifier = Identifier.fromCommandInput(this.reader);
			this.callbacks.onItem((RegistryEntry<Item>)ItemStringReader.this.itemRegistry.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
				this.reader.setCursor(i);
				return ItemStringReader.INVALID_ITEM_ID_EXCEPTION.createWithContext(this.reader, identifier);
			}));
		}

		private void readComponents() throws CommandSyntaxException {
			this.reader.expect('[');
			this.callbacks.setSuggestor(this::suggestComponentType);
			Set<DataComponentType<?>> set = new ReferenceArraySet<>();

			while (this.reader.canRead() && this.reader.peek() != ']') {
				this.reader.skipWhitespace();
				DataComponentType<?> dataComponentType = readComponentType(this.reader);
				if (!set.add(dataComponentType)) {
					throw ItemStringReader.REPEATED_COMPONENT_EXCEPTION.create(dataComponentType);
				}

				this.callbacks.setSuggestor(this::suggestEqual);
				this.reader.skipWhitespace();
				this.reader.expect('=');
				this.callbacks.setSuggestor(ItemStringReader.SUGGEST_DEFAULT);
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
					throw ItemStringReader.COMPONENT_EXPECTED_EXCEPTION.createWithContext(this.reader);
				}
			}

			this.reader.expect(']');
			this.callbacks.setSuggestor(ItemStringReader.SUGGEST_DEFAULT);
		}

		public static DataComponentType<?> readComponentType(StringReader reader) throws CommandSyntaxException {
			if (!reader.canRead()) {
				throw ItemStringReader.COMPONENT_EXPECTED_EXCEPTION.createWithContext(reader);
			} else {
				int i = reader.getCursor();
				Identifier identifier = Identifier.fromCommandInput(reader);
				DataComponentType<?> dataComponentType = Registries.DATA_COMPONENT_TYPE.get(identifier);
				if (dataComponentType != null && !dataComponentType.shouldSkipSerialization()) {
					return dataComponentType;
				} else {
					reader.setCursor(i);
					throw ItemStringReader.UNKNOWN_COMPONENT_EXCEPTION.createWithContext(reader, identifier);
				}
			}
		}

		private <T> void readComponentValue(DataComponentType<T> type) throws CommandSyntaxException {
			int i = this.reader.getCursor();
			NbtElement nbtElement = new StringNbtReader(this.reader).parseElement();
			DataResult<T> dataResult = type.getCodecOrThrow().parse(ItemStringReader.this.nbtOps, nbtElement);
			this.callbacks.onComponent(type, Util.getResult(dataResult, error -> {
				this.reader.setCursor(i);
				return ItemStringReader.MALFORMED_COMPONENT_EXCEPTION.createWithContext(this.reader, type.toString(), error);
			}));
		}

		private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder) {
			if (builder.getRemaining().isEmpty()) {
				builder.suggest(String.valueOf('['));
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
			return CommandSource.suggestIdentifiers(ItemStringReader.this.itemRegistry.streamKeys().map(RegistryKey::getValue), builder);
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

	static class SuggestionCallbacks implements ItemStringReader.Callbacks {
		private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor = ItemStringReader.SUGGEST_DEFAULT;

		@Override
		public void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
			this.suggestor = suggestor;
		}

		public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, StringReader reader) {
			return (CompletableFuture<Suggestions>)this.suggestor.apply(builder.createOffset(reader.getCursor()));
		}
	}
}
