/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

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
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import org.jetbrains.annotations.Nullable;

public class ItemPredicateArgumentType
implements ArgumentType<ItemPredicateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.item.tag.unknown", object));

    public static ItemPredicateArgumentType itemPredicate() {
        return new ItemPredicateArgumentType();
    }

    public ItemPredicateArgument method_9800(StringReader stringReader) throws CommandSyntaxException {
        ItemStringReader itemStringReader = new ItemStringReader(stringReader, true).consume();
        if (itemStringReader.getItem() != null) {
            ItemPredicate itemPredicate = new ItemPredicate(itemStringReader.getItem(), itemStringReader.getTag());
            return commandContext -> itemPredicate;
        }
        Identifier identifier = itemStringReader.getId();
        return commandContext -> {
            Tag<Item> tag = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager().items().get(identifier);
            if (tag == null) {
                throw UNKNOWN_TAG_EXCEPTION.create(identifier.toString());
            }
            return new TagPredicate(tag, itemStringReader.getTag());
        };
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return commandContext.getArgument(string, ItemPredicateArgument.class).create(commandContext);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
        stringReader.setCursor(suggestionsBuilder.getStart());
        ItemStringReader itemStringReader = new ItemStringReader(stringReader, true);
        try {
            itemStringReader.consume();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemStringReader.getSuggestions(suggestionsBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9800(stringReader);
    }

    static class TagPredicate
    implements Predicate<ItemStack> {
        private final Tag<Item> tag;
        @Nullable
        private final CompoundTag compound;

        public TagPredicate(Tag<Item> tag, @Nullable CompoundTag compoundTag) {
            this.tag = tag;
            this.compound = compoundTag;
        }

        public boolean method_9807(ItemStack itemStack) {
            return this.tag.contains(itemStack.getItem()) && TagHelper.areTagsEqual(this.compound, itemStack.getTag(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.method_9807((ItemStack)object);
        }
    }

    static class ItemPredicate
    implements Predicate<ItemStack> {
        private final Item item;
        @Nullable
        private final CompoundTag compound;

        public ItemPredicate(Item item, @Nullable CompoundTag compoundTag) {
            this.item = item;
            this.compound = compoundTag;
        }

        public boolean method_9806(ItemStack itemStack) {
            return itemStack.getItem() == this.item && TagHelper.areTagsEqual(this.compound, itemStack.getTag(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.method_9806((ItemStack)object);
        }
    }

    public static interface ItemPredicateArgument {
        public Predicate<ItemStack> create(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

