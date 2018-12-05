package net.minecraft;

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

public class class_2259 {
	public static final SimpleCommandExceptionType field_10691 = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.block.tag.disallowed"));
	public static final DynamicCommandExceptionType field_10690 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.block.id.invalid", object)
	);
	public static final Dynamic2CommandExceptionType field_10695 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.unknown", object, object2)
	);
	public static final Dynamic2CommandExceptionType field_10692 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.duplicate", object2, object)
	);
	public static final Dynamic3CommandExceptionType field_10683 = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new TranslatableTextComponent("argument.block.property.invalid", object, object3, object2)
	);
	public static final Dynamic2CommandExceptionType field_10688 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.block.property.novalue", object, object2)
	);
	public static final SimpleCommandExceptionType field_10684 = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.block.property.unclosed"));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10682 = SuggestionsBuilder::buildFuture;
	private final StringReader field_10698;
	private final boolean field_10687;
	private final Map<Property<?>, Comparable<?>> field_10699 = Maps.<Property<?>, Comparable<?>>newHashMap();
	private final Map<String, String> field_10685 = Maps.<String, String>newHashMap();
	private Identifier field_10697 = new Identifier("");
	private StateFactory<Block, BlockState> field_10689;
	private BlockState field_10686;
	@Nullable
	private CompoundTag field_10693;
	private Identifier field_10681 = new Identifier("");
	private int field_10694;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10696 = field_10682;

	public class_2259(StringReader stringReader, boolean bl) {
		this.field_10698 = stringReader;
		this.field_10687 = bl;
	}

	public Map<Property<?>, Comparable<?>> method_9692() {
		return this.field_10699;
	}

	@Nullable
	public BlockState method_9669() {
		return this.field_10686;
	}

	@Nullable
	public CompoundTag method_9694() {
		return this.field_10693;
	}

	@Nullable
	public Identifier method_9664() {
		return this.field_10681;
	}

	public class_2259 method_9678(boolean bl) throws CommandSyntaxException {
		this.field_10696 = this::method_9673;
		if (this.field_10698.canRead() && this.field_10698.peek() == '#') {
			this.method_9677();
			this.field_10696 = this::method_9679;
			if (this.field_10698.canRead() && this.field_10698.peek() == '[') {
				this.method_9680();
				this.field_10696 = this::method_9687;
			}
		} else {
			this.method_9675();
			this.field_10696 = this::method_9681;
			if (this.field_10698.canRead() && this.field_10698.peek() == '[') {
				this.method_9659();
				this.field_10696 = this::method_9687;
			}
		}

		if (bl && this.field_10698.canRead() && this.field_10698.peek() == '{') {
			this.field_10696 = field_10682;
			this.method_9672();
		}

		return this;
	}

	private CompletableFuture<Suggestions> method_9671(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_9665(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9674(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_9667(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9665(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Property<?> property : this.field_10686.getProperties()) {
			if (!this.field_10699.containsKey(property) && property.getName().startsWith(string)) {
				suggestionsBuilder.suggest(property.getName() + '=');
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9667(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		if (this.field_10681 != null && !this.field_10681.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().get(this.field_10681);
			if (tag != null) {
				for (Block block : tag.values()) {
					for (Property<?> property : block.getStateFactory().getProperties()) {
						if (!this.field_10685.containsKey(property.getName()) && property.getName().startsWith(string)) {
							suggestionsBuilder.suggest(property.getName() + '=');
						}
					}
				}
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9687(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty() && this.method_9676()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private boolean method_9676() {
		if (this.field_10686 != null) {
			return this.field_10686.getBlock().hasBlockEntity();
		} else {
			if (this.field_10681 != null) {
				Tag<Block> tag = BlockTags.getContainer().get(this.field_10681);
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

	private CompletableFuture<Suggestions> method_9693(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('='));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9689(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		if (suggestionsBuilder.getRemaining().isEmpty() && this.field_10699.size() < this.field_10686.getProperties().size()) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		return suggestionsBuilder.buildFuture();
	}

	private static <T extends Comparable<T>> SuggestionsBuilder method_9662(SuggestionsBuilder suggestionsBuilder, Property<T> property) {
		for (T comparable : property.getValues()) {
			if (comparable instanceof Integer) {
				suggestionsBuilder.suggest((Integer)comparable);
			} else {
				suggestionsBuilder.suggest(property.getValueAsString(comparable));
			}
		}

		return suggestionsBuilder;
	}

	private CompletableFuture<Suggestions> method_9690(SuggestionsBuilder suggestionsBuilder, String string) {
		boolean bl = false;
		if (this.field_10681 != null && !this.field_10681.getPath().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().get(this.field_10681);
			if (tag != null) {
				for (Block block : tag.values()) {
					Property<?> property = block.getStateFactory().getProperty(string);
					if (property != null) {
						method_9662(suggestionsBuilder, property);
					}

					if (!bl) {
						for (Property<?> property2 : block.getStateFactory().getProperties()) {
							if (!this.field_10685.containsKey(property2.getName())) {
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

	private CompletableFuture<Suggestions> method_9679(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			Tag<Block> tag = BlockTags.getContainer().get(this.field_10681);
			if (tag != null) {
				boolean bl = false;
				boolean bl2 = false;

				for (Block block : tag.values()) {
					bl |= !block.getStateFactory().getProperties().isEmpty();
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

		return this.method_9670(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9681(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			if (!this.field_10686.getBlock().getStateFactory().getProperties().isEmpty()) {
				suggestionsBuilder.suggest(String.valueOf('['));
			}

			if (this.field_10686.getBlock().hasBlockEntity()) {
				suggestionsBuilder.suggest(String.valueOf('{'));
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9670(SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(BlockTags.getContainer().getKeys(), suggestionsBuilder.createOffset(this.field_10694).add(suggestionsBuilder));
	}

	private CompletableFuture<Suggestions> method_9673(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_10687) {
			CommandSource.suggestIdentifiers(BlockTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
		}

		CommandSource.suggestIdentifiers(Registry.BLOCK.keys(), suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	public void method_9675() throws CommandSyntaxException {
		int i = this.field_10698.getCursor();
		this.field_10697 = Identifier.parse(this.field_10698);
		if (Registry.BLOCK.contains(this.field_10697)) {
			Block block = Registry.BLOCK.get(this.field_10697);
			this.field_10689 = block.getStateFactory();
			this.field_10686 = block.getDefaultState();
		} else {
			this.field_10698.setCursor(i);
			throw field_10690.createWithContext(this.field_10698, this.field_10697.toString());
		}
	}

	public void method_9677() throws CommandSyntaxException {
		if (!this.field_10687) {
			throw field_10691.create();
		} else {
			this.field_10696 = this::method_9670;
			this.field_10698.expect('#');
			this.field_10694 = this.field_10698.getCursor();
			this.field_10681 = Identifier.parse(this.field_10698);
		}
	}

	public void method_9659() throws CommandSyntaxException {
		this.field_10698.skip();
		this.field_10696 = this::method_9671;
		this.field_10698.skipWhitespace();

		while (this.field_10698.canRead() && this.field_10698.peek() != ']') {
			this.field_10698.skipWhitespace();
			int i = this.field_10698.getCursor();
			String string = this.field_10698.readString();
			Property<?> property = this.field_10689.getProperty(string);
			if (property == null) {
				this.field_10698.setCursor(i);
				throw field_10695.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			if (this.field_10699.containsKey(property)) {
				this.field_10698.setCursor(i);
				throw field_10692.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skipWhitespace();
			this.field_10696 = this::method_9693;
			if (!this.field_10698.canRead() || this.field_10698.peek() != '=') {
				throw field_10688.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skip();
			this.field_10698.skipWhitespace();
			this.field_10696 = suggestionsBuilder -> method_9662(suggestionsBuilder, property).buildFuture();
			int j = this.field_10698.getCursor();
			this.method_9668(property, this.field_10698.readString(), j);
			this.field_10696 = this::method_9689;
			this.field_10698.skipWhitespace();
			if (this.field_10698.canRead()) {
				if (this.field_10698.peek() != ',') {
					if (this.field_10698.peek() != ']') {
						throw field_10684.createWithContext(this.field_10698);
					}
					break;
				}

				this.field_10698.skip();
				this.field_10696 = this::method_9665;
			}
		}

		if (this.field_10698.canRead()) {
			this.field_10698.skip();
		} else {
			throw field_10684.createWithContext(this.field_10698);
		}
	}

	public void method_9680() throws CommandSyntaxException {
		this.field_10698.skip();
		this.field_10696 = this::method_9674;
		int i = -1;
		this.field_10698.skipWhitespace();

		while (this.field_10698.canRead() && this.field_10698.peek() != ']') {
			this.field_10698.skipWhitespace();
			int j = this.field_10698.getCursor();
			String string = this.field_10698.readString();
			if (this.field_10685.containsKey(string)) {
				this.field_10698.setCursor(j);
				throw field_10692.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skipWhitespace();
			if (!this.field_10698.canRead() || this.field_10698.peek() != '=') {
				this.field_10698.setCursor(j);
				throw field_10688.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skip();
			this.field_10698.skipWhitespace();
			this.field_10696 = suggestionsBuilder -> this.method_9690(suggestionsBuilder, string);
			i = this.field_10698.getCursor();
			String string2 = this.field_10698.readString();
			this.field_10685.put(string, string2);
			this.field_10698.skipWhitespace();
			if (this.field_10698.canRead()) {
				i = -1;
				if (this.field_10698.peek() != ',') {
					if (this.field_10698.peek() != ']') {
						throw field_10684.createWithContext(this.field_10698);
					}
					break;
				}

				this.field_10698.skip();
				this.field_10696 = this::method_9667;
			}
		}

		if (this.field_10698.canRead()) {
			this.field_10698.skip();
		} else {
			if (i >= 0) {
				this.field_10698.setCursor(i);
			}

			throw field_10684.createWithContext(this.field_10698);
		}
	}

	public void method_9672() throws CommandSyntaxException {
		this.field_10693 = new JsonLikeTagParser(this.field_10698).parseCompoundTag();
	}

	private <T extends Comparable<T>> void method_9668(Property<T> property, String string, int i) throws CommandSyntaxException {
		Optional<T> optional = property.getValue(string);
		if (optional.isPresent()) {
			this.field_10686 = this.field_10686.with(property, (Comparable)optional.get());
			this.field_10699.put(property, optional.get());
		} else {
			this.field_10698.setCursor(i);
			throw field_10683.createWithContext(this.field_10698, this.field_10697.toString(), property.getName(), string);
		}
	}

	public static String method_9685(BlockState blockState) {
		StringBuilder stringBuilder = new StringBuilder(Registry.BLOCK.getId(blockState.getBlock()).toString());
		if (!blockState.getProperties().isEmpty()) {
			stringBuilder.append('[');
			boolean bl = false;

			for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
				if (bl) {
					stringBuilder.append(',');
				}

				method_9663(stringBuilder, (Property)entry.getKey(), (Comparable<?>)entry.getValue());
				bl = true;
			}

			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> void method_9663(StringBuilder stringBuilder, Property<T> property, Comparable<?> comparable) {
		stringBuilder.append(property.getName());
		stringBuilder.append('=');
		stringBuilder.append(property.getValueAsString((T)comparable));
	}

	public CompletableFuture<Suggestions> method_9666(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_10696.apply(suggestionsBuilder.createOffset(this.field_10698.getCursor()));
	}

	public Map<String, String> method_9688() {
		return this.field_10685;
	}
}
