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

public class class_2291 {
	public static final SimpleCommandExceptionType field_10800 = new SimpleCommandExceptionType(new class_2588("argument.item.tag.disallowed"));
	public static final DynamicCommandExceptionType field_10799 = new DynamicCommandExceptionType(object -> new class_2588("argument.item.id.invalid", object));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10806 = SuggestionsBuilder::buildFuture;
	private final StringReader field_10802;
	private final boolean field_10804;
	private final Map<class_2769<?>, Comparable<?>> field_10801 = Maps.<class_2769<?>, Comparable<?>>newHashMap();
	private class_1792 field_10803;
	@Nullable
	private class_2487 field_10807;
	private class_2960 field_10808 = new class_2960("");
	private int field_10809;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10805 = field_10806;

	public class_2291(StringReader stringReader, boolean bl) {
		this.field_10802 = stringReader;
		this.field_10804 = bl;
	}

	public class_1792 method_9786() {
		return this.field_10803;
	}

	@Nullable
	public class_2487 method_9797() {
		return this.field_10807;
	}

	public class_2960 method_9790() {
		return this.field_10808;
	}

	public void method_9795() throws CommandSyntaxException {
		int i = this.field_10802.getCursor();
		class_2960 lv = class_2960.method_12835(this.field_10802);
		if (class_2378.field_11142.method_10250(lv)) {
			this.field_10803 = class_2378.field_11142.method_10223(lv);
		} else {
			this.field_10802.setCursor(i);
			throw field_10799.createWithContext(this.field_10802, lv.toString());
		}
	}

	public void method_9787() throws CommandSyntaxException {
		if (!this.field_10804) {
			throw field_10800.create();
		} else {
			this.field_10805 = this::method_9796;
			this.field_10802.expect('#');
			this.field_10809 = this.field_10802.getCursor();
			this.field_10808 = class_2960.method_12835(this.field_10802);
		}
	}

	public void method_9788() throws CommandSyntaxException {
		this.field_10807 = new class_2522(this.field_10802).method_10727();
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
		return class_2172.method_9270(class_3489.method_15106().method_15189(), suggestionsBuilder.createOffset(this.field_10809));
	}

	private CompletableFuture<Suggestions> method_9791(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_10804) {
			class_2172.method_9258(class_3489.method_15106().method_15189(), suggestionsBuilder, String.valueOf('#'));
		}

		return class_2172.method_9270(class_2378.field_11142.method_10235(), suggestionsBuilder);
	}

	public CompletableFuture<Suggestions> method_9793(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_10805.apply(suggestionsBuilder.createOffset(this.field_10802.getCursor()));
	}
}
