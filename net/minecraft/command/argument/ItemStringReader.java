/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public class ItemStringReader {
    private static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.item.tag.disallowed"));
    private static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("argument.item.id.invalid", id));
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.translatable("arguments.item.tag.unknown", tag));
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char HASH_SIGN = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> NBT_SUGGESTION_PROVIDER = SuggestionsBuilder::buildFuture;
    private final CommandRegistryWrapper<Item> registryWrapper;
    private final StringReader reader;
    private final boolean allowTag;
    private Either<RegistryEntry<Item>, RegistryEntryList<Item>> result;
    @Nullable
    private NbtCompound nbt;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = NBT_SUGGESTION_PROVIDER;

    private ItemStringReader(CommandRegistryWrapper<Item> registryWrapper, StringReader reader, boolean allowTag) {
        this.registryWrapper = registryWrapper;
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public static ItemResult item(CommandRegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, reader, false);
            itemStringReader.consume();
            RegistryEntry<Item> registryEntry = itemStringReader.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
            return new ItemResult(registryEntry, itemStringReader.nbt);
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static Either<ItemResult, TagResult> itemOrTag(CommandRegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, reader, true);
            itemStringReader.consume();
            return itemStringReader.result.mapBoth(item -> new ItemResult((RegistryEntry<Item>)item, itemStringReader.nbt), tag -> new TagResult((RegistryEntryList<Item>)tag, itemStringReader.nbt));
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static CompletableFuture<Suggestions> getSuggestions(CommandRegistryWrapper<Item> registryWrapper, SuggestionsBuilder builder, boolean allowTag) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, stringReader, allowTag);
        try {
            itemStringReader.consume();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemStringReader.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntry<Item>> optional = this.registryWrapper.getEntry(RegistryKey.of(Registry.ITEM_KEY, identifier));
        this.result = Either.left(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ID_INVALID_EXCEPTION.createWithContext(this.reader, identifier);
        }));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw TAG_DISALLOWED_EXCEPTION.createWithContext(this.reader);
        }
        int i = this.reader.getCursor();
        this.reader.expect('#');
        this.suggestions = this::suggestTag;
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntryList<Item>> optional = this.registryWrapper.getEntryList(TagKey.of(Registry.ITEM_KEY, identifier));
        this.result = Either.right(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return UNKNOWN_TAG_EXCEPTION.createWithContext(this.reader, identifier);
        }));
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = new StringNbtReader(this.reader).parseCompound();
    }

    private void consume() throws CommandSyntaxException {
        this.suggestions = this.allowTag ? this::suggestItemOrTagId : this::suggestItemId;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
        }
        this.suggestions = this::suggestItem;
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = NBT_SUGGESTION_PROVIDER;
            this.readNbt();
        }
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('{'));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamTags().map(TagKey::id), builder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItemId(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    private CompletableFuture<Suggestions> suggestItemOrTagId(SuggestionsBuilder builder) {
        this.suggestTag(builder);
        return this.suggestItemId(builder);
    }

    public record ItemResult(RegistryEntry<Item> item, @Nullable NbtCompound nbt) {
        @Nullable
        public NbtCompound nbt() {
            return this.nbt;
        }
    }

    public record TagResult(RegistryEntryList<Item> tag, @Nullable NbtCompound nbt) {
        @Nullable
        public NbtCompound nbt() {
            return this.nbt;
        }
    }
}

