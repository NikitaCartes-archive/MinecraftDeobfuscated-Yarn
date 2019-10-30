package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableEntry extends LeafEntry {
	private final Identifier id;

	private LootTableEntry(Identifier id, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(weight, quality, conditions, functions);
		this.id = id;
	}

	@Override
	public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
		LootTable lootTable = context.getSupplier(this.id);
		lootTable.drop(context, itemDropper);
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		if (lootTableReporter.hasSupplier(this.id)) {
			lootTableReporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(lootTableReporter);
			LootTable lootTable = lootTableReporter.getSupplier(this.id);
			if (lootTable == null) {
				lootTableReporter.report("Unknown loot table called " + this.id);
			} else {
				lootTable.check(lootTableReporter.withSupplier("->{" + this.id + "}", this.id));
			}
		}
	}

	public static LeafEntry.Builder<?> builder(Identifier id) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(id, weight, quality, conditions, functions));
	}

	public static class Serializer extends LeafEntry.Serializer<LootTableEntry> {
		public Serializer() {
			super(new Identifier("loot_table"), LootTableEntry.class);
		}

		public void method_431(JsonObject jsonObject, LootTableEntry lootTableEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, lootTableEntry, jsonSerializationContext);
			jsonObject.addProperty("name", lootTableEntry.id.toString());
		}

		protected LootTableEntry method_432(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new LootTableEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
