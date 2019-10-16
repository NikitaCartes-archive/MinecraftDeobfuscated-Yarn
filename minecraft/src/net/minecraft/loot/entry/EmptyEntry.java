package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;

public class EmptyEntry extends LeafEntry {
	private EmptyEntry(int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
	}

	public static LeafEntry.Builder<?> Serializer() {
		return builder(EmptyEntry::new);
	}

	public static class Serializer extends LeafEntry.Serializer<EmptyEntry> {
		public Serializer() {
			super(new Identifier("empty"), EmptyEntry.class);
		}

		protected EmptyEntry method_402(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			return new EmptyEntry(i, j, lootConditions, lootFunctions);
		}
	}
}
