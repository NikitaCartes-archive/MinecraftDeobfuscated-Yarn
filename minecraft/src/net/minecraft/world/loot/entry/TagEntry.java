package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

public class TagEntry extends LeafEntry {
	private final Tag<Item> name;
	private final boolean expand;

	private TagEntry(Tag<Item> tag, boolean bl, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
		this.name = tag;
		this.expand = bl;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		this.name.values().forEach(item -> consumer.accept(new ItemStack(item)));
	}

	private boolean grow(LootContext lootContext, Consumer<LootChoice> consumer) {
		if (!this.test(lootContext)) {
			return false;
		} else {
			for (final Item item : this.name.values()) {
				consumer.accept(new LeafEntry.Choice() {
					@Override
					public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
						consumer.accept(new ItemStack(item));
					}
				});
			}

			return true;
		}
	}

	@Override
	public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		return this.expand ? this.grow(lootContext, consumer) : super.expand(lootContext, consumer);
	}

	public static LeafEntry.Builder<?> builder(Tag<Item> tag) {
		return builder((i, j, lootConditions, lootFunctions) -> new TagEntry(tag, true, i, j, lootConditions, lootFunctions));
	}

	public static class Serializer extends LeafEntry.Serializer<TagEntry> {
		public Serializer() {
			super(new Identifier("tag"), TagEntry.class);
		}

		public void method_451(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, tagEntry, jsonSerializationContext);
			jsonObject.addProperty("name", tagEntry.name.getId().toString());
			jsonObject.addProperty("expand", tagEntry.expand);
		}

		protected TagEntry method_450(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			Tag<Item> tag = ItemTags.getContainer().get(identifier);
			if (tag == null) {
				throw new JsonParseException("Can't find tag: " + identifier);
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "expand");
				return new TagEntry(tag, bl, i, j, lootConditions, lootFunctions);
			}
		}
	}
}
