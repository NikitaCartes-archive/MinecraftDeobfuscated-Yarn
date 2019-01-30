package net.minecraft;

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
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.state.property.Property;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_2291 {
	public static final SimpleCommandExceptionType field_10800 = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.item.tag.disallowed"));
	public static final DynamicCommandExceptionType field_10799 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.item.id.invalid", object)
	);
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10806 = SuggestionsBuilder::buildFuture;
	private final StringReader field_10802;
	private final boolean field_10804;
	private final Map<Property<?>, Comparable<?>> field_10801 = Maps.<Property<?>, Comparable<?>>newHashMap();
	private Item field_10803;
	@Nullable
	private CompoundTag field_10807;
	private Identifier field_10808 = new Identifier("");
	private int field_10809;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10805 = field_10806;

	public class_2291(StringReader stringReader, boolean bl) {
		this.field_10802 = stringReader;
		this.field_10804 = bl;
	}

	public Item method_9786() {
		return this.field_10803;
	}

	@Nullable
	public CompoundTag method_9797() {
		return this.field_10807;
	}

	public Identifier method_9790() {
		return this.field_10808;
	}

	public void method_9795() throws CommandSyntaxException {
		int i = this.field_10802.getCursor();
		Identifier identifier = Identifier.parse(this.field_10802);
		this.field_10803 = (Item)Registry.ITEM.getOptional(identifier).orElseThrow(() -> {
			this.field_10802.setCursor(i);
			return field_10799.createWithContext(this.field_10802, identifier.toString());
		});
	}

	public void method_9787() throws CommandSyntaxException {
		if (!this.field_10804) {
			throw field_10800.create();
		} else {
			this.field_10805 = this::method_9796;
			this.field_10802.expect('#');
			this.field_10809 = this.field_10802.getCursor();
			this.field_10808 = Identifier.parse(this.field_10802);
		}
	}

	public void method_9788() throws CommandSyntaxException {
		this.field_10807 = new JsonLikeTagParser(this.field_10802).parseCompoundTag();
	}

	public class_2291 method_9789() throws CommandSyntaxException {
		this.field_10805 = this::method_9791;
		if (this.field_10802.canRead() && this.field_10802.peek() == '#') {
			this.method_9787();
		} else {
			this.method_9795();
			this.field_10805 = this::method_9794;
		}

		if (this.field_10802.canRead() && this.field_10802.peek() == '{') {
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
		return CommandSource.suggestIdentifiers(ItemTags.getContainer().getKeys(), suggestionsBuilder.createOffset(this.field_10809));
	}

	private CompletableFuture<Suggestions> method_9791(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_10804) {
			CommandSource.suggestIdentifiers(ItemTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
		}

		return CommandSource.suggestIdentifiers(Registry.ITEM.keys(), suggestionsBuilder);
	}

	public CompletableFuture<Suggestions> method_9793(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_10805.apply(suggestionsBuilder.createOffset(this.field_10802.getCursor()));
	}
}
