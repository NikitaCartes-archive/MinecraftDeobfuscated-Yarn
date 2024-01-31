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

public class TagKeyArgumentType {
	static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
		data -> Text.stringifiedTranslatable("argument.item.id.invalid", data)
	);
	static final DynamicCommandExceptionType ERROR_UNKNOWN = new DynamicCommandExceptionType(
		data -> Text.stringifiedTranslatable("arguments.item.tag.unknown", data)
	);
	private static final char TAG_PREFIX = '#';
	private static final char NBT_OPENING = '{';
	static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_48967 = SuggestionsBuilder::buildFuture;
	final RegistryWrapper.Impl<Item> registryWrapper;
	final boolean field_48969;

	public TagKeyArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, boolean bl) {
		this.registryWrapper = wrapperLookup.getWrapperOrThrow(RegistryKeys.ITEM);
		this.field_48969 = bl;
	}

	public void method_56865(StringReader stringReader, TagKeyArgumentType.class_9219 arg) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		try {
			new TagKeyArgumentType.class_9217(stringReader, arg).method_56869();
		} catch (CommandSyntaxException var5) {
			stringReader.setCursor(i);
			throw var5;
		}
	}

	public CompletableFuture<Suggestions> method_56866(SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		TagKeyArgumentType.class_9218 lv = new TagKeyArgumentType.class_9218();
		TagKeyArgumentType.class_9217 lv2 = new TagKeyArgumentType.class_9217(stringReader, lv);

		try {
			lv2.method_56869();
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_56879(suggestionsBuilder, stringReader);
	}

	class class_9217 {
		private final StringReader field_48971;
		private final TagKeyArgumentType.class_9219 field_48972;

		class_9217(StringReader stringReader, TagKeyArgumentType.class_9219 arg) {
			this.field_48971 = stringReader;
			this.field_48972 = arg;
		}

		public void method_56869() throws CommandSyntaxException {
			this.field_48972.method_56880(TagKeyArgumentType.this.field_48969 ? this::method_56878 : this::method_56874);
			if (TagKeyArgumentType.this.field_48969 && this.field_48971.canRead() && this.field_48971.peek() == '#') {
				this.method_56875();
			} else {
				this.method_56872();
			}

			this.field_48972.method_56880(this::method_56871);
			if (this.field_48971.canRead() && this.field_48971.peek() == '{') {
				this.field_48972.method_56880(TagKeyArgumentType.field_48967);
				this.method_56877();
			}
		}

		private void method_56872() throws CommandSyntaxException {
			int i = this.field_48971.getCursor();
			Identifier identifier = Identifier.fromCommandInput(this.field_48971);
			this.field_48972
				.method_56853((RegistryEntry<Item>)TagKeyArgumentType.this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
					this.field_48971.setCursor(i);
					return TagKeyArgumentType.ERROR_INVALID.createWithContext(this.field_48971, identifier);
				}));
		}

		private void method_56875() throws CommandSyntaxException {
			int i = this.field_48971.getCursor();
			this.field_48971.expect('#');
			this.field_48972.method_56880(this::method_56876);
			Identifier identifier = Identifier.fromCommandInput(this.field_48971);
			RegistryEntryList<Item> registryEntryList = (RegistryEntryList<Item>)TagKeyArgumentType.this.registryWrapper
				.getOptional(TagKey.of(RegistryKeys.ITEM, identifier))
				.orElseThrow(() -> {
					this.field_48971.setCursor(i);
					return TagKeyArgumentType.ERROR_UNKNOWN.createWithContext(this.field_48971, identifier);
				});
			this.field_48972.method_56862(registryEntryList);
		}

		private void method_56877() throws CommandSyntaxException {
			this.field_48972.method_56880(TagKeyArgumentType.field_48967);
			this.field_48972.method_56854(new StringNbtReader(this.field_48971).parseCompound());
		}

		private CompletableFuture<Suggestions> method_56871(SuggestionsBuilder suggestionsBuilder) {
			if (suggestionsBuilder.getRemaining().isEmpty()) {
				suggestionsBuilder.suggest(String.valueOf('{'));
			}

			return suggestionsBuilder.buildFuture();
		}

		private CompletableFuture<Suggestions> method_56874(SuggestionsBuilder suggestionsBuilder) {
			return CommandSource.suggestIdentifiers(TagKeyArgumentType.this.registryWrapper.streamKeys().map(RegistryKey::getValue), suggestionsBuilder);
		}

		private CompletableFuture<Suggestions> method_56876(SuggestionsBuilder suggestionsBuilder) {
			return CommandSource.suggestIdentifiers(TagKeyArgumentType.this.registryWrapper.streamTagKeys().map(TagKey::id), suggestionsBuilder, String.valueOf('#'));
		}

		private CompletableFuture<Suggestions> method_56878(SuggestionsBuilder suggestionsBuilder) {
			this.method_56876(suggestionsBuilder);
			return this.method_56874(suggestionsBuilder);
		}
	}

	static class class_9218 implements TagKeyArgumentType.class_9219 {
		private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_48973 = TagKeyArgumentType.field_48967;

		@Override
		public void method_56880(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> function) {
			this.field_48973 = function;
		}

		public CompletableFuture<Suggestions> method_56879(SuggestionsBuilder suggestionsBuilder, StringReader stringReader) {
			return (CompletableFuture<Suggestions>)this.field_48973.apply(suggestionsBuilder.createOffset(stringReader.getCursor()));
		}
	}

	public interface class_9219 {
		default void method_56853(RegistryEntry<Item> registryEntry) {
		}

		default void method_56862(RegistryEntryList<Item> registryEntryList) {
		}

		default void method_56854(NbtCompound nbtCompound) {
		}

		default void method_56880(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> function) {
		}
	}
}
