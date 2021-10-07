package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockArgumentParser {
	public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.block.tag.disallowed"));
	public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(
		block -> new TranslatableText("argument.block.id.invalid", block)
	);
	public static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(block, property) -> new TranslatableText("argument.block.property.unknown", block, property)
	);
	public static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(block, property) -> new TranslatableText("argument.block.property.duplicate", property, block)
	);
	public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType(
		(block, property, value) -> new TranslatableText("argument.block.property.invalid", block, value, property)
	);
	public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(block, property) -> new TranslatableText("argument.block.property.novalue", block, property)
	);
	public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.block.property.unclosed")
	);
	private static final char PROPERTIES_OPENING = '[';
	private static final char NBT_OPENING = '{';
	private static final char PROPERTIES_CLOSING = ']';
	private static final char PROPERTY_DEFINER = '=';
	private static final char PROPERTY_SEPARATOR = ',';
	private static final char TAG_PREFIX = '#';
	private static final BiFunction<SuggestionsBuilder, TagGroup<Block>, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = (builder, tagGroup) -> builder.buildFuture();
	private final StringReader reader;
	private final boolean allowTag;
	private final Map<Property<?>, Comparable<?>> blockProperties = Maps.<Property<?>, Comparable<?>>newHashMap();
	private final Map<String, String> tagProperties = Maps.<String, String>newHashMap();
	private Identifier blockId = new Identifier("");
	private StateManager<Block, BlockState> stateFactory;
	private BlockState blockState;
	@Nullable
	private NbtCompound data;
	private Identifier tagId = new Identifier("");
	private int cursorPos;
	private BiFunction<SuggestionsBuilder, TagGroup<Block>, CompletableFuture<Suggestions>> suggestions = SUGGEST_DEFAULT;

	public BlockArgumentParser(StringReader reader, boolean allowTag) {
		this.reader = reader;
		this.allowTag = allowTag;
	}

	public Map<Property<?>, Comparable<?>> getBlockProperties() {
		return this.blockProperties;
	}

	@Nullable
	public BlockState getBlockState() {
		return this.blockState;
	}

	@Nullable
	public NbtCompound getNbtData() {
		return this.data;
	}

	@Nullable
	public Identifier getTagId() {
		return this.tagId;
	}

	public BlockArgumentParser parse(boolean allowNbt) throws CommandSyntaxException {
		this.suggestions = this::suggestBlockOrTagId;
		if (this.reader.canRead() && this.reader.peek() == '#') {
			this.parseTagId();
			this.suggestions = this::suggestSnbtOrTagProperties;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.parseTagProperties();
				this.suggestions = this::suggestSnbt;
			}
		} else {
			this.parseBlockId();
			this.suggestions = this::suggestSnbtOrBlockProperties;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.parseBlockProperties();
				this.suggestions = this::suggestSnbt;
			}
		}

		if (allowNbt && this.reader.canRead() && this.reader.peek() == '{') {
			this.suggestions = SUGGEST_DEFAULT;
			this.parseSnbt();
		}

		return this;
	}

	private CompletableFuture<Suggestions> suggestBlockPropertiesOrEnd(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf(']'));
		}

		return this.suggestBlockProperties(builder, tagGroup);
	}

	private CompletableFuture<Suggestions> suggestTagPropertiesOrEnd(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf(']'));
		}

		return this.suggestTagProperties(builder, tagGroup);
	}

	private CompletableFuture<Suggestions> suggestBlockProperties(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (Property<?> property : this.blockState.getProperties()) {
			if (!this.blockProperties.containsKey(property) && property.getName().startsWith(string)) {
				builder.suggest(property.getName() + "=");
			}
		}

		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestTagProperties(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		String string = builder.getRemaining().toLowerCase(Locale.ROOT);
		if (this.tagId != null && !this.tagId.getPath().isEmpty()) {
			Tag<Block> tag = tagGroup.getTag(this.tagId);
			if (tag != null) {
				for (Block block : tag.values()) {
					for (Property<?> property : block.getStateManager().getProperties()) {
						if (!this.tagProperties.containsKey(property.getName()) && property.getName().startsWith(string)) {
							builder.suggest(property.getName() + "=");
						}
					}
				}
			}
		}

		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestSnbt(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty() && this.hasBlockEntity(tagGroup)) {
			builder.suggest(String.valueOf('{'));
		}

		return builder.buildFuture();
	}

	private boolean hasBlockEntity(TagGroup<Block> tagGroup) {
		if (this.blockState != null) {
			return this.blockState.hasBlockEntity();
		} else {
			if (this.tagId != null) {
				Tag<Block> tag = tagGroup.getTag(this.tagId);
				if (tag != null) {
					for (Block block : tag.values()) {
						if (block.getDefaultState().hasBlockEntity()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private CompletableFuture<Suggestions> suggestEqualsCharacter(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf('='));
		}

		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf(']'));
		}

		if (builder.getRemaining().isEmpty() && this.blockProperties.size() < this.blockState.getProperties().size()) {
			builder.suggest(String.valueOf(','));
		}

		return builder.buildFuture();
	}

	private static <T extends Comparable<T>> SuggestionsBuilder suggestPropertyValues(SuggestionsBuilder builder, Property<T> property) {
		for (T comparable : property.getValues()) {
			if (comparable instanceof Integer) {
				builder.suggest((Integer)comparable);
			} else {
				builder.suggest(property.name(comparable));
			}
		}

		return builder;
	}

	private CompletableFuture<Suggestions> suggestTagPropertyValues(SuggestionsBuilder builder, TagGroup<Block> tagGroup, String propertyName) {
		boolean bl = false;
		if (this.tagId != null && !this.tagId.getPath().isEmpty()) {
			Tag<Block> tag = tagGroup.getTag(this.tagId);
			if (tag != null) {
				for (Block block : tag.values()) {
					Property<?> property = block.getStateManager().getProperty(propertyName);
					if (property != null) {
						suggestPropertyValues(builder, property);
					}

					if (!bl) {
						for (Property<?> property2 : block.getStateManager().getProperties()) {
							if (!this.tagProperties.containsKey(property2.getName())) {
								bl = true;
								break;
							}
						}
					}
				}
			}
		}

		if (bl) {
			builder.suggest(String.valueOf(','));
		}

		builder.suggest(String.valueOf(']'));
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestSnbtOrTagProperties(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			Tag<Block> tag = tagGroup.getTag(this.tagId);
			if (tag != null) {
				boolean bl = false;
				boolean bl2 = false;

				for (Block block : tag.values()) {
					bl |= !block.getStateManager().getProperties().isEmpty();
					bl2 |= block.getDefaultState().hasBlockEntity();
					if (bl && bl2) {
						break;
					}
				}

				if (bl) {
					builder.suggest(String.valueOf('['));
				}

				if (bl2) {
					builder.suggest(String.valueOf('{'));
				}
			}
		}

		return this.suggestIdentifiers(builder, tagGroup);
	}

	private CompletableFuture<Suggestions> suggestSnbtOrBlockProperties(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (builder.getRemaining().isEmpty()) {
			if (!this.blockState.getBlock().getStateManager().getProperties().isEmpty()) {
				builder.suggest(String.valueOf('['));
			}

			if (this.blockState.hasBlockEntity()) {
				builder.suggest(String.valueOf('{'));
			}
		}

		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestIdentifiers(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		return CommandSource.suggestIdentifiers(tagGroup.getTagIds(), builder.createOffset(this.cursorPos).add(builder));
	}

	private CompletableFuture<Suggestions> suggestBlockOrTagId(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		if (this.allowTag) {
			CommandSource.suggestIdentifiers(tagGroup.getTagIds(), builder, String.valueOf('#'));
		}

		CommandSource.suggestIdentifiers(Registry.BLOCK.getIds(), builder);
		return builder.buildFuture();
	}

	public void parseBlockId() throws CommandSyntaxException {
		int i = this.reader.getCursor();
		this.blockId = Identifier.fromCommandInput(this.reader);
		Block block = (Block)Registry.BLOCK.getOrEmpty(this.blockId).orElseThrow(() -> {
			this.reader.setCursor(i);
			return INVALID_BLOCK_ID_EXCEPTION.createWithContext(this.reader, this.blockId.toString());
		});
		this.stateFactory = block.getStateManager();
		this.blockState = block.getDefaultState();
	}

	public void parseTagId() throws CommandSyntaxException {
		if (!this.allowTag) {
			throw DISALLOWED_TAG_EXCEPTION.create();
		} else {
			this.suggestions = this::suggestIdentifiers;
			this.reader.expect('#');
			this.cursorPos = this.reader.getCursor();
			this.tagId = Identifier.fromCommandInput(this.reader);
		}
	}

	public void parseBlockProperties() throws CommandSyntaxException {
		this.reader.skip();
		this.suggestions = this::suggestBlockPropertiesOrEnd;
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != ']') {
			this.reader.skipWhitespace();
			int i = this.reader.getCursor();
			String string = this.reader.readString();
			Property<?> property = this.stateFactory.getProperty(string);
			if (property == null) {
				this.reader.setCursor(i);
				throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
			}

			if (this.blockProperties.containsKey(property)) {
				this.reader.setCursor(i);
				throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
			}

			this.reader.skipWhitespace();
			this.suggestions = this::suggestEqualsCharacter;
			if (!this.reader.canRead() || this.reader.peek() != '=') {
				throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
			}

			this.reader.skip();
			this.reader.skipWhitespace();
			this.suggestions = (builder, tagGroup) -> suggestPropertyValues(builder, property).buildFuture();
			int j = this.reader.getCursor();
			this.parsePropertyValue(property, this.reader.readString(), j);
			this.suggestions = this::suggestCommaOrEnd;
			this.reader.skipWhitespace();
			if (this.reader.canRead()) {
				if (this.reader.peek() != ',') {
					if (this.reader.peek() != ']') {
						throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
					}
					break;
				}

				this.reader.skip();
				this.suggestions = this::suggestBlockProperties;
			}
		}

		if (this.reader.canRead()) {
			this.reader.skip();
		} else {
			throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
		}
	}

	public void parseTagProperties() throws CommandSyntaxException {
		this.reader.skip();
		this.suggestions = this::suggestTagPropertiesOrEnd;
		int i = -1;
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != ']') {
			this.reader.skipWhitespace();
			int j = this.reader.getCursor();
			String string = this.reader.readString();
			if (this.tagProperties.containsKey(string)) {
				this.reader.setCursor(j);
				throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
			}

			this.reader.skipWhitespace();
			if (!this.reader.canRead() || this.reader.peek() != '=') {
				this.reader.setCursor(j);
				throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
			}

			this.reader.skip();
			this.reader.skipWhitespace();
			this.suggestions = (builder, tagGroup) -> this.suggestTagPropertyValues(builder, tagGroup, string);
			i = this.reader.getCursor();
			String string2 = this.reader.readString();
			this.tagProperties.put(string, string2);
			this.reader.skipWhitespace();
			if (this.reader.canRead()) {
				i = -1;
				if (this.reader.peek() != ',') {
					if (this.reader.peek() != ']') {
						throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
					}
					break;
				}

				this.reader.skip();
				this.suggestions = this::suggestTagProperties;
			}
		}

		if (this.reader.canRead()) {
			this.reader.skip();
		} else {
			if (i >= 0) {
				this.reader.setCursor(i);
			}

			throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
		}
	}

	public void parseSnbt() throws CommandSyntaxException {
		this.data = new StringNbtReader(this.reader).parseCompound();
	}

	private <T extends Comparable<T>> void parsePropertyValue(Property<T> property, String value, int cursor) throws CommandSyntaxException {
		Optional<T> optional = property.parse(value);
		if (optional.isPresent()) {
			this.blockState = this.blockState.with(property, (Comparable)optional.get());
			this.blockProperties.put(property, (Comparable)optional.get());
		} else {
			this.reader.setCursor(cursor);
			throw INVALID_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), property.getName(), value);
		}
	}

	public static String stringifyBlockState(BlockState state) {
		StringBuilder stringBuilder = new StringBuilder(Registry.BLOCK.getId(state.getBlock()).toString());
		if (!state.getProperties().isEmpty()) {
			stringBuilder.append('[');
			boolean bl = false;

			for (Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
				if (bl) {
					stringBuilder.append(',');
				}

				stringifyProperty(stringBuilder, (Property)entry.getKey(), (Comparable<?>)entry.getValue());
				bl = true;
			}

			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> void stringifyProperty(StringBuilder builder, Property<T> property, Comparable<?> value) {
		builder.append(property.getName());
		builder.append('=');
		builder.append(property.name((T)value));
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, TagGroup<Block> tagGroup) {
		return (CompletableFuture<Suggestions>)this.suggestions.apply(builder.createOffset(this.reader.getCursor()), tagGroup);
	}

	public Map<String, String> getProperties() {
		return this.tagProperties;
	}
}
