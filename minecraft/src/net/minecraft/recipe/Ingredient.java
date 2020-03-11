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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public final class Ingredient implements Predicate<ItemStack> {
	private static final Predicate<? super Ingredient.Entry> NON_EMPTY = entry -> !entry.getStacks().stream().allMatch(ItemStack::isEmpty);
	public static final Ingredient EMPTY = new Ingredient(Stream.empty());
	private final Ingredient.Entry[] entries;
	private ItemStack[] matchingStacks;
	private IntList ids;

	private Ingredient(Stream<? extends Ingredient.Entry> entries) {
		this.entries = (Ingredient.Entry[])entries.filter(NON_EMPTY).toArray(Ingredient.Entry[]::new);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack[] getMatchingStacksClient() {
		this.cacheMatchingStacks();
		return this.matchingStacks;
	}

	private void cacheMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().toArray(ItemStack[]::new);
		}
	}

	public boolean test(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else if (this.entries.length == 0) {
			return itemStack.isEmpty();
		} else {
			this.cacheMatchingStacks();

			for (ItemStack itemStack2 : this.matchingStacks) {
				if (itemStack2.getItem() == itemStack.getItem()) {
					return true;
				}
			}

			return false;
		}
	}

	public IntList getIds() {
		if (this.ids == null) {
			this.cacheMatchingStacks();
			this.ids = new IntArrayList(this.matchingStacks.length);

			for (ItemStack itemStack : this.matchingStacks) {
				this.ids.add(RecipeFinder.getItemId(itemStack));
			}

			this.ids.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.ids;
	}

	public void write(PacketByteBuf buf) {
		this.cacheMatchingStacks();
		buf.writeVarInt(this.matchingStacks.length);

		for (int i = 0; i < this.matchingStacks.length; i++) {
			buf.writeItemStack(this.matchingStacks[i]);
		}
	}

	public JsonElement toJson() {
		if (this.entries.length == 1) {
			return this.entries[0].toJson();
		} else {
			JsonArray jsonArray = new JsonArray();

			for (Ingredient.Entry entry : this.entries) {
				jsonArray.add(entry.toJson());
			}

			return jsonArray;
		}
	}

	public boolean isEmpty() {
		return this.entries.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.ids == null || this.ids.isEmpty());
	}

	private static Ingredient ofEntries(Stream<? extends Ingredient.Entry> entries) {
		Ingredient ingredient = new Ingredient(entries);
		return ingredient.entries.length == 0 ? EMPTY : ingredient;
	}

	public static Ingredient ofItems(ItemConvertible... items) {
		return ofEntries(Arrays.stream(items).map(item -> new Ingredient.StackEntry(new ItemStack(item))));
	}

	@Environment(EnvType.CLIENT)
	public static Ingredient ofStacks(ItemStack... stacks) {
		return ofEntries(Arrays.stream(stacks).map(stack -> new Ingredient.StackEntry(stack)));
	}

	public static Ingredient fromTag(Tag<Item> tag) {
		return ofEntries(Stream.of(new Ingredient.TagEntry(tag)));
	}

	public static Ingredient fromPacket(PacketByteBuf buf) {
		int i = buf.readVarInt();
		return ofEntries(Stream.generate(() -> new Ingredient.StackEntry(buf.readItemStack())).limit((long)i));
	}

	public static Ingredient fromJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (json.isJsonObject()) {
			return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
		} else if (json.isJsonArray()) {
			JsonArray jsonArray = json.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				return ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

	public static Ingredient.Entry entryFromJson(JsonObject json) {
		if (json.has("item") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		} else if (json.has("item")) {
			Identifier identifier = new Identifier(JsonHelper.getString(json, "item"));
			Item item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + identifier + "'"));
			return new Ingredient.StackEntry(new ItemStack(item));
		} else if (json.has("tag")) {
			Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
			Tag<Item> tag = ItemTags.getContainer().get(identifier);
			if (tag == null) {
				throw new JsonSyntaxException("Unknown item tag '" + identifier + "'");
			} else {
				return new Ingredient.TagEntry(tag);
			}
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or an item");
		}
	}

	interface Entry {
		Collection<ItemStack> getStacks();

		JsonObject toJson();
	}

	static class StackEntry implements Ingredient.Entry {
		private final ItemStack stack;

		private StackEntry(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public Collection<ItemStack> getStacks() {
			return Collections.singleton(this.stack);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.stack.getItem()).toString());
			return jsonObject;
		}
	}

	static class TagEntry implements Ingredient.Entry {
		private final Tag<Item> tag;

		private TagEntry(Tag<Item> tag) {
			this.tag = tag;
		}

		@Override
		public Collection<ItemStack> getStacks() {
			List<ItemStack> list = Lists.<ItemStack>newArrayList();

			for (Item item : this.tag.values()) {
				list.add(new ItemStack(item));
			}

			return list;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("tag", this.tag.getId().toString());
			return jsonObject;
		}
	}
}
