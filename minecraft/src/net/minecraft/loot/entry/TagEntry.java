package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class TagEntry extends LeafEntry {
	final TagKey<Item> name;
	final boolean expand;

	TagEntry(TagKey<Item> name, boolean expand, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
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
		Registry.ITEM.iterateEntries(this.name).forEach(entry -> lootConsumer.accept(new ItemStack(entry)));
	}

	private boolean grow(LootContext context, Consumer<LootChoice> lootChoiceExpander) {
		if (!this.test(context)) {
			return false;
		} else {
			for (final RegistryEntry<Item> registryEntry : Registry.ITEM.iterateEntries(this.name)) {
				lootChoiceExpander.accept(new LeafEntry.Choice() {
					@Override
					public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
						lootConsumer.accept(new ItemStack(registryEntry));
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

	public static LeafEntry.Builder<?> builder(TagKey<Item> name) {
		return builder((weight, quality, conditions, functions) -> new TagEntry(name, false, weight, quality, conditions, functions));
	}

	public static LeafEntry.Builder<?> expandBuilder(TagKey<Item> name) {
		return builder((weight, quality, conditions, functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
	}

	public static class Serializer extends LeafEntry.Serializer<TagEntry> {
		public void addEntryFields(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
			super.addEntryFields(jsonObject, tagEntry, jsonSerializationContext);
			jsonObject.addProperty("name", tagEntry.name.id().toString());
			jsonObject.addProperty("expand", tagEntry.expand);
		}

		protected TagEntry fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			TagKey<Item> tagKey = TagKey.intern(Registry.ITEM_KEY, identifier);
			boolean bl = JsonHelper.getBoolean(jsonObject, "expand");
			return new TagEntry(tagKey, bl, i, j, lootConditions, lootFunctions);
		}
	}
}
