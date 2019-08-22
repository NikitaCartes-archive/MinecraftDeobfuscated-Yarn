package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

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
