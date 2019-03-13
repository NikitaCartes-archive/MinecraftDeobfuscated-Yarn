package net.minecraft.command.arguments;

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
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockArgumentParser {
	public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.block.tag.disallowed")
	);
	public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.block.id.invalid", object)
	);
	public static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.unknown", object, object2)
	);
	public static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.duplicate", object2, object)
	);
	public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new TranslatableTextComponent("argument.block.property.invalid", object, object3, object2)
	);
	public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.novalue", object, object2)
	);
	public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.block.property.unclosed")
	);
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
	private final StringReader reader;
	private final boolean allowTag;
	private final Map<Property<?>, Comparable<?>> blockProperties = Maps.<Property<?>, Comparable<?>>newHashMap();
	private final Map<String, String> tagProperties = Maps.<String, String>newHashMap();
	private Identifier field_10697 = new Identifier("");
	private StateFactory<Block, BlockState> stateFactory;
	private BlockState blockState;
	@Nullable
	private CompoundTag field_10693;
	private Identifier field_10681 = new Identifier("");
	private int cursorPos;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_DEFAULT;

	public BlockArgumentParser(StringReader stringReader, boolean bl) {
		this.reader = stringReader;
		this.allowTag = bl;
	}

	public Map<Property<?>, Comparable<?>> method_9692() {
		return this.blockProperties;
	}

	@Nullable
	public BlockState getBlockState() {
		return this.blockState;
	}

	@Nullable
	public CompoundTag method_9694() {
		return this.field_10693;
	}

	@Nullable
	public Identifier method_9664() {
		return this.field_10681;
	}

	public BlockArgumentParser parse(boolean bl) throws CommandSyntaxException {
		this.suggestions = this::suggestBlockOrTagId;
		if (this.reader.canRead() && this.reader.peek() == '#') {
			this.parseTagId();
			this.suggestions = this::suggestMojangsonOrTagProperties;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.parseTagProperties();
				this.suggestions = this::suggestMojangson;
			}
		} else {
			this.parseBlockId();
			this.suggestions = this::suggestMojangsonOrBlockProperties;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.parseBlockProperties();
				this.suggestions = this::suggestMojangson;
			}
		}

		if (bl && this.reader.canRead() && this.reader.peek() == '{') {
			this.suggestions = SUGGEST_DEFAULT;
			this.parseMojangson();
		}

		return this;
	}

	private CompletableFuture<Suggestions> suggestBlockPropertiesOrEnd(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.suggestBlockProperties(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> suggestTagPropertiesOrEnd(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.suggestTagProperties(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> suggestBlockProperties(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Property<?> property : this.blockState.getProperties()) {
			if (!this.blockProperties.containsKey(property) && property.getName().startsWith(string)) {
				suggestionsBuilder.suggest(property.getName() + '=');
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestTagProperties(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		if (this.field_10681 != null && !this.field_10681.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.method_15073().get(this.field_10681);
			if (tag != null) {
				for (Block block : tag.values()) {
					for (Property<?> property : block.method_9595().getProperties()) {
						if (!this.tagProperties.containsKey(property.getName()) && property.getName().startsWith(string)) {
							suggestionsBuilder.suggest(property.getName() + '=');
						}
					}
				}
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestMojangson(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty() && this.hasTileEntity()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private boolean hasTileEntity() {
		if (this.blockState != null) {
			return this.blockState.getBlock().hasBlockEntity();
		} else {
			if (this.field_10681 != null) {
				Tag<Block> tag = BlockTags.method_15073().get(this.field_10681);
				if (tag != null) {
					for (Block block : tag.values()) {
						if (block.hasBlockEntity()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private CompletableFuture<Suggestions> suggestEqualsCharacter(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('='));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		if (suggestionsBuilder.getRemaining().isEmpty() && this.blockProperties.size() < this.blockState.getProperties().size()) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		return suggestionsBuilder.buildFuture();
	}

	private static <T extends Comparable<T>> SuggestionsBuilder suggestPropertyValues(SuggestionsBuilder suggestionsBuilder, Property<T> property) {
		for (T comparable : property.getValues()) {
			if (comparable instanceof Integer) {
				suggestionsBuilder.suggest((Integer)comparable);
			} else {
				suggestionsBuilder.suggest(property.getValueAsString(comparable));
			}
		}

		return suggestionsBuilder;
	}

	private CompletableFuture<Suggestions> suggestTagPropertyValues(SuggestionsBuilder suggestionsBuilder, String string) {
		boolean bl = false;
		if (this.field_10681 != null && !this.field_10681.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.method_15073().get(this.field_10681);
			if (tag != null) {
				for (Block block : tag.values()) {
					Property<?> property = block.method_9595().method_11663(string);
					if (property != null) {
						suggestPropertyValues(suggestionsBuilder, property);
					}

					if (!bl) {
						for (Property<?> property2 : block.method_9595().getProperties()) {
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
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestMojangsonOrTagProperties(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			Tag<Block> tag = BlockTags.method_15073().get(this.field_10681);
			if (tag != null) {
				boolean bl = false;
				boolean bl2 = false;

				for (Block block : tag.values()) {
					bl |= !block.method_9595().getProperties().isEmpty();
					bl2 |= block.hasBlockEntity();
					if (bl && bl2) {
						break;
					}
				}

				if (bl) {
					suggestionsBuilder.suggest(String.valueOf('['));
				}

				if (bl2) {
					suggestionsBuilder.suggest(String.valueOf('{'));
				}
			}
		}

		return this.suggestIdentifiers(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> suggestMojangsonOrBlockProperties(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			if (!this.blockState.getBlock().method_9595().getProperties().isEmpty()) {
				suggestionsBuilder.suggest(String.valueOf('['));
			}

			if (this.blockState.getBlock().hasBlockEntity()) {
				suggestionsBuilder.suggest(String.valueOf('{'));
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestIdentifiers(SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(BlockTags.method_15073().getKeys(), suggestionsBuilder.createOffset(this.cursorPos).add(suggestionsBuilder));
	}

	private CompletableFuture<Suggestions> suggestBlockOrTagId(SuggestionsBuilder suggestionsBuilder) {
		if (this.allowTag) {
			CommandSource.suggestIdentifiers(BlockTags.method_15073().getKeys(), suggestionsBuilder, String.valueOf('#'));
		}

		CommandSource.suggestIdentifiers(Registry.BLOCK.getIds(), suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	public void parseBlockId() throws CommandSyntaxException {
		int i = this.reader.getCursor();
		this.field_10697 = Identifier.parse(this.reader);
		Block block = (Block)Registry.BLOCK.method_17966(this.field_10697).orElseThrow(() -> {
			this.reader.setCursor(i);
			return INVALID_BLOCK_ID_EXCEPTION.createWithContext(this.reader, this.field_10697.toString());
		});
		this.stateFactory = block.method_9595();
		this.blockState = block.method_9564();
	}

	public void parseTagId() throws CommandSyntaxException {
		if (!this.allowTag) {
			throw DISALLOWED_TAG_EXCEPTION.create();
		} else {
			this.suggestions = this::suggestIdentifiers;
			this.reader.expect('#');
			this.cursorPos = this.reader.getCursor();
			this.field_10681 = Identifier.parse(this.reader);
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
			Property<?> property = this.stateFactory.method_11663(string);
			if (property == null) {
				this.reader.setCursor(i);
				throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), string);
			}

			if (this.blockProperties.containsKey(property)) {
				this.reader.setCursor(i);
				throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), string);
			}

			this.reader.skipWhitespace();
			this.suggestions = this::suggestEqualsCharacter;
			if (!this.reader.canRead() || this.reader.peek() != '=') {
				throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), string);
			}

			this.reader.skip();
			this.reader.skipWhitespace();
			this.suggestions = suggestionsBuilder -> suggestPropertyValues(suggestionsBuilder, property).buildFuture();
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
				throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), string);
			}

			this.reader.skipWhitespace();
			if (!this.reader.canRead() || this.reader.peek() != '=') {
				this.reader.setCursor(j);
				throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), string);
			}

			this.reader.skip();
			this.reader.skipWhitespace();
			this.suggestions = suggestionsBuilder -> this.suggestTagPropertyValues(suggestionsBuilder, string);
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

	public void parseMojangson() throws CommandSyntaxException {
		this.field_10693 = new JsonLikeTagParser(this.reader).parseCompoundTag();
	}

	private <T extends Comparable<T>> void parsePropertyValue(Property<T> property, String string, int i) throws CommandSyntaxException {
		Optional<T> optional = property.getValue(string);
		if (optional.isPresent()) {
			this.blockState = this.blockState.method_11657(property, (Comparable)optional.get());
			this.blockProperties.put(property, optional.get());
		} else {
			this.reader.setCursor(i);
			throw INVALID_PROPERTY_EXCEPTION.createWithContext(this.reader, this.field_10697.toString(), property.getName(), string);
		}
	}

	public static String stringifyBlockState(BlockState blockState) {
		StringBuilder stringBuilder = new StringBuilder(Registry.BLOCK.method_10221(blockState.getBlock()).toString());
		if (!blockState.getProperties().isEmpty()) {
			stringBuilder.append('[');
			boolean bl = false;

			for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
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

	private static <T extends Comparable<T>> void stringifyProperty(StringBuilder stringBuilder, Property<T> property, Comparable<?> comparable) {
		stringBuilder.append(property.getName());
		stringBuilder.append('=');
		stringBuilder.append(property.getValueAsString((T)comparable));
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
	}

	public Map<String, String> getProperties() {
		return this.tagProperties;
	}
}
