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
	public static final Identifier field_981 = new Identifier("dynamic");
	private final Identifier field_980;

	private DynamicEntry(Identifier identifier, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
		this.field_980 = identifier;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		lootContext.method_297(this.field_980, consumer);
	}

	public static LeafEntry.Builder<?> method_390(Identifier identifier) {
		return create((i, j, lootConditions, lootFunctions) -> new DynamicEntry(identifier, i, j, lootConditions, lootFunctions));
	}

	public static class Serializer extends LeafEntry.Serializer<DynamicEntry> {
		public Serializer() {
			super(new Identifier("dynamic"), DynamicEntry.class);
		}

		public void method_393(JsonObject jsonObject, DynamicEntry dynamicEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, dynamicEntry, jsonSerializationContext);
			jsonObject.addProperty("name", dynamicEntry.field_980.toString());
		}

		protected DynamicEntry method_392(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new DynamicEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
