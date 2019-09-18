package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

public class EmptyEntry extends LeafEntry {
	private EmptyEntry(int i, int j, class_4570[] args, LootFunction[] lootFunctions) {
		super(i, j, args, lootFunctions);
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
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_4570[] args, LootFunction[] lootFunctions
		) {
			return new EmptyEntry(i, j, args, lootFunctions);
		}
	}
}
