package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DynamicEntry extends LeafEntry {
	public static final Identifier instance = new Identifier("dynamic");
	private final Identifier name;

	private DynamicEntry(Identifier name, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(weight, quality, conditions, functions);
		this.name = name;
	}

	@Override
	public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
		context.drop(this.name, itemDropper);
	}

	public static LeafEntry.Builder<?> builder(Identifier name) {
		return builder((weight, quality, conditions, functions) -> new DynamicEntry(name, weight, quality, conditions, functions));
	}

	public static class Serializer extends LeafEntry.Serializer<DynamicEntry> {
		public Serializer() {
			super(new Identifier("dynamic"), DynamicEntry.class);
		}

		public void toJson(JsonObject jsonObject, DynamicEntry dynamicEntry, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, dynamicEntry, jsonSerializationContext);
			jsonObject.addProperty("name", dynamicEntry.name.toString());
		}

		protected DynamicEntry fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new DynamicEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
