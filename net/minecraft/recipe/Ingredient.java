/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public final class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Entry[] entries;
    @Nullable
    private ItemStack[] matchingStacks;
    @Nullable
    private IntList ids;

    private Ingredient(Stream<? extends Entry> entries) {
        this.entries = (Entry[])entries.toArray(Entry[]::new);
    }

    public ItemStack[] getMatchingStacks() {
        if (this.matchingStacks == null) {
            this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().toArray(ItemStack[]::new);
        }
        return this.matchingStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (this.isEmpty()) {
            return itemStack.isEmpty();
        }
        for (ItemStack itemStack2 : this.getMatchingStacks()) {
            if (!itemStack2.isOf(itemStack.getItem())) continue;
            return true;
        }
        return false;
    }

    public IntList getMatchingItemIds() {
        if (this.ids == null) {
            ItemStack[] itemStacks = this.getMatchingStacks();
            this.ids = new IntArrayList(itemStacks.length);
            for (ItemStack itemStack : itemStacks) {
                this.ids.add(RecipeMatcher.getItemId(itemStack));
            }
            this.ids.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.ids;
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(Arrays.asList(this.getMatchingStacks()), PacketByteBuf::writeItemStack);
    }

    public JsonElement toJson() {
        if (this.entries.length == 1) {
            return this.entries[0].toJson();
        }
        JsonArray jsonArray = new JsonArray();
        for (Entry entry : this.entries) {
            jsonArray.add(entry.toJson());
        }
        return jsonArray;
    }

    public boolean isEmpty() {
        return this.entries.length == 0;
    }

    private static Ingredient ofEntries(Stream<? extends Entry> entries) {
        Ingredient ingredient = new Ingredient(entries);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static Ingredient empty() {
        return EMPTY;
    }

    public static Ingredient ofItems(ItemConvertible ... items) {
        return Ingredient.ofStacks(Arrays.stream(items).map(ItemStack::new));
    }

    public static Ingredient ofStacks(ItemStack ... stacks) {
        return Ingredient.ofStacks(Arrays.stream(stacks));
    }

    public static Ingredient ofStacks(Stream<ItemStack> stacks) {
        return Ingredient.ofEntries(stacks.filter(stack -> !stack.isEmpty()).map(StackEntry::new));
    }

    public static Ingredient fromTag(TagKey<Item> tag) {
        return Ingredient.ofEntries(Stream.of(new TagEntry(tag)));
    }

    public static Ingredient fromPacket(PacketByteBuf buf) {
        return Ingredient.ofEntries(buf.readList(PacketByteBuf::readItemStack).stream().map(StackEntry::new));
    }

    public static Ingredient fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (json.isJsonObject()) {
            return Ingredient.ofEntries(Stream.of(Ingredient.entryFromJson(json.getAsJsonObject())));
        }
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            return Ingredient.ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> Ingredient.entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    private static Entry entryFromJson(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (json.has("item")) {
            Item item = ShapedRecipe.getItem(json);
            return new StackEntry(new ItemStack(item));
        }
        if (json.has("tag")) {
            Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
            TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, identifier);
            return new TagEntry(tagKey);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object stack) {
        return this.test((ItemStack)stack);
    }

    static interface Entry {
        public Collection<ItemStack> getStacks();

        public JsonObject toJson();
    }

    static class TagEntry
    implements Entry {
        private final TagKey<Item> tag;

        TagEntry(TagKey<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            ArrayList<ItemStack> list = Lists.newArrayList();
            for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(this.tag)) {
                list.add(new ItemStack(registryEntry));
            }
            return list;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tag", this.tag.id().toString());
            return jsonObject;
        }
    }

    static class StackEntry
    implements Entry {
        private final ItemStack stack;

        StackEntry(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registries.ITEM.getId(this.stack.getItem()).toString());
            return jsonObject;
        }
    }
}

