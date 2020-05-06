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
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		LootTable lootTable = context.getSupplier(this.id);
		lootTable.generateUnprocessedLoot(context, lootConsumer);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		if (reporter.hasTable(this.id)) {
			reporter.report("Table " + this.id + " is recursively called");
		} else {
			super.validate(reporter);
			LootTable lootTable = reporter.getTable(this.id);
			if (lootTable == null) {
				reporter.report("Unknown loot table called " + this.id);
			} else {
				lootTable.validate(reporter.withTable("->{" + this.id + "}", this.id));
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

		public void toJson(JsonObject jsonObject, LootTableEntry lootTableEntry, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, lootTableEntry, jsonSerializationContext);
			jsonObject.addProperty("name", lootTableEntry.id.toString());
		}

		protected LootTableEntry fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new LootTableEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
