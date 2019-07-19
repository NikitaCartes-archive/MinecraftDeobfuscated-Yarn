package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class LootTableEntry extends LeafEntry {
	private final Identifier id;

	private LootTableEntry(Identifier id, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(weight, quality, conditions, functions);
		this.id = id;
	}

	@Override
	public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
		LootTable lootTable = context.getLootManager().getSupplier(this.id);
		lootTable.drop(context, itemDropper);
	}

	@Override
	public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
		if (parentLootTables.contains(this.id)) {
			reporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(reporter, supplierGetter, parentLootTables, contextType);
			LootTable lootTable = (LootTable)supplierGetter.apply(this.id);
			if (lootTable == null) {
				reporter.report("Unknown loot table called " + this.id);
			} else {
				Set<Identifier> set = ImmutableSet.<Identifier>builder().addAll(parentLootTables).add(this.id).build();
				lootTable.check(reporter.makeChild("->{" + this.id + "}"), supplierGetter, set, contextType);
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
