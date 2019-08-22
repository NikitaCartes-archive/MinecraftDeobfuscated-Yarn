package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

public class ItemEntry extends LeafEntry {
	private final Item item;

	private ItemEntry(Item item, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
		this.item = item;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		consumer.accept(new ItemStack(this.item));
	}

	public static LeafEntry.Builder<?> builder(ItemConvertible itemConvertible) {
		return builder((i, j, lootConditions, lootFunctions) -> new ItemEntry(itemConvertible.asItem(), i, j, lootConditions, lootFunctions));
	}

	public static class Serializer extends LeafEntry.Serializer<ItemEntry> {
		public Serializer() {
			super(new Identifier("item"), ItemEntry.class);
		}

		public void method_412(JsonObject jsonObject, ItemEntry itemEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, itemEntry, jsonSerializationContext);
			Identifier identifier = Registry.ITEM.getId(itemEntry.item);
			if (identifier == null) {
				throw new IllegalArgumentException("Can't serialize unknown item " + itemEntry.item);
			} else {
				jsonObject.addProperty("name", identifier.toString());
			}
		}

		protected ItemEntry method_413(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Item item = JsonHelper.getItem(jsonObject, "name");
			return new ItemEntry(item, i, j, lootConditions, lootFunctions);
		}
	}
}
