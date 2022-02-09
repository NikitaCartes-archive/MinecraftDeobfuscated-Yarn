/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.TagKey;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemPredicateArgumentType
implements ArgumentType<ItemPredicateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("arguments.item.tag.unknown", id));

    public static ItemPredicateArgumentType itemPredicate() {
        return new ItemPredicateArgumentType();
    }

    @Override
    public ItemPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        ItemStringReader itemStringReader = new ItemStringReader(stringReader, true).consume();
        if (itemStringReader.getItem() != null) {
            ItemPredicate itemPredicate = new ItemPredicate(itemStringReader.getItem(), itemStringReader.getNbt());
            return context -> itemPredicate;
        }
        TagKey<Item> tagKey = itemStringReader.getId();
        return commandContext -> {
            if (!Registry.ITEM.containsTag(tagKey)) {
                throw UNKNOWN_TAG_EXCEPTION.create(tagKey);
            }
            return new TagPredicate(tagKey, itemStringReader.getNbt());
        };
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, ItemPredicateArgument.class).create(context);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ItemStringReader itemStringReader = new ItemStringReader(stringReader, true);
        try {
            itemStringReader.consume();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemStringReader.getSuggestions(builder, Registry.ITEM);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    static class ItemPredicate
    implements Predicate<ItemStack> {
        private final Item item;
        @Nullable
        private final NbtCompound nbt;

        public ItemPredicate(Item item, @Nullable NbtCompound nbt) {
            this.item = item;
            this.nbt = nbt;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return itemStack.isOf(this.item) && NbtHelper.matches(this.nbt, itemStack.getNbt(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((ItemStack)context);
        }
    }

    public static interface ItemPredicateArgument {
        public Predicate<ItemStack> create(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }

    static class TagPredicate
    implements Predicate<ItemStack> {
        private final TagKey<Item> tag;
        @Nullable
        private final NbtCompound compound;

        public TagPredicate(TagKey<Item> tag, @Nullable NbtCompound nbt) {
            this.tag = tag;
            this.compound = nbt;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return itemStack.isIn(this.tag) && NbtHelper.matches(this.compound, itemStack.getNbt(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((ItemStack)context);
        }
    }
}

