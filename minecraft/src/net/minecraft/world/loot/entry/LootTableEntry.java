package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;

public class LootTableEntry extends LeafEntry {
	private final Identifier id;

	private LootTableEntry(Identifier identifier, int i, int j, class_4570[] args, LootFunction[] lootFunctions) {
		super(i, j, args, lootFunctions);
		this.id = identifier;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		LootSupplier lootSupplier = lootContext.method_22556(this.id);
		lootSupplier.drop(lootContext, consumer);
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		if (lootTableReporter.method_22570(this.id)) {
			lootTableReporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(lootTableReporter);
			LootSupplier lootSupplier = lootTableReporter.method_22574(this.id);
			if (lootSupplier == null) {
				lootTableReporter.report("Unknown loot table called " + this.id);
			} else {
				lootSupplier.check(lootTableReporter.method_22569("->{" + this.id + "}", this.id));
			}
		}
	}

	public static LeafEntry.Builder<?> builder(Identifier identifier) {
		return builder((i, j, args, lootFunctions) -> new LootTableEntry(identifier, i, j, args, lootFunctions));
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
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_4570[] args, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new LootTableEntry(identifier, i, j, args, lootFunctions);
		}
	}
}
