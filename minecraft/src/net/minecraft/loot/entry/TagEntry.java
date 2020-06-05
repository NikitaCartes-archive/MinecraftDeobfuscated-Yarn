package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TagEntry extends LeafEntry {
	private final Tag<Item> name;
	private final boolean expand;

	private TagEntry(Tag<Item> name, boolean expand, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(weight, quality, conditions, functions);
		this.name = name;
		this.expand = expand;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.TAG;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		this.name.values().forEach(item -> lootConsumer.accept(new ItemStack(item)));
	}

	private boolean grow(LootContext context, Consumer<LootChoice> lootChoiceExpander) {
		if (!this.test(context)) {
			return false;
		} else {
			for (final Item item : this.name.values()) {
				lootChoiceExpander.accept(new LeafEntry.Choice() {
					@Override
					public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
						lootConsumer.accept(new ItemStack(item));
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

	public static LeafEntry.Builder<?> builder(Tag<Item> name) {
		return builder((weight, quality, conditions, functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
	}

	public static class Serializer extends LeafEntry.Serializer<TagEntry> {
		public void addEntryFields(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
			super.addEntryFields(jsonObject, tagEntry, jsonSerializationContext);
			jsonObject.addProperty("name", TagContainers.instance().items().checkId(tagEntry.name).toString());
			jsonObject.addProperty("expand", tagEntry.expand);
		}

		protected TagEntry fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			Tag<Item> tag = TagContainers.instance().items().get(identifier);
			if (tag == null) {
				throw new JsonParseException("Can't find tag: " + identifier);
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "expand");
				return new TagEntry(tag, bl, i, j, lootConditions, lootFunctions);
			}
		}
	}
}
