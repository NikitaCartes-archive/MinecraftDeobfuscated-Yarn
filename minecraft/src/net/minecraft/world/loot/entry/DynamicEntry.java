package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

public class DynamicEntry extends LeafEntry {
	public static final Identifier instance = new Identifier("dynamic");
	private final Identifier name;

	private DynamicEntry(Identifier identifier, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
		this.name = identifier;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		lootContext.drop(this.name, consumer);
	}

	public static LeafEntry.Builder<?> builder(Identifier identifier) {
		return builder((i, j, lootConditions, lootFunctions) -> new DynamicEntry(identifier, i, j, lootConditions, lootFunctions));
	}

	public static class Serializer extends LeafEntry.Serializer<DynamicEntry> {
		public Serializer() {
			super(new Identifier("dynamic"), DynamicEntry.class);
		}

		public void method_393(JsonObject jsonObject, DynamicEntry dynamicEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, dynamicEntry, jsonSerializationContext);
			jsonObject.addProperty("name", dynamicEntry.name.toString());
		}

		protected DynamicEntry method_392(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new DynamicEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
