package net.minecraft.sortme;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStringReader {
	public static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.item.tag.disallowed")
	);
	public static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.item.id.invalid", object)
	);
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10806 = SuggestionsBuilder::buildFuture;
	private final StringReader reader;
	private final boolean field_10804;
	private final Map<Property<?>, Comparable<?>> field_10801 = Maps.<Property<?>, Comparable<?>>newHashMap();
	private Item item;
	@Nullable
	private CompoundTag field_10807;
	private Identifier field_10808 = new Identifier("");
	private int cursor;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10805 = field_10806;

	public ItemStringReader(StringReader stringReader, boolean bl) {
		this.reader = stringReader;
		this.field_10804 = bl;
	}

	public Item getItem() {
		return this.item;
	}

	@Nullable
	public CompoundTag method_9797() {
		return this.field_10807;
	}

	public Identifier method_9790() {
		return this.field_10808;
	}

	public void method_9795() throws CommandSyntaxException {
		int i = this.reader.getCursor();
		Identifier identifier = Identifier.parse(this.reader);
		this.item = (Item)Registry.ITEM.method_17966(identifier).orElseThrow(() -> {
			this.reader.setCursor(i);
			return ID_INVALID_EXCEPTION.createWithContext(this.reader, identifier.toString());
		});
	}

	public void method_9787() throws CommandSyntaxException {
		if (!this.field_10804) {
			throw TAG_DISALLOWED_EXCEPTION.create();
		} else {
			this.field_10805 = this::method_9796;
			this.reader.expect('#');
			this.cursor = this.reader.getCursor();
			this.field_10808 = Identifier.parse(this.reader);
		}
	}

	public void method_9788() throws CommandSyntaxException {
		this.field_10807 = new JsonLikeTagParser(this.reader).parseCompoundTag();
	}

	public ItemStringReader consume() throws CommandSyntaxException {
		this.field_10805 = this::method_9791;
		if (this.reader.canRead() && this.reader.peek() == '#') {
			this.method_9787();
		} else {
			this.method_9795();
			this.field_10805 = this::method_9794;
		}

		if (this.reader.canRead() && this.reader.peek() == '{') {
			this.field_10805 = field_10806;
			this.method_9788();
		}

		return this;
	}

	private CompletableFuture<Suggestions> method_9794(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9796(SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(ItemTags.method_15106().getKeys(), suggestionsBuilder.createOffset(this.cursor));
	}

	private CompletableFuture<Suggestions> method_9791(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_10804) {
			CommandSource.suggestIdentifiers(ItemTags.method_15106().getKeys(), suggestionsBuilder, String.valueOf('#'));
		}

		return CommandSource.suggestIdentifiers(Registry.ITEM.getIds(), suggestionsBuilder);
	}

	public CompletableFuture<Suggestions> method_9793(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_10805.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
	}
}
